package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransacaoRequest(
        @NotNull(message = "O tipo de transação é obrigatório.")
        TipoTransacao tipoTransacao,

        @NotNull(message = "O valor da transação não pode ser nulo.")
        @Positive(message = "O valor da transação deve ser positivo.")
        BigDecimal valor,

        @NotNull(message = "O ID da conta é obrigatório.")
        Long contaId,

        Long contaOrigemId,
        Long contaDestinoId,

        String motivoMovimentacao
) {
}