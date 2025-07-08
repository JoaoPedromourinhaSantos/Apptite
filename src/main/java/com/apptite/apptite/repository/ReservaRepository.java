package com.apptite.apptite.repository;

import com.apptite.apptite.model.Reserva;
import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import com.apptite.apptite.model.Cliente;
import com.apptite.apptite.model.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByRestauranteAndStatusReservaOrderByDataReservaAscHorarioChegadaAsc( // ATUALIZADO
            Restaurante restaurante, StatusReserva statusReserva
    );

    List<Reserva> findByRestauranteAndStatusReservaInOrderByDataReservaAscHorarioChegadaAsc(
        Restaurante restaurante, List<StatusReserva> statuses
);

    List<Reserva> findByClienteOrderByDataReservaDescHorarioChegadaDesc(Cliente cliente); // ATUALIZADO

    @Query("SELECT r FROM Reserva r WHERE r.restaurante = :restaurante " + // ATUALIZADO
           "AND r.statusReserva IN :statusesParaVerificar " +
           "AND CONCAT(r.dataReserva, 'T', r.horarioChegada) < :agora")
    List<Reserva> findReservasParaAtualizarParaPassado(
            @Param("restaurante") Restaurante restaurante, // ATUALIZADO
            @Param("statusesParaVerificar") List<StatusReserva> statusesParaVerificar,
            @Param("agora") String agora
    );

    void deleteByCliente(Cliente cliente);
    void deleteByRestaurante(Restaurante restaurante);

    /**
     * Conta quantas reservas um restaurante tem com um status específico.
     * Ex: Contar todas as reservas 'COMPARECIDA'.
     */
    long countByRestauranteAndStatusReserva(Restaurante restaurante, StatusReserva status);

    /**
     * Conta quantas reservas um restaurante tem com status que estão DENTRO de uma lista.
     * Ex: Contar todas as reservas que são 'COMPARECIDA' OU 'PASSADO'.
     */
    long countByRestauranteAndStatusReservaIn(Restaurante restaurante, List<StatusReserva> statuses);
}