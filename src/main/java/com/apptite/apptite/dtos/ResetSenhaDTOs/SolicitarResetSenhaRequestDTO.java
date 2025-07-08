package com.apptite.apptite.dtos.ResetSenhaDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SolicitarResetSenhaRequestDTO(
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Formato de email inválido.")
    String email,

    @NotBlank(message = "O tipo de conta é obrigatório (cliente ou restaurante).")
    @Pattern(regexp = "cliente|restaurante", message = "Tipo deve ser 'cliente' ou 'restaurante'.")
    String tipoConta
) {}