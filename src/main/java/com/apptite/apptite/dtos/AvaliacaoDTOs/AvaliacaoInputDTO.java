package com.apptite.apptite.dtos.AvaliacaoDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoInputDTO(
        @NotNull(message = "O ID do restaurante é obrigatório.")
        Integer idRestaurante,

        @NotNull(message = "A nota é obrigatória.")
        @Min(value = 1, message = "A nota mínima é 1.")
        @Max(value = 5, message = "A nota máxima é 5.")
        Integer nota,

        @Size(max = 1000, message = "O comentário não pode exceder 1000 caracteres.")
        String comentario
) {}