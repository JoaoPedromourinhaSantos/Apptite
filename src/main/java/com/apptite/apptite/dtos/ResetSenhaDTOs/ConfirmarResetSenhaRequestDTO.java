package com.apptite.apptite.dtos.ResetSenhaDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmarResetSenhaRequestDTO(
    @NotBlank(message = "O token é obrigatório.")
    String token,

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, max = 20, message = "A nova senha deve ter entre 6 e 20 caracteres.")
    String novaSenha,

    @NotBlank(message = "A confirmação da nova senha é obrigatória.")
    String confirmarNovaSenha
) {}