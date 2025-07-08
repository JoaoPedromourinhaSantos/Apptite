package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.RestauranteDTOs.*;
import com.apptite.apptite.dtos.LoginRequestDTO;
import com.apptite.apptite.model.GaleriaRestaurante; // Ou ImagemGaleriaRestaurante se renomeado
import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.service.RestauranteService;

import jakarta.validation.Valid;

// Importar @Valid para validações depois
// import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
// Para Exception Handler (depois)
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/restaurante")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarRestaurante(
            @Valid @RequestBody CriarRestauranteRequestDTO dto
    ) {
        try {
            Restaurante novoRestaurante = restauranteService.cadastrarRestaurante(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(RestauranteResponseDTO.fromEntity(novoRestaurante));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(/*@Valid*/ @RequestBody LoginRequestDTO dto) {
        Optional<Restaurante> optRestaurante = restauranteService.loginRestaurante(dto);
        if (optRestaurante.isPresent()) {
            return ResponseEntity.ok(RestauranteResponseDTO.fromEntity(optRestaurante.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha inválidos.");
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editarRestaurante(
            @PathVariable Integer id,
            /*@Valid*/ @ModelAttribute EditarRestauranteRequestDTO dto, // @ModelAttribute se multipart
            @RequestParam(value = "file", required = false) MultipartFile imagemCardapio,
            @RequestParam(value = "galeria", required = false) List<MultipartFile> arquivosGaleria) {
        try {
            Restaurante restauranteAtualizado = restauranteService.atualizarRestaurante(id, dto, imagemCardapio, arquivosGaleria);
            return ResponseEntity.ok(RestauranteResponseDTO.fromEntity(restauranteAtualizado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) { 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> getRestaurante(@PathVariable Integer id) {
        return restauranteService.getRestaurantePorId(id)
                .map(restaurante -> ResponseEntity.ok(RestauranteResponseDTO.fromEntity(restaurante)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/todos")
    public ResponseEntity<List<RestauranteResponseDTO>> listarTodosRestaurantes() {
        List<Restaurante> restaurantes = restauranteService.listarTodosRestaurantes();
        if (restaurantes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<RestauranteResponseDTO> responseDTOs = restaurantes.stream()
                .map(RestauranteResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/buscar-por-filtros")
public ResponseEntity<List<RestauranteResponseDTO>> buscarRestaurantesPorFiltros(@RequestParam("ids") List<Integer> filtroIds) {
    List<Restaurante> restaurantesEncontrados = restauranteService.buscarRestaurantesPorFiltros(filtroIds);

    if (restaurantesEncontrados.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    List<RestauranteResponseDTO> responseDTOs = restaurantesEncontrados.stream()
            .map(RestauranteResponseDTO::fromEntity)
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseDTOs);
}

@GetMapping("/buscar-por-todos-filtros")
public ResponseEntity<List<RestauranteResponseDTO>> buscarRestaurantesPorTodosFiltros(@RequestParam(required = false) List<Integer> ids) {
    List<Restaurante> restaurantesEncontrados = restauranteService.buscarRestaurantesPorTodosFiltros(ids);

    if (restaurantesEncontrados.isEmpty()) {
        return ResponseEntity.noContent().build();
    }

    List<RestauranteResponseDTO> responseDTOs = restaurantesEncontrados.stream()
            .map(RestauranteResponseDTO::fromEntity) // Usando o DTO que já corrigimos
            .collect(Collectors.toList());

    return ResponseEntity.ok(responseDTOs);
}

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> excluirRestaurante(@PathVariable Integer id) {
        try {
            restauranteService.excluirRestaurante(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) { // Ex: Restaurante não encontrado
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/galeria/imagem/{imagemId}")
    public ResponseEntity<byte[]> getImagemGaleria(@PathVariable Integer imagemId) {
        Optional<GaleriaRestaurante> optImagem = restauranteService.getImagemGaleriaPorId(imagemId);

        if (optImagem.isPresent()) {
            GaleriaRestaurante imagem = optImagem.get();
            // Assegure que sua entidade ImagemGaleria(Estabelecimento/Restaurante) tem getDadosImagem() e getTipoMimeImagem()
            if (imagem.getDadosImagem() != null && imagem.getTipoMimeImagem() != null) {
                HttpHeaders headers = new HttpHeaders();
                try {
                    headers.setContentType(MediaType.parseMediaType(imagem.getTipoMimeImagem()));
                } catch (Exception e) {
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Fallback
                }
                return ResponseEntity.ok().headers(headers).body(imagem.getDadosImagem());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    } 

    @GetMapping("/{id}/cardapio")
    public ResponseEntity<byte[]> getImagemCardapio(@PathVariable Integer id) {
        Optional<Restaurante> optRestaurante = restauranteService.getRestaurantePorId(id);

        if (optRestaurante.isPresent()) {
            Restaurante restaurante = optRestaurante.get();
            if (restaurante.getFotoCardapio() != null && restaurante.getFotoCardapioMimeType() != null) {
                HttpHeaders headers = new HttpHeaders();
                try {
                    headers.setContentType(MediaType.parseMediaType(restaurante.getFotoCardapioMimeType()));
                    return ResponseEntity.ok().headers(headers).body(restaurante.getFotoCardapio());
                } catch (Exception e) {
                    // Fallback para um tipo genérico se o MimeType for inválido
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    return ResponseEntity.ok().headers(headers).body(restaurante.getFotoCardapio());
                }
            }
        }
        return ResponseEntity.notFound().build();
    }
}