package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.ReservaDTOs.ReservaRequestDTO;
import com.apptite.apptite.dtos.ReservaDTOs.ReservaResponseDTO;
import com.apptite.apptite.model.StatusReserva;
import com.apptite.apptite.service.ReservaService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/reservas")
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarReserva(
            @RequestBody ReservaRequestDTO dto,
            @RequestParam Integer idCliente,
            @RequestParam Integer idRestaurante) {
        try {
            ReservaResponseDTO responseDTO = reservaService.solicitarReserva(dto, idCliente, idRestaurante);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            logger.error("Erro ao solicitar reserva: ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{idReserva}/aceitar")
    public ResponseEntity<?> aceitarReserva(@PathVariable Long idReserva, @RequestParam Integer idRestauranteLogado) {
        try {
            ReservaResponseDTO responseDTO = reservaService.aceitarReserva(idReserva, idRestauranteLogado);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Erro ao aceitar reserva {}:", idReserva, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{idReserva}/recusar")
    public ResponseEntity<?> recusarReserva(@PathVariable Long idReserva, @RequestParam Integer idRestauranteLogado) {
        try {
            ReservaResponseDTO responseDTO = reservaService.recusarReserva(idReserva, idRestauranteLogado);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Erro ao recusar reserva {}:", idReserva, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{idReserva}/cancelar-pelo-restaurante")
    public ResponseEntity<?> cancelarReservaPeloRestaurante(@PathVariable Long idReserva, @RequestParam Integer idRestauranteLogado) {
        try {
            reservaService.cancelarReservaPeloRestaurante(idReserva, idRestauranteLogado);
            return ResponseEntity.ok().body("Reserva cancelada com sucesso.");
        } catch (Exception e) {
            logger.error("Erro ao cancelar reserva {} pelo restaurante:", idReserva, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{idReserva}/compareceu")
    public ResponseEntity<?> marcarComoComparecida(@PathVariable Long idReserva, @RequestParam Integer idRestauranteLogado) {
        try {
            ReservaResponseDTO responseDTO = reservaService.marcarComoComparecida(idReserva, idRestauranteLogado);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Erro ao marcar comparecimento para reserva {}:", idReserva, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<List<ReservaResponseDTO>> getReservasPorStatus(Integer idRestaurante, List<StatusReserva> statusList) {
        try {
            List<ReservaResponseDTO> responseDTOs = reservaService.getReservasPorRestauranteEStatus(idRestaurante, statusList);
            if (responseDTOs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(responseDTOs);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/restaurante/{idRestaurante}/solicitadas")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasSolicitadas(@PathVariable Integer idRestaurante) {
        return getReservasPorStatus(idRestaurante, Arrays.asList(StatusReserva.SOLICITADA));
    }

    @GetMapping("/restaurante/{idRestaurante}/agendadas")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasAgendadas(@PathVariable Integer idRestaurante) {
        return getReservasPorStatus(idRestaurante, Arrays.asList(StatusReserva.AGENDADO));
    }

    @GetMapping("/restaurante/{idRestaurante}/recusadas")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasRecusadasECanceladas(@PathVariable Integer idRestaurante) {
        return getReservasPorStatus(idRestaurante, Arrays.asList(StatusReserva.RECUSADA, StatusReserva.CANCELADO));
    }

    @GetMapping("/restaurante/{idRestaurante}/passadas")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasPassadas(@PathVariable Integer idRestaurante) {
        return getReservasPorStatus(idRestaurante, Arrays.asList(StatusReserva.PASSADO));
    }

    @GetMapping("/restaurante/{idRestaurante}/finalizadas")
    public ResponseEntity<List<ReservaResponseDTO>> getReservasFinalizadas(@PathVariable Integer idRestaurante) {
        return getReservasPorStatus(idRestaurante, Arrays.asList(StatusReserva.COMPARECIDA));
    }

    @GetMapping("/indicadores/taxa-comparecimento/{idRestaurante}")
    public ResponseEntity<Map<String, Double>> getTaxaComparecimento(@PathVariable Integer idRestaurante) {
        try {
            double taxa = reservaService.calcularTaxaDeComparecimento(idRestaurante);
            // Retorna um JSON no formato: { "taxa": 85.5 }
            return ResponseEntity.ok(Map.of("taxa", taxa));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}