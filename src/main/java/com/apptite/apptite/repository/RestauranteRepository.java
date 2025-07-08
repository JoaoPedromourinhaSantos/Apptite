package com.apptite.apptite.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.apptite.apptite.model.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> { // Entidade atualizada
    Optional<Restaurante> findByEmailRestaurante(String emailRestaurante);

    @Query("SELECT DISTINCT r FROM Restaurante r JOIN r.filtros f WHERE f.idFiltro IN :filtroIds")
    List<Restaurante> buscarPorFiltros(@Param("filtroIds") List<Integer> filtroIds);

    @Query("SELECT r FROM Restaurante r JOIN r.filtros f WHERE f.idFiltro IN :filtroIds GROUP BY r HAVING COUNT(DISTINCT f.idFiltro) = :count")
    List<Restaurante> findByAllFiltros(@Param("filtroIds") List<Integer> filtroIds, @Param("count") Long count);

    @Query("SELECT DISTINCT r FROM Restaurante r LEFT JOIN FETCH r.imagensGaleria")
    List<Restaurante> findAllWithGaleria();
}