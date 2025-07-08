package com.apptite.apptite.dtos.RestauranteDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record EditarRestauranteRequestDTO(
    @Size(min = 2, max = 50, message = "O nome do restaurante deve ter entre 2 e 50 caracteres, se fornecido.")
    String nomeRestaurante,

    @Email(message = "Formato de email inválido, se fornecido.")
    @Size(max = 50, message = "O email não pode exceder 50 caracteres, se fornecido.")
    String emailRestaurante,

    @Size(min = 10, max = 20, message = "O telefone deve ter entre 10 e 20 caracteres, se fornecido.") // Ex: (XX)XXXXX-XXXX
    String telefoneRestaurante,

    @Size(min = 6, max = 20, message = "A nova senha deve ter entre 6 e 20 caracteres, se fornecida.")
    String senhaRestaurante,

    String confirmarSenhaRestaurante,

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres, se fornecida.")
    String descricaoRestaurante,

    @Size(max = 20, message = "A faixa de preço não pode exceder 20 caracteres, se fornecida.") // Ex: "$$", "R$10-R$20"
    String faixaPreco,

    @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter 14 dígitos numéricos, se fornecido.")
    String cnpj,

    @Size(max = 50, message = "A rua não pode exceder 50 caracteres, se fornecida.")
    String ruaEndereco,

    @Size(max = 50, message = "O bairro não pode exceder 50 caracteres, se fornecida.")
    String bairroEndereco,

    @Size(max = 50, message = "O número não pode exceder 50 caracteres, se fornecido.")
    String numeroEndereco,

    @Size(max = 50, message = "A cidade não pode exceder 50 caracteres, se fornecida.")
    String cidadeEndereco,

    @Size(min = 2, max = 2, message = "O estado deve ser a sigla com 2 caracteres, se fornecido.")
    String estadoEndereco,

    String filtros, // A validação para 'filtros' dependerá do seu formato esperado (ex: @Pattern se for JSON específico)
    List<Integer> imagensGaleriaParaManter,

     List<Integer> filtroIds
) {}