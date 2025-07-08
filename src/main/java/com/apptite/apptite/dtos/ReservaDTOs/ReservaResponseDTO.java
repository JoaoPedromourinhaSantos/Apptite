package com.apptite.apptite.dtos.ReservaDTOs;

import com.apptite.apptite.model.Reserva;
import com.apptite.apptite.model.StatusReserva;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponseDTO(
    Long idReserva,
    Integer idRestaurante, // ATUALIZADO
    String nomeRestaurante, // ATUALIZADO
    Integer idCliente, // ATUALIZADO
    String nomeCliente, // ATUALIZADO
    LocalDate dataReserva,
    LocalTime horarioChegada,
    Integer quantidadePessoas,
    StatusReserva statusReserva
) {
    public static ReservaResponseDTO fromEntity(Reserva reserva) {
        if (reserva == null) {
            return null;
        }
        return new ReservaResponseDTO(
            reserva.getIdReserva(),
            reserva.getRestaurante().getIdRestaurante(),
            reserva.getRestaurante().getNomeRestaurante(),
            reserva.getCliente().getIdCliente(),
            reserva.getCliente().getNomeCliente(), // Assumindo que o nome está disponível
            reserva.getDataReserva(),
            reserva.getHorarioChegada(),
            reserva.getQuantidadePessoas(),
            reserva.getStatusReserva()
        );
    }
}