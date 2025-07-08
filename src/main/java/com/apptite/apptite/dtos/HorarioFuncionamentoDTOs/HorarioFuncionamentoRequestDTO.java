package com.apptite.apptite.dtos.HorarioFuncionamentoDTOs; // Ou um subpacote como com.apptite.apptite.dtos.HorarioDTOs

// Adicionaremos validações depois
public record HorarioFuncionamentoRequestDTO(
    Integer idHorario, // Opcional, usado para identificar um horário existente ao atualizar
    String diaSemana,  // Ex: "Segunda-feira", "Terça-feira"
    String horaInicio, // Formato "HH:mm"
    String horaFim,    // Formato "HH:mm"
    boolean ativo      // true se o horário estiver ativo para o dia
) {}