package com.apptite.apptite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "favoritos", // Nome da tabela simplificado
        uniqueConstraints = @UniqueConstraint(columnNames = {"cliente_id", "restaurante_id"})) // Constraint para não favoritar duas vezes
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente; // ATUALIZADO de Usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false) // ATUALIZADO
    private Restaurante restaurante; // ATUALIZADO de Estabelecimento

    public Favorito() {}

    public Favorito(Cliente cliente, Restaurante restaurante) {
        this.cliente = cliente;
        this.restaurante = restaurante;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }
}