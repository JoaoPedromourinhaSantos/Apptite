package com.apptite.apptite.repository;

import com.apptite.apptite.model.HorarioFuncionamento;
import com.apptite.apptite.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioFuncionamentoRepository extends JpaRepository<HorarioFuncionamento, Integer> {

    List<HorarioFuncionamento> findByRestaurante(Restaurante restaurante);

    void deleteByRestaurante(Restaurante restaurante);
}