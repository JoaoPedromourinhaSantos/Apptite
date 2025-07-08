package com.apptite.apptite.service;

import com.apptite.apptite.dtos.RestauranteDTOs.RestauranteResponseDTO; // Reutilizar o DTO existente
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.model.Favorito;
import com.apptite.apptite.model.Restaurante;
import com.apptite.apptite.repository.ClienteRepository;
import com.apptite.apptite.repository.FavoritoRepository;
import com.apptite.apptite.repository.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;

    @Transactional
    public boolean alternarFavorito(Integer idCliente, Integer idRestaurante) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado."));

        Optional<Favorito> favoritoExistente = favoritoRepository.findByClienteAndRestaurante(cliente, restaurante);

        if (favoritoExistente.isPresent()) {
            favoritoRepository.delete(favoritoExistente.get());
            return false; // Foi removido
        } else {
            favoritoRepository.save(new Favorito(cliente, restaurante));
            return true; // Foi adicionado
        }
    }

    public List<RestauranteResponseDTO> listarFavoritosPorCliente(Integer idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
        
        return favoritoRepository.findByCliente(cliente).stream()
                .map(favorito -> RestauranteResponseDTO.fromEntity(favorito.getRestaurante()))
                .collect(Collectors.toList());
    }
    
    public boolean verificarFavorito(Integer idCliente, Integer idRestaurante) {
         return clienteRepository.findById(idCliente).flatMap(cliente ->
            restauranteRepository.findById(idRestaurante).flatMap(restaurante ->
                favoritoRepository.findByClienteAndRestaurante(cliente, restaurante)
            )
        ).isPresent();
    }

    public double calcularPercentualRestaurantesFavoritados() {
        // 1. Conta o número total de restaurantes cadastrados no sistema.
        long totalRestaurantes = restauranteRepository.count();

        // Se não houver restaurantes, a taxa é 0 para evitar divisão por zero.
        if (totalRestaurantes == 0) {
            return 0.0;
        }

        // 2. Conta o número de restaurantes únicos que estão na tabela de favoritos.
        long restaurantesUnicosFavoritados = favoritoRepository.countDistinctRestaurantesFavoritados();

        // 3. Calcula e retorna o percentual.
        return ((double) restaurantesUnicosFavoritados / totalRestaurantes) * 100.0;
    }
}