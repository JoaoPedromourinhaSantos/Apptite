package com.apptite.apptite.service;

import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import com.apptite.apptite.model.Reserva;
import com.apptite.apptite.model.StatusReserva;
import com.apptite.apptite.dtos.ReservaDTOs.ReservaResponseDTO;
import com.apptite.apptite.dtos.ReservaDTOs.ReservaRequestDTO;
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.repository.RestauranteRepository; // ATUALIZADO
import com.apptite.apptite.repository.ReservaRepository;
import com.apptite.apptite.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private RestauranteRepository restauranteRepository; // ATUALIZADO

    @Autowired
    private ClienteRepository clienteRepository; // ATUALIZADO (nome da variável)

    @Transactional
    public ReservaResponseDTO solicitarReserva(ReservaRequestDTO dto, Integer idCliente, Integer idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + idCliente));

        LocalDate dataReserva = parseData(dto.getDataReserva());
        LocalTime horarioChegada = parseHorario(dto.getHorarioChegada());

        // Regras de negócio...
        validarDataReserva(dataReserva);
        if (dto.getQuantidadePessoas() == null || dto.getQuantidadePessoas() < 1) {
            throw new IllegalArgumentException("A quantidade de pessoas deve ser maior que zero.");
        }

        Reserva novaReserva = new Reserva(restaurante, cliente, dataReserva, horarioChegada, dto.getQuantidadePessoas(), StatusReserva.SOLICITADA);
        Reserva reservaSalva = reservaRepository.save(novaReserva);

        return ReservaResponseDTO.fromEntity(reservaSalva);
    }

    @Transactional
    public ReservaResponseDTO aceitarReserva(Long idReserva, Integer idRestauranteLogado) {
        Reserva reserva = buscarReservaEValidarPermissao(idReserva, idRestauranteLogado);
        if (reserva.getStatusReserva() != StatusReserva.SOLICITADA) {
            throw new IllegalStateException("Apenas reservas com status SOLICITADA podem ser aceitas.");
        }
        reserva.setStatusReserva(StatusReserva.AGENDADO);
        return ReservaResponseDTO.fromEntity(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO recusarReserva(Long idReserva, Integer idRestauranteLogado) {
        Reserva reserva = buscarReservaEValidarPermissao(idReserva, idRestauranteLogado);
        if (reserva.getStatusReserva() != StatusReserva.SOLICITADA) {
            throw new IllegalStateException("Apenas reservas com status SOLICITADA podem ser recusadas.");
        }
        reserva.setStatusReserva(StatusReserva.RECUSADA);
        return ReservaResponseDTO.fromEntity(reservaRepository.save(reserva));
    }

     @Transactional
    public void cancelarReservaPeloCliente(Long idReserva, Integer idClienteLogado) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada com ID: " + idReserva));

        if (!reserva.getCliente().getIdCliente().equals(idClienteLogado)) {
            throw new SecurityException("Usuário não autorizado a cancelar esta reserva.");
        }
        // Agora muda o status em vez de deletar
        reserva.setStatusReserva(StatusReserva.CANCELADO);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReservaPeloRestaurante(Long idReserva, Integer idRestauranteLogado) {
        Reserva reserva = buscarReservaEValidarPermissao(idReserva, idRestauranteLogado);
        // Agora muda o status em vez de deletar
        reserva.setStatusReserva(StatusReserva.CANCELADO);
        reservaRepository.save(reserva);
    }

    @Transactional
    public ReservaResponseDTO marcarComoComparecida(Long idReserva, Integer idRestauranteLogado) {
        Reserva reserva = buscarReservaEValidarPermissao(idReserva, idRestauranteLogado);

        if (reserva.getStatusReserva() != StatusReserva.PASSADO) {
            throw new IllegalStateException("Apenas reservas com status PASSADO podem ser marcadas como comparecidas.");
        }
        
        reserva.setStatusReserva(StatusReserva.COMPARECIDA);
        Reserva reservaSalva = reservaRepository.save(reserva);
        return ReservaResponseDTO.fromEntity(reservaSalva);
    }

    // Este método agora busca múltiplos status
    public List<ReservaResponseDTO> getReservasPorRestauranteEStatus(Integer idRestaurante, List<StatusReserva> statusList) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        atualizarReservasAntigasParaPassado(restaurante);

        // O repositório precisa de um novo método para buscar por uma lista de status
        return reservaRepository.findByRestauranteAndStatusReservaInOrderByDataReservaAscHorarioChegadaAsc(restaurante, statusList)
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<ReservaResponseDTO> getReservasPorCliente(Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com ID: " + idCliente));
        // Poderia ter uma lógica para atualizar status antes de listar também
        return reservaRepository.findByClienteOrderByDataReservaDescHorarioChegadaDesc(cliente)
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public double calcularTaxaDeComparecimento(Integer idRestaurante) {
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + idRestaurante));

        // Conta as reservas onde o cliente compareceu
        long comparecidas = reservaRepository.countByRestauranteAndStatusReserva(restaurante, StatusReserva.COMPARECIDA);

        // O total de reservas que deveriam ter acontecido (o universo relevante)
        // são aquelas que o cliente compareceu + aquelas que passaram da data e ele não veio.
        List<StatusReserva> statusRelevantes = Arrays.asList(StatusReserva.COMPARECIDA, StatusReserva.PASSADO);
        long totalReservasFinalizadas = reservaRepository.countByRestauranteAndStatusReservaIn(restaurante, statusRelevantes);

        // Evita divisão por zero
        if (totalReservasFinalizadas == 0) {
            return 0.0;
        }

        // Calcula e retorna o percentual
        return ((double) comparecidas / totalReservasFinalizadas) * 100.0;
    }

    // --- Métodos privados de ajuda ---

    private Reserva buscarReservaEValidarPermissao(Long idReserva, Integer idRestaurante) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new EntityNotFoundException("Reserva não encontrada com ID: " + idReserva));
        if (!reserva.getRestaurante().getIdRestaurante().equals(idRestaurante)) {
            throw new SecurityException("Este restaurante não tem permissão para modificar esta reserva.");
        }
        return reserva;
    }

    private void validarStatusParaCancelamento(StatusReserva status) {
        if (status != StatusReserva.SOLICITADA && status != StatusReserva.AGENDADO) {
            throw new IllegalStateException("Reserva não pode ser cancelada neste status: " + status);
        }
    }

    @Transactional
    private void atualizarReservasAntigasParaPassado(Restaurante restaurante) {
        String agoraFormatado = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        List<StatusReserva> statusesParaVerificar = Arrays.asList(StatusReserva.AGENDADO, StatusReserva.SOLICITADA);
        
        List<Reserva> reservasParaAtualizar = reservaRepository.findReservasParaAtualizarParaPassado(
            restaurante, statusesParaVerificar, agoraFormatado
        );
        reservasParaAtualizar.forEach(reserva -> reserva.setStatusReserva(StatusReserva.PASSADO));
        reservaRepository.saveAll(reservasParaAtualizar); // Salva todas as alterações em lote
    }

    private LocalDate parseData(String dataStr) {
        try {
            return LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido. Use AAAA-MM-DD.");
        }
    }

    private LocalTime parseHorario(String horarioStr) {
        try {
            // Tenta o formato mais completo primeiro
            return LocalTime.parse(horarioStr, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException e1) {
            try {
                // Tenta o formato mais simples
                return LocalTime.parse(horarioStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException("Formato de horário inválido. Use HH:mm ou HH:mm:ss.");
            }
        }
    }
    
    private void validarDataReserva(LocalDate dataReserva) {
        LocalDate hoje = LocalDate.now();
        if (dataReserva.isBefore(hoje.plusDays(1))) {
            throw new IllegalArgumentException("A data da reserva deve ser no futuro.");
        }
        if (dataReserva.isAfter(hoje.plusDays(14))) { // Exemplo de regra: no máximo 14 dias de antecedência
            throw new IllegalArgumentException("A reserva não pode ser feita com mais de 14 dias de antecedência.");
        }
    }
}