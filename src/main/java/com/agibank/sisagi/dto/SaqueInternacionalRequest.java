package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SaqueInternacionalRequest(
        @NotNull(message = "O ID da conta não pode ser nulo")
        Long contaId,

        @NotNull(message = "O valor do saque não pode ser nulo")
        @Positive(message = "O valor do saque deve ser positivo")
        BigDecimal valorDolares,

        String motivoMovimentacao
) {
}

