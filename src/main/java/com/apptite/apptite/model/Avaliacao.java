package com.apptite.apptite.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes") // Nome da tabela está bom
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Integer idAvaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", nullable = false) // ATUALIZADO
    private Restaurante restaurante; // ATUALIZADO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false) // ATUALIZADO (de usuario para cliente)
    private Cliente cliente; // ATUALIZADO

    @Column(name = "nota", nullable = false)
    private Integer nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;

    @Column(name = "resposta_restaurante", columnDefinition = "TEXT") // ATUALIZADO
    private String respostaRestaurante; // ATUALIZADO

    // Construtores
    public Avaliacao() {}

    // Getters e Setters
    public Integer getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(Integer idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public Restaurante getRestaurante() { // ATUALIZADO
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) { // ATUALIZADO
        this.restaurante = restaurante;
    }

    public Cliente getCliente() { // ATUALIZADO
        return cliente;
    }

    public void setCliente(Cliente cliente) { // ATUALIZADO
        this.cliente = cliente;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getRespostaRestaurante() { // ATUALIZADO
        return respostaRestaurante;
    }

    public void setRespostaRestaurante(String respostaRestaurante) { // ATUALIZADO
        this.respostaRestaurante = respostaRestaurante;
    }

    @PrePersist
    protected void onCreate() {
        this.dataAvaliacao = LocalDateTime.now();
    }
}