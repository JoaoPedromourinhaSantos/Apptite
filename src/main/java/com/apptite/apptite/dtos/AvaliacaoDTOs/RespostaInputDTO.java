package com.apptite.apptite.dtos.AvaliacaoDTOs; // Usando um subpacote para organização

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RespostaInputDTO(
    @NotBlank(message = "O texto da resposta não pode estar em branco.")
    @Size(max = 1000, message = "A resposta não pode exceder 1000 caracteres.")
    String textoResposta
) {}