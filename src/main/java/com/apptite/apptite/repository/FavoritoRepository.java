package com.apptite.apptite.repository;

import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.model.Favorito;
import com.apptite.apptite.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {

    Optional<Favorito> findByClienteAndRestaurante(Cliente cliente, Restaurante restaurante);

    List<Favorito> findByCliente(Cliente cliente);

    // Métodos para exclusão quando um cliente ou restaurante for deletado
    void deleteByCliente(Cliente cliente);
    void deleteByRestaurante(Restaurante restaurante);

    @Query("SELECT COUNT(DISTINCT f.restaurante.id) FROM Favorito f")
    long countDistinctRestaurantesFavoritados();
}