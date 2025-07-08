package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.AvaliacaoDTOs.*;
import com.apptite.apptite.service.AvaliacaoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/avaliacoes")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<?> criarAvaliacao(@RequestBody AvaliacaoInputDTO avaliacaoInputDTO,
                                            @RequestParam Integer idCliente /* Idealmente, viria do contexto de segurança */) {

        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cliente não autenticado.");
        }
        try {
            AvaliacaoOutputDTO savedAvaliacao = avaliacaoService.criarAvaliacao(avaliacaoInputDTO, idCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAvaliacao);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Logar a exceção
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar avaliação.");
        }
    }

    @GetMapping("/restaurante/{idRestaurante}") // ATUALIZADO
    public ResponseEntity<List<AvaliacaoOutputDTO>> getAvaliacoesPorRestaurante(@PathVariable Integer idRestaurante) {
        try {
            List<AvaliacaoOutputDTO> avaliacoes = avaliacaoService.getAvaliacoesPorRestaurante(idRestaurante);
            if (avaliacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(avaliacoes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Logar a exceção
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/cliente/{idCliente}") // ATUALIZADO
    public ResponseEntity<List<AvaliacaoOutputDTO>> getAvaliacoesPorCliente(@PathVariable Integer idCliente) {
         try {
            List<AvaliacaoOutputDTO> avaliacoes = avaliacaoService.getAvaliacoesPorCliente(idCliente);
            if (avaliacoes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(avaliacoes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Logar a exceção
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkClienteJaAvaliou( // ATUALIZADO
            @RequestParam Integer idRestaurante, // ATUALIZADO
            @RequestParam Integer idCliente /* Idealmente, viria do contexto de segurança */) {

        if (idCliente == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        boolean jaAvaliou = avaliacaoService.usuarioJaAvaliou(idRestaurante, idCliente);
        return ResponseEntity.ok(Map.of("jaAvaliou", jaAvaliou));
    }

    @PostMapping("/{idAvaliacao}/responder")
    public ResponseEntity<?> responderAvaliacao(
            @PathVariable Integer idAvaliacao,
            @RequestBody RespostaInputDTO respostaDTO,
            @RequestParam Integer idRestauranteLogado /* Idealmente, viria do contexto de segurança */) {

        if (idRestauranteLogado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Restaurante não autenticado.");
        }

        try {
            AvaliacaoOutputDTO avaliacaoRespondida = avaliacaoService.adicionarOuAtualizarResposta(idAvaliacao, respostaDTO, idRestauranteLogado);
            return ResponseEntity.ok(avaliacaoRespondida);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Logar o erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a resposta.");
        }
    }
    
    @GetMapping("/indicadores/taxa-resposta/{idRestaurante}")
    public ResponseEntity<Map<String, Double>> getTaxaDeResposta(@PathVariable Integer idRestaurante) {
        try {
            double taxa = avaliacaoService.calcularTaxaDeResposta(idRestaurante);
            // Retorna um JSON como: { "taxaDeResposta": 80.0 }
            return ResponseEntity.ok(Map.of("taxaDeResposta", taxa));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/indicadores/percentual-avaliacoes-negativas/{idRestaurante}")
    public ResponseEntity<Map<String, Double>> getPercentualAvaliacoesNegativas(@PathVariable Integer idRestaurante) {
        try {
            double percentual = avaliacaoService.calcularPercentualAvaliacoesNegativas(idRestaurante);
            return ResponseEntity.ok(Map.of("percentualAvaliacoesNegativas", percentual));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Logar a exceção
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}