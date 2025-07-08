package com.apptite.apptite.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Filtro")
public class Filtro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFiltro")
    private Integer idFiltro;

    @Column(name = "nomeFiltro", nullable = false, unique = true, length = 50)
    private String nomeFiltro;

    // O 'mappedBy' indica que a relação é gerenciada pela entidade Restaurante
    @ManyToMany(mappedBy = "filtros", fetch = FetchType.LAZY)
    @JsonIgnore // Evita serialização infinita ao consultar filtros
    private Set<Restaurante> restaurantes = new HashSet<>();

    // Construtores
    public Filtro() {}

    public Filtro(String nomeFiltro) {
        this.nomeFiltro = nomeFiltro;
    }

    // Getters e Setters
    public Integer getIdFiltro() {
        return idFiltro;
    }

    public void setIdFiltro(Integer idFiltro) {
        this.idFiltro = idFiltro;
    }

    public String getNomeFiltro() {
        return nomeFiltro;
    }

    public void setNomeFiltro(String nomeFiltro) {
        this.nomeFiltro = nomeFiltro;
    }

    public Set<Restaurante> getRestaurantes() {
        return restaurantes;
    }

    public void setRestaurantes(Set<Restaurante> restaurantes) {
        this.restaurantes = restaurantes;
    }
}