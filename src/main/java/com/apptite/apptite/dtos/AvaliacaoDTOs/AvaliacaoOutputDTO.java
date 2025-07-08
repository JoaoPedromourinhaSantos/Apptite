package com.apptite.apptite.dtos.AvaliacaoDTOs; // Usando um subpacote para organização

import com.apptite.apptite.model.Avaliacao;
import java.time.LocalDateTime;

public record AvaliacaoOutputDTO(
        Integer idAvaliacao,
        String nomeCliente,
        Integer idCliente,
        Integer nota,
        String comentario,
        LocalDateTime dataAvaliacao,
        Integer idRestaurante,
        String nomeRestaurante,
        String respostaRestaurante
) {
    /**
     * Método de fábrica estático para converter uma entidade Avaliacao para este DTO.
     * @param avaliacao A entidade a ser convertida.
     * @return Uma instância de AvaliacaoOutputDTO.
     */
    public static AvaliacaoOutputDTO fromEntity(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }

        // Extrai os dados das entidades relacionadas, tratando possíveis valores nulos.
        String nomeRestaurante = (avaliacao.getRestaurante() != null)
                               ? avaliacao.getRestaurante().getNomeRestaurante()
                               : "Restaurante Indisponível";

        String nomeCliente = (avaliacao.getCliente() != null)
                           ? avaliacao.getCliente().getNomeCliente()
                           : "Cliente Anônimo";

        Integer idCliente = (avaliacao.getCliente() != null)
                          ? avaliacao.getCliente().getIdCliente()
                          : null;

        Integer idRestaurante = (avaliacao.getRestaurante() != null)
                              ? avaliacao.getRestaurante().getIdRestaurante()
                              : null;

        return new AvaliacaoOutputDTO(
            avaliacao.getIdAvaliacao(),
            nomeCliente,
            idCliente,
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getDataAvaliacao(),
            idRestaurante,
            nomeRestaurante,
            avaliacao.getRespostaRestaurante()
        );
    }
}