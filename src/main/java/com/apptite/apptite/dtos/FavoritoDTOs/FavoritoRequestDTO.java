package com.apptite.apptite.dtos.FavoritoDTOs;

import jakarta.validation.constraints.NotNull;

public record FavoritoRequestDTO(
    @NotNull Integer idCliente,
    @NotNull Integer idRestaurante
) {}