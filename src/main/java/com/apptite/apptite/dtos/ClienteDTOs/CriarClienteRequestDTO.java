package com.apptite.apptite.dtos.ClienteDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarClienteRequestDTO(
    @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres")
    String nomeCliente,

    @NotBlank(message = "O email não pode estar em branco")
    @Email(message = "Formato de email inválido")
    @Size(max = 50, message = "O email não pode exceder 50 caracteres")
    String emailCliente,

    @NotBlank(message = "A senha não pode estar em branco")
    @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres") // Ajuste max se necessário (para a senha antes do hash)
    String senhaCliente,

    @NotBlank(message = "A confirmação da senha não pode estar em branco")
    String confirmarSenhaCliente
) {}