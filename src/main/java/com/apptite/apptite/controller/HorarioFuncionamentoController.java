package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.HorarioFuncionamentoDTOs.*;
import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.service.HorarioFuncionamentoService;
import com.apptite.apptite.service.RestauranteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/horarios") // Mantendo o endpoint base
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
public class HorarioFuncionamentoController {

    private final HorarioFuncionamentoService horarioFuncionamentoService;
    private final RestauranteService restauranteService; // Injetar RestauranteService

    public HorarioFuncionamentoController(HorarioFuncionamentoService service,
                                          RestauranteService restauranteService) {
        this.horarioFuncionamentoService = service;
        this.restauranteService = restauranteService;
    }

    @GetMapping("/restaurante/{idRestaurante}") // Endpoint mais RESTful para listar por restaurante
    public ResponseEntity<List<HorarioFuncionamentoResponseDTO>> listarPorRestaurante(@PathVariable Integer idRestaurante) {
        Optional<Restaurante> optRestaurante = restauranteService.getRestaurantePorId(idRestaurante);
        if (optRestaurante.isEmpty()) {
            return ResponseEntity.notFound().build(); // Restaurante não encontrado
        }

        List<HorarioFuncionamentoResponseDTO> responseDTOs =
                horarioFuncionamentoService.listarPorRestaurante(optRestaurante.get()).stream()
                        .map(HorarioFuncionamentoResponseDTO::fromEntity)
                        .collect(Collectors.toList());

        if (responseDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/restaurante/{idRestaurante}") // Endpoint mais RESTful para atualizar por restaurante
    public ResponseEntity<?> atualizarTodos(
            @PathVariable Integer idRestaurante,
            @RequestBody List<HorarioFuncionamentoRequestDTO> novosHorariosDTO) { // Usar @Valid aqui no futuro
        Optional<Restaurante> optRestaurante = restauranteService.getRestaurantePorId(idRestaurante);
        if (optRestaurante.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Restaurante não encontrado.");
        }

        try {
            horarioFuncionamentoService.atualizarHorariosParaRestaurante(optRestaurante.get(), novosHorariosDTO);
            return ResponseEntity.ok().body("Horários atualizados com sucesso.");
        } catch (Exception e) {
            // Adicionar log aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar horários: " + e.getMessage());
        }
    }

}