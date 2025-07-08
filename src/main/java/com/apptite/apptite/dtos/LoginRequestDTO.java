package com.apptite.apptite.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    String email,

    @NotBlank(message = "A senha não pode estar em branco")
    String senha
) {}