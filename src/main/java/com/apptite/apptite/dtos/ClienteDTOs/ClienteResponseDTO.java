package com.apptite.apptite.dtos.ClienteDTOs;

public record ClienteResponseDTO(
    Integer idCliente,
    String nomeCliente,
    String emailCliente,
    String telefoneCliente,
    String descricaoCliente,
    String fotoCliente
) {
    // Construtor de conveniência para mapear da entidade Cliente
    public static ClienteResponseDTO fromEntity(com.apptite.apptite.model.Cliente cliente) {
        if (cliente == null) return null;
        return new ClienteResponseDTO(
            cliente.getIdCliente(),
            cliente.getNomeCliente(),
            cliente.getEmailCliente(),
            cliente.getTelefoneCliente(),
            cliente.getDescricaoCliente(),
            cliente.getFotoCliente()
        );
    }
}