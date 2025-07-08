package com.apptite.apptite.dtos.RestauranteDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CriarRestauranteRequestDTO(
    @NotBlank(message = "O nome do restaurante não pode estar em branco")
    @Size(min = 2, max = 50, message = "O nome do restaurante deve ter entre 2 e 50 caracteres")
    String nomeRestaurante,

    @NotBlank(message = "O CNPJ não pode estar em branco")
    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos")
    String cnpj,

    @NotBlank(message = "O email do restaurante não pode estar em branco")
    @Email(message = "Formato de email inválido")
    @Size(max = 50, message = "O email do restaurante não pode exceder 50 caracteres")
    String emailRestaurante,

    @NotBlank(message = "A senha não pode estar em branco")
    @Size(min = 6, max = 20, message = "A senha deve ter entre 6 e 20 caracteres")
    String senhaRestaurante,

    @NotBlank(message = "A confirmação da senha não pode estar em branco")
    String confirmarSenhaRestaurante,

    @NotBlank(message = "A rua não pode estar em branco")
    @Size(max = 50, message = "A rua não pode exceder 50 caracteres")
    String ruaEndereco,

    @NotBlank(message = "O número não pode estar em branco")
    @Size(max = 50, message = "O número não pode exceder 50 caracteres") // Ajuste o max se necessário
    String numeroEndereco,

    @NotBlank(message = "O bairro não pode estar em branco")
    @Size(max = 50, message = "O bairro não pode exceder 50 caracteres")
    String bairroEndereco,

    @NotBlank(message = "A cidade não pode estar em branco")
    @Size(max = 50, message = "A cidade não pode exceder 50 caracteres")
    String cidadeEndereco,

    @NotBlank(message = "O estado não pode estar em branco")
    @Size(min = 2, max = 2, message = "O estado deve ser a sigla com 2 caracteres (ex: MG)")
    String estadoEndereco
) {}