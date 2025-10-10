package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotNull(message = "O valor da transação não pode ser nulo.")
        @Positive(message = "O valor da transação deve ser positivo.")
        BigDecimal valor,

        @NotNull(message = "O ID da conta de origem é obrigatório.")
        Long contaOrigemId,

        @NotNull(message = "O ID da conta de destino é obrigatório.")
        Long contaDestinoId,

        String motivoMovimentacao
) {
}