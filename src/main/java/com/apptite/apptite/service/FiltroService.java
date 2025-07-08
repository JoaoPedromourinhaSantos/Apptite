package com.apptite.apptite.service;

import com.apptite.apptite.model.Filtro;
import com.apptite.apptite.repository.FiltroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FiltroService {

    @Autowired
    private FiltroRepository filtroRepository;

    /**
     * Busca todos os filtros disponíveis no banco de dados.
     * @return Uma lista de todas as entidades Filtro.
     */
    public List<Filtro> listarTodos() {
        return filtroRepository.findAll();
    }
}