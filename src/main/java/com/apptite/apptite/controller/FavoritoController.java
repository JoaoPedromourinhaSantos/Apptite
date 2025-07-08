package com.apptite.apptite.controller;

import com.apptite.apptite.dtos.FavoritoDTOs.FavoritoRequestDTO;
import com.apptite.apptite.dtos.RestauranteDTOs.RestauranteResponseDTO;
import com.apptite.apptite.service.FavoritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping("/alternar")
    public ResponseEntity<Map<String, Boolean>> alternarFavorito(@Valid @RequestBody FavoritoRequestDTO requestDTO) {
        boolean isFavorito = favoritoService.alternarFavorito(requestDTO.idCliente(), requestDTO.idRestaurante());
        return ResponseEntity.ok(Map.of("isFavorito", isFavorito));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<RestauranteResponseDTO>> listarFavoritos(@PathVariable Integer idCliente) {
        List<RestauranteResponseDTO> favoritos = favoritoService.listarFavoritosPorCliente(idCliente);
        if (favoritos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(favoritos);
    }
    
    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Boolean>> verificarFavorito(@RequestParam Integer idCliente, @RequestParam Integer idRestaurante) {
        boolean isFavorito = favoritoService.verificarFavorito(idCliente, idRestaurante);
        return ResponseEntity.ok(Map.of("isFavorito", isFavorito));
    }

    @GetMapping("/indicador/percentual-engajamento")
    public ResponseEntity<Map<String, Object>> getPercentualEngajamentoFavoritos() {
        double percentual = favoritoService.calcularPercentualRestaurantesFavoritados();
        
        Map<String, Object> response = Map.of(
            "indicador", "Percentual de Restaurantes Favoritados",
            "valorPercentual", percentual
        );
        
        return ResponseEntity.ok(response);
    }
}