package com.apptite.apptite.model; // Ou o pacote que você usa para enums

public enum StatusReserva {
    SOLICITADA,  // Reserva foi solicitada pelo cliente, aguardando aprovação.
    AGENDADO,    // Reserva foi aceita/confirmada pelo estabelecimento.
    RECUSADA,    // Reserva foi explicitamente recusada pelo estabelecimento.
    CANCELADO,  // Reserva foi cancelada pelo cliente ou pelo estabelecimento.
    COMPARECIDA,  // Reserva foi concluída (o cliente compareceu e a reserva foi finalizada).
    PASSADO      // A data/hora da reserva já ocorreu (originalmente AGENDADO ou SOLICITADA que não foi tratada).
}