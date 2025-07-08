package com.apptite.apptite.repository;

import com.apptite.apptite.model.GaleriaRestaurante; // ATUALIZADO
import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GaleriaRestauranteRepository extends JpaRepository<GaleriaRestaurante, Integer> { // ATUALIZADO

    void deleteByRestaurante(Restaurante restaurante); // ATUALIZADO
}