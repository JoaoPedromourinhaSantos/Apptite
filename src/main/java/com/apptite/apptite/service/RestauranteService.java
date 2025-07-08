package com.apptite.apptite.service;

import com.apptite.apptite.dtos.RestauranteDTOs.*;
import com.apptite.apptite.dtos.LoginRequestDTO;
import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.model.Filtro;
import com.apptite.apptite.model.GaleriaRestaurante; // Mantém o nome da entidade da imagem por enquanto
import com.apptite.apptite.repository.RestauranteRepository;
import com.apptite.apptite.repository.ReservaRepository;
import com.apptite.apptite.repository.AvaliacaoRepository;
import com.apptite.apptite.repository.FavoritoRepository;
import com.apptite.apptite.repository.FiltroRepository;
import com.apptite.apptite.repository.HorarioFuncionamentoRepository;
import com.apptite.apptite.repository.GaleriaRestauranteRepository;
import com.apptite.apptite.repository.ResetSenhaRepository;
import com.apptite.apptite.util.UploadUtil; // Assumindo que ainda usa para fotoCardapio

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Para hashing de senha
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RestauranteService {

    @Autowired
    private FiltroRepository filtroRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    @Autowired
    private ResetSenhaRepository mudarSenhaTokenRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private GaleriaRestauranteRepository imagemGaleriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // UploadUtil pode ser injetado se for um bean, ou instanciado se for simples.
    // Para o exemplo, vou instanciar onde usado, mas @Autowired é melhor se for um @Component.
    // private UploadUtil uploadUtil = new UploadUtil();


    @Transactional
    public Restaurante cadastrarRestaurante(CriarRestauranteRequestDTO dto) {
        restauranteRepository.findByEmailRestaurante(dto.emailRestaurante()).ifPresent(r -> {
            throw new IllegalArgumentException("Email já cadastrado para outro restaurante.");
        });
        if (!dto.senhaRestaurante().equals(dto.confirmarSenhaRestaurante())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        String senhaHasheada = passwordEncoder.encode(dto.senhaRestaurante());

        Restaurante novoRestaurante = new Restaurante(
                dto.nomeRestaurante(),
                dto.cnpj(),
                dto.emailRestaurante(),
                senhaHasheada, // Senha hasheada
                dto.ruaEndereco(),
                dto.numeroEndereco(),
                dto.bairroEndereco(),
                dto.cidadeEndereco(),
                dto.estadoEndereco()
        );
        // Definir outros campos se existirem no DTO de criação (telefone, descrição etc.)
        // Ex: if (dto.telefoneRestaurante() != null) novoRestaurante.setTelefoneRestaurante(dto.telefoneRestaurante());

        return restauranteRepository.save(novoRestaurante);
    }

    public Optional<Restaurante> loginRestaurante(LoginRequestDTO dto) {
        Optional<Restaurante> optRestaurante = restauranteRepository.findByEmailRestaurante(dto.email());
        if (optRestaurante.isPresent()) {
            Restaurante restaurante = optRestaurante.get();
            if (passwordEncoder.matches(dto.senha(), restaurante.getSenhaRestaurante())) {
                return Optional.of(restaurante);
            }
        }
        return Optional.empty();
    }

    @Transactional
    public Restaurante atualizarRestaurante(
            Integer id,
            EditarRestauranteRequestDTO dto,
            MultipartFile imagemCardapio,
            List<MultipartFile> arquivosGaleria) {

        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com ID: " + id));

        // Atualizar campos básicos
        if (dto.nomeRestaurante() != null) restaurante.setNomeRestaurante(dto.nomeRestaurante());
        if (dto.emailRestaurante() != null) {
            // Validação de email único (se diferente do atual e já existe)
            restauranteRepository.findByEmailRestaurante(dto.emailRestaurante()).ifPresent(outroRestaurante -> {
                if (!outroRestaurante.getIdRestaurante().equals(id)) {
                    throw new IllegalArgumentException("O email fornecido já está em uso por outro restaurante.");
                }
            });
            restaurante.setEmailRestaurante(dto.emailRestaurante());
        }
        if (dto.telefoneRestaurante() != null) restaurante.setTelefoneRestaurante(dto.telefoneRestaurante());
        if (dto.senhaRestaurante() != null && !dto.senhaRestaurante().isEmpty()) {
            if (dto.confirmarSenhaRestaurante() == null || !dto.senhaRestaurante().equals(dto.confirmarSenhaRestaurante())) {
                throw new IllegalArgumentException("As senhas para atualização não coincidem!");
            }
            restaurante.setSenhaRestaurante(passwordEncoder.encode(dto.senhaRestaurante()));
        }
        if (dto.descricaoRestaurante() != null) restaurante.setDescricaoRestaurante(dto.descricaoRestaurante());
        if (dto.faixaPreco() != null) restaurante.setFaixaPreco(dto.faixaPreco());
        if (dto.cnpj() != null) restaurante.setCnpj(dto.cnpj()); // Adicionar validação de formato/unicidade se necessário
        if (dto.ruaEndereco() != null) restaurante.setRuaEndereco(dto.ruaEndereco());
        if (dto.bairroEndereco() != null) restaurante.setBairroEndereco(dto.bairroEndereco());
        if (dto.numeroEndereco() != null) restaurante.setNumeroEndereco(dto.numeroEndereco());
        if (dto.cidadeEndereco() != null) restaurante.setCidadeEndereco(dto.cidadeEndereco());
        if (dto.estadoEndereco() != null) restaurante.setEstadoEndereco(dto.estadoEndereco());
        // Processar dto.filtros() se necessário (converter de JSON string para o formato desejado)

        // Processar foto do cardápio
        if (imagemCardapio != null && !imagemCardapio.isEmpty()) {
            try {
                restaurante.setFotoCardapio(imagemCardapio.getBytes());
                restaurante.setFotoCardapioMimeType(imagemCardapio.getContentType());
            } catch (IOException e) {
                // É uma boa prática logar o erro e/ou lançar uma exceção específica
                throw new RuntimeException("Erro ao processar a imagem do cardápio.", e);
            }
        }

        // Lógica para galeria de imagens (bytes no banco) - como no seu EstabelecimentoService original
        List<GaleriaRestaurante> currentImages = new ArrayList<>(restaurante.getImagensGaleria());
        Set<Integer> idsToRetain = dto.imagensGaleriaParaManter() != null ?
                                   dto.imagensGaleriaParaManter().stream().collect(Collectors.toSet()) :
                                   java.util.Collections.emptySet();

        // Remover imagens que não estão na lista de retenção
        currentImages.stream()
            .filter(img -> !idsToRetain.contains(img.getId())) // Assumindo que ImagemGaleriaEstabelecimento tem getId()
            .collect(Collectors.toList()) // Coleta para evitar ConcurrentModificationException
            .forEach(restaurante::removeImagemGaleria); // Usa o método utilitário da entidade

        // Adicionar novas imagens
        if (arquivosGaleria != null && !arquivosGaleria.isEmpty()) {
            int currentMaxOrder = restaurante.getImagensGaleria().stream()
                .mapToInt(GaleriaRestaurante::getOrdem) // Assumindo getOrdem()
                .max()
                .orElse(0);
            int ordem = currentMaxOrder + 1;

            for (MultipartFile arquivoDaGaleria : arquivosGaleria) {
                if (arquivoDaGaleria != null && !arquivoDaGaleria.isEmpty()) {
                    try {
                        byte[] dadosImagem = arquivoDaGaleria.getBytes();
                        String tipoMime = arquivoDaGaleria.getContentType();
                        String nomeOriginal = arquivoDaGaleria.getOriginalFilename();

                        // Assumindo construtor em ImagemGaleriaEstabelecimento
                        GaleriaRestaurante novaImagem = new GaleriaRestaurante(
                                restaurante, // Associação
                                dadosImagem,
                                tipoMime,
                                nomeOriginal,
                                ordem++
                        );
                        restaurante.addImagemGaleria(novaImagem); // Usa o método utilitário da entidade
                    } catch (IOException e) {
                        e.printStackTrace(); // Logar melhor
                        throw new RuntimeException("Erro ao processar arquivo da galeria: " + arquivoDaGaleria.getOriginalFilename(), e);
                    }
                }
            }
        }

        if (dto.filtroIds() != null) {
        List<Filtro> novosFiltros = filtroRepository.findAllById(dto.filtroIds());
        restaurante.setFiltros(new HashSet<>(novosFiltros));

        }
           
        return restauranteRepository.save(restaurante);
    }

    public List<Restaurante> buscarRestaurantesPorFiltros(List<Integer> filtroIds) {
        if (filtroIds == null || filtroIds.isEmpty()) {
            // Se nenhuma ID de filtro for fornecida, retorna uma lista vazia.
            return new ArrayList<>();
        }
        System.out.println(filtroIds);
        return restauranteRepository.buscarPorFiltros(filtroIds);
    }

    @Transactional(readOnly = true)
    public Optional<Restaurante> getRestaurantePorId(Integer id) {
        return restauranteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Restaurante> listarTodosRestaurantes() {
    // Chama o novo método que garante o carregamento da galeria
    return restauranteRepository.findAllWithGaleria();
}

    @Transactional(readOnly = true) // Boa prática para métodos de busca
    public List<Restaurante> buscarRestaurantesPorTodosFiltros(List<Integer> filtroIds) {
        if (filtroIds == null || filtroIds.isEmpty()) {
            return restauranteRepository.findAll(); // Se nenhum filtro for selecionado, retorna todos
        }
        // O `count` precisa ser um Long para corresponder ao que a função COUNT() do JPQL retorna
        long count = filtroIds.size(); 
        return restauranteRepository.findByAllFiltros(filtroIds, count);
    }

    @Transactional
    public void excluirRestaurante(Integer id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado com ID: " + id));

        // A exclusão de imagens da galeria é tratada por orphanRemoval=true ao limpar a coleção
        // ou se você as remover explicitamente da coleção antes de salvar o restaurante.
        // Outras dependências:
        mudarSenhaTokenRepository.deleteByRestaurante(restaurante); // Precisa ser adaptado se MudarSenhaToken tem referência a Restaurante agora
        horarioFuncionamentoRepository.deleteByRestaurante(restaurante); // Idem
        avaliacaoRepository.deleteByRestaurante(restaurante);
        reservaRepository.deleteByRestaurante(restaurante);
        favoritoRepository.deleteByRestaurante(restaurante); // Idem
        // imagemGaleriaRepository.deleteByEstabelecimento(restaurante) não é mais necessário aqui
        // se orphanRemoval=true estiver funcionando corretamente na entidade Restaurante para imagensGaleria.

        restauranteRepository.delete(restaurante);
    }

    public Optional<GaleriaRestaurante> getImagemGaleriaPorId(Integer imagemId) {
        return imagemGaleriaRepository.findById(imagemId);
    }
}