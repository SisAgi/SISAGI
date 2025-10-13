package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ConversaoMoedaRequest(
        @NotNull(message = "O ID da conta não pode ser nulo")
        Long contaId,

        @NotNull(message = "O valor a ser convertido não pode ser nulo")
        @Positive(message = "O valor a ser convertido deve ser positivo")
        BigDecimal valorReais,

        String motivoMovimentacao
) {
}

