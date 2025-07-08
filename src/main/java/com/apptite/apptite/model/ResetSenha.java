package com.apptite.apptite.model; // Certifique-se que o pacote está correto

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class ResetSenha { // Nome da classe atualizado

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    // Construtores, Getters e Setters
    public ResetSenha() {}

    public ResetSenha(String token, LocalDateTime expiration, Cliente cliente, Restaurante restaurante) {
        this.token = token;
        this.expiration = expiration;
        this.cliente = cliente;
        this.restaurante = restaurante;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiration() { return expiration; }
    public void setExpiration(LocalDateTime expiration) { this.expiration = expiration; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }
}