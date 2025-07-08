package com.apptite.apptite.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", nullable = false) // ATUALIZADO
    private Restaurante restaurante; // ATUALIZADO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false) // ATUALIZADO
    private Cliente cliente; // ATUALIZADO

    @Column(nullable = false)
    private LocalDate dataReserva;

    @Column(nullable = false)
    private LocalTime horarioChegada;

    @Column(nullable = false)
    private Integer quantidadePessoas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusReserva statusReserva;

    // Construtores
    public Reserva() {}

    public Reserva(Restaurante restaurante, Cliente cliente, LocalDate dataReserva, LocalTime horarioChegada, Integer quantidadePessoas, StatusReserva statusReserva) {
        this.restaurante = restaurante;
        this.cliente = cliente;
        this.dataReserva = dataReserva;
        this.horarioChegada = horarioChegada;
        this.quantidadePessoas = quantidadePessoas;
        this.statusReserva = statusReserva;
    }

    // Getters e Setters
    public Long getIdReserva() { return idReserva; }
    public void setIdReserva(Long idReserva) { this.idReserva = idReserva; }

    public Restaurante getRestaurante() { return restaurante; } // ATUALIZADO
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; } // ATUALIZADO

    public Cliente getCliente() { return cliente; } // ATUALIZADO
    public void setCliente(Cliente cliente) { this.cliente = cliente; } // ATUALIZADO

    public LocalDate getDataReserva() { return dataReserva; }
    public void setDataReserva(LocalDate dataReserva) { this.dataReserva = dataReserva; }

    public LocalTime getHorarioChegada() { return horarioChegada; }
    public void setHorarioChegada(LocalTime horarioChegada) { this.horarioChegada = horarioChegada; }

    public Integer getQuantidadePessoas() { return quantidadePessoas; }
    public void setQuantidadePessoas(Integer quantidadePessoas) { this.quantidadePessoas = quantidadePessoas; }

    public StatusReserva getStatusReserva() { return statusReserva; }
    public void setStatusReserva(StatusReserva statusReserva) { this.statusReserva = statusReserva; }
}