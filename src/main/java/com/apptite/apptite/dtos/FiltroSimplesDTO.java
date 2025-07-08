package com.apptite.apptite.dtos;

import com.apptite.apptite.model.Filtro;

public record FiltroSimplesDTO(Integer idFiltro, String nomeFiltro) {
    public static FiltroSimplesDTO fromEntity(Filtro filtro) {
        return new FiltroSimplesDTO(filtro.getIdFiltro(), filtro.getNomeFiltro());
    }
}