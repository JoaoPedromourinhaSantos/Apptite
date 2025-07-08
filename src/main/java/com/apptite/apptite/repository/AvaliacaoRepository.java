package com.apptite.apptite.repository;

import com.apptite.apptite.model.Avaliacao;
import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import com.apptite.apptite.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {

    // Nomes dos métodos atualizados para seguir os novos nomes de campos na entidade Avaliacao
    List<Avaliacao> findByRestauranteIdRestauranteOrderByDataAvaliacaoDesc(Integer idRestaurante);

    List<Avaliacao> findByClienteIdClienteOrderByDataAvaliacaoDesc(Integer idCliente);

    Optional<Avaliacao> findByRestauranteIdRestauranteAndClienteIdCliente(Integer idRestaurante, Integer idCliente);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.restaurante.idRestaurante = :idRestaurante") // ATUALIZADO
    Double findAverageNotaByRestauranteId(Integer idRestaurante);

    // Métodos para exclusão em cascata (usados pelos services de Cliente e Restaurante)
    void deleteByCliente(Cliente cliente);

    void deleteByRestaurante(Restaurante restaurante); // ATUALIZADO


    long countByRestaurante(Restaurante restaurante);

    // Adicionado para contar avaliações negativas (nota < 3)
    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.restaurante.idRestaurante = :idRestaurante AND a.nota < 3")
    long countNegativeReviewsByRestauranteId(Integer idRestaurante);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.restaurante = :restaurante AND a.respostaRestaurante IS NOT NULL AND a.respostaRestaurante != ''")
    long countRespostasByRestaurante(@Param("restaurante") Restaurante restaurante);
}