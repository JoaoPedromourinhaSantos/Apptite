package com.apptite.apptite.repository;

import com.apptite.apptite.model.Filtro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiltroRepository extends JpaRepository<Filtro, Integer> {
    // Você pode adicionar métodos de busca customizados aqui se precisar,
    // como findByNomeFiltro(String nomeFiltro);
}