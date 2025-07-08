package com.apptite.apptite.dtos.ReservaDTOs;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReservaRequestDTO {

    @NotBlank(message = "A data da reserva é obrigatória.")
    // Valida se a string está no formato YYYY-MM-DD
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Formato de data inválido. Use AAAA-MM-DD.")
    private String dataReserva;

    @NotBlank(message = "O horário de chegada é obrigatório.")
    // Valida se a string está no formato HH:MM ou HH:MM:SS
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)(?::([0-5]\\d))?$", message = "Formato de horário inválido. Use HH:MM ou HH:MM:SS.")
    private String horarioChegada;

    @NotNull(message = "A quantidade de pessoas é obrigatória.")
    @Min(value = 1, message = "A quantidade de pessoas deve ser no mínimo 1.")
    private Integer quantidadePessoas;

    // Getters e Setters
    public String getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(String dataReserva) {
        this.dataReserva = dataReserva;
    }

    public String getHorarioChegada() {
        return horarioChegada;
    }

    public void setHorarioChegada(String horarioChegada) {
        this.horarioChegada = horarioChegada;
    }

    public Integer getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }
}