// NOVO AvaliacaoService.java (COMPLETO)
package com.apptite.apptite.service;

import com.apptite.apptite.dtos.AvaliacaoDTOs.*;
import com.apptite.apptite.model.Avaliacao;
import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.repository.AvaliacaoRepository;
import com.apptite.apptite.repository.RestauranteRepository;
import com.apptite.apptite.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public AvaliacaoOutputDTO criarAvaliacao(AvaliacaoInputDTO dto, Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + idCliente));

        Restaurante restaurante = restauranteRepository.findById(dto.idRestaurante())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + dto.idRestaurante()));

        // Verifica se o usuário já avaliou este restaurante
        if (usuarioJaAvaliou(dto.idRestaurante(), idCliente)) {
            throw new IllegalStateException("Cliente já avaliou este restaurante.");
        }

        if (dto.nota() < 1 || dto.nota() > 5) {
            throw new IllegalArgumentException("A nota da avaliação deve ser entre 1 e 5.");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setCliente(cliente);
        avaliacao.setRestaurante(restaurante);
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        Avaliacao savedAvaliacao = avaliacaoRepository.save(avaliacao);

        // Atualiza a nota média geral do restaurante
        atualizarAvaliacaoGeralRestaurante(dto.idRestaurante());

        return AvaliacaoOutputDTO.fromEntity(savedAvaliacao);
    }




    public List<AvaliacaoOutputDTO> getAvaliacoesPorRestaurante(Integer idRestaurante) {
        if (!restauranteRepository.existsById(idRestaurante)) {
             throw new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante);
        }
        return avaliacaoRepository.findByRestauranteIdRestauranteOrderByDataAvaliacaoDesc(idRestaurante)
                .stream()
                .map(AvaliacaoOutputDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AvaliacaoOutputDTO> getAvaliacoesPorCliente(Integer idCliente) {
         if (!clienteRepository.existsById(idCliente)) {
             throw new EntityNotFoundException("Cliente não encontrado com ID: " + idCliente);
        }
        return avaliacaoRepository.findByClienteIdClienteOrderByDataAvaliacaoDesc(idCliente)
                .stream()
                .map(AvaliacaoOutputDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean usuarioJaAvaliou(Integer idRestaurante, Integer idCliente) {
        return avaliacaoRepository.findByRestauranteIdRestauranteAndClienteIdCliente(idRestaurante, idCliente).isPresent();
    }




    @Transactional
    public AvaliacaoOutputDTO adicionarOuAtualizarResposta(Integer idAvaliacao, RespostaInputDTO respostaDTO, Integer idRestauranteLogado) throws AccessDeniedException {
        Avaliacao avaliacao = avaliacaoRepository.findById(idAvaliacao)
                .orElseThrow(() -> new EntityNotFoundException("Avaliação não encontrada com ID: " + idAvaliacao));

        // Verifica se o restaurante logado é o dono da avaliação
        if (!avaliacao.getRestaurante().getIdRestaurante().equals(idRestauranteLogado)) {
            throw new AccessDeniedException("Você não tem permissão para responder a esta avaliação.");
        }

        avaliacao.setRespostaRestaurante(respostaDTO.textoResposta());
        Avaliacao avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);
        return AvaliacaoOutputDTO.fromEntity(avaliacaoAtualizada);
    }




    @Transactional
    private void atualizarAvaliacaoGeralRestaurante(Integer idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        Double mediaNotas = avaliacaoRepository.findAverageNotaByRestauranteId(idRestaurante);

        restaurante.setAvaliacaoGeral(mediaNotas != null ? mediaNotas.floatValue() : 0.0f);
        restauranteRepository.save(restaurante);
    }




    public double calcularTaxaDeResposta(Integer idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        long totalAvaliacoes = avaliacaoRepository.countByRestaurante(restaurante);

        if (totalAvaliacoes == 0) {
            return 0.0;
        }

        long avaliacoesRespondidas = avaliacaoRepository.countRespostasByRestaurante(restaurante);

        return ((double) avaliacoesRespondidas / totalAvaliacoes) * 100.0;
    }

    

    // ESTE É O MÉTODO QUE ESTAVA FALTANDO OU FOI REMOVIDO!
    public double calcularPercentualAvaliacoesNegativas(Integer idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        long totalAvaliacoes = avaliacaoRepository.countByRestaurante(restaurante);
        if (totalAvaliacoes == 0) {
            return 0.0;
        }

        long avaliacoesNegativas = avaliacaoRepository.countNegativeReviewsByRestauranteId(idRestaurante);
        return ((double) avaliacoesNegativas / totalAvaliacoes) * 100.0;
    }
}