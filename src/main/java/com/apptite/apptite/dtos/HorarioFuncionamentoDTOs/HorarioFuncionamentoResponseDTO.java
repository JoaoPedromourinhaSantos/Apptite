package com.apptite.apptite.dtos.HorarioFuncionamentoDTOs; // Ou um subpacote

import com.apptite.apptite.model.HorarioFuncionamento;

public record HorarioFuncionamentoResponseDTO(
    Integer idHorario,
    String diaSemana,
    String horaInicio,
    String horaFim,
    boolean ativo,
    Integer idRestaurante
) {
    public static HorarioFuncionamentoResponseDTO fromEntity(HorarioFuncionamento horario) {
        if (horario == null) return null;
        return new HorarioFuncionamentoResponseDTO(
            horario.getIdHorario(),
            horario.getDiaSemana(),
            horario.getHoraInicio(),
            horario.getHoraFim(),
            horario.isAtivo(),
            horario.getRestaurante() != null ? horario.getRestaurante().getIdRestaurante() : null
        );
    }
}