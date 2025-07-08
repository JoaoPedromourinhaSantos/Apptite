package com.apptite.apptite.controller;

import com.apptite.apptite.model.Filtro;
import com.apptite.apptite.service.FiltroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080"}, allowCredentials = "true")
@RequestMapping("/filtros")
public class FiltroController {

    @Autowired
    private FiltroService filtroService;

    /**
     * Endpoint para listar todos os filtros cadastrados manualmente no banco.
     * O frontend usará este endpoint para exibir as opções de filtro para o restaurante.
     * @return ResponseEntity contendo a lista de todos os filtros.
     */
    @GetMapping
    public ResponseEntity<List<Filtro>> listarTodosFiltros() {
        List<Filtro> filtros = filtroService.listarTodos();
        return ResponseEntity.ok(filtros);
    }
}