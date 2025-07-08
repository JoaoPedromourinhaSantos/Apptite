package com.apptite.apptite.dtos.ClienteDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


public record EditarClienteRequestDTO(
    @Size(min = 2, max = 50, message = "O nome deve ter entre 2 e 50 caracteres, se fornecido.")
    String nomeCliente,

    @Email(message = "Formato de email inválido, se fornecido.")
    @Size(max = 50, message = "O email não pode exceder 50 caracteres, se fornecido.")
    String emailCliente,

    @Size(min = 8, max = 20, message = "O telefone deve ter entre 8 e 20 caracteres, se fornecido.")
    String telefoneCliente,

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres, se fornecida.")
    String descricaoCliente,

    @Size(min = 6, max = 20, message = "A nova senha deve ter entre 6 e 20 caracteres, se fornecida.")
    String senhaCliente,

    String confirmarSenhaCliente
) {}