package com.apptite.apptite.model;

import jakarta.persistence.*;

@Entity
@Table(name = HorarioFuncionamento.TABLE_NAME)
public class HorarioFuncionamento {

    public static final String TABLE_NAME = "HorarioFuncionamento";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHorario")
    private Integer idHorario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRestaurante", nullable = false) // ATUALIZADO de idEstabelecimento
    private Restaurante restaurante; // ATUALIZADO de Estabelecimento

    @Column(name = "diaSemana", length = 20, nullable = false) // Aumentado o length para "Segunda-feira"
    private String diaSemana;

    @Column(name = "horaInicio", length = 5, nullable = false)
    private String horaInicio;

    @Column(name = "horaFim", length = 5, nullable = false)
    private String horaFim;

    @Column(name = "ativo", nullable = false)
    private boolean ativo;

    public HorarioFuncionamento() {}

    // Construtor atualizado para receber Restaurante
    public HorarioFuncionamento(Restaurante restaurante, String diaSemana, String horaInicio, String horaFim, boolean ativo) {
        this.restaurante = restaurante;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.ativo = ativo;
    }

    // Getters e setters atualizados
    public Integer getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    public Restaurante getRestaurante() { // ATUALIZADO
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) { // ATUALIZADO
        this.restaurante = restaurante;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}