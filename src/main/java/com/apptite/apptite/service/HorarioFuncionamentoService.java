package com.apptite.apptite.service;

import com.apptite.apptite.dtos.HorarioFuncionamentoDTOs.*;
import com.apptite.apptite.model.HorarioFuncionamento;
import com.apptite.apptite.model.Restaurante; // ATUALIZADO
import com.apptite.apptite.repository.HorarioFuncionamentoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Spring @Transactional

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HorarioFuncionamentoService {

    private final HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    public HorarioFuncionamentoService(HorarioFuncionamentoRepository repository) {
        this.horarioFuncionamentoRepository = repository;
    }

    public List<HorarioFuncionamento> listarPorRestaurante(Restaurante restaurante) { // Parâmetro atualizado
        return horarioFuncionamentoRepository.findByRestaurante(restaurante); // Método do repo atualizado
    }

    // O método salvar individual pode ser mantido se houver um endpoint para isso,
    // mas a lógica principal de atualização será via atualizarHorariosParaRestaurante
    public HorarioFuncionamento salvar(HorarioFuncionamento horario) {
        return horarioFuncionamentoRepository.save(horario);
    }

    public Optional<HorarioFuncionamento> buscarPorId(Integer id) {
        return horarioFuncionamentoRepository.findById(id);
    }

    public void deletarPorId(Integer id) {
        horarioFuncionamentoRepository.deleteById(id);
    }

    @Transactional // Usar Spring @Transactional
    public void atualizarHorariosParaRestaurante(Restaurante restaurante, List<HorarioFuncionamentoRequestDTO> novosHorariosDTO) {
        // 1. Buscar horários existentes para este restaurante
        List<HorarioFuncionamento> horariosAntigos = horarioFuncionamentoRepository.findByRestaurante(restaurante);

        // 2. Identificar horários para deletar
        List<Integer> idsDosNovosHorarios = novosHorariosDTO.stream()
                .map(HorarioFuncionamentoRequestDTO::idHorario)
                .filter(java.util.Objects::nonNull) // Considera apenas DTOs que podem ser de horários existentes
                .collect(Collectors.toList());

        List<HorarioFuncionamento> horariosParaDeletar = horariosAntigos.stream()
                .filter(antigo -> !idsDosNovosHorarios.contains(antigo.getIdHorario()))
                .collect(Collectors.toList());

        horarioFuncionamentoRepository.deleteAll(horariosParaDeletar);

        // 3. Atualizar horários existentes e adicionar novos
        for (HorarioFuncionamentoRequestDTO dto : novosHorariosDTO) {
            HorarioFuncionamento horario;
            if (dto.idHorario() != null) { // Tenta atualizar um existente
                horario = horariosAntigos.stream()
                            .filter(h -> h.getIdHorario().equals(dto.idHorario()))
                            .findFirst()
                            .orElseGet(HorarioFuncionamento::new); // Ou lança exceção se ID não encontrado mas esperado
            } else { // Cria um novo
                horario = new HorarioFuncionamento();
            }

            horario.setRestaurante(restaurante);
            horario.setDiaSemana(dto.diaSemana());
            horario.setHoraInicio(dto.horaInicio());
            horario.setHoraFim(dto.horaFim());
            horario.setAtivo(dto.ativo());

            horarioFuncionamentoRepository.save(horario);
        }
    }
}