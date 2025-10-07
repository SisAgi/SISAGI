package com.agibank.sisagi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        String nsUnico,
        String tipoTransacao,
        BigDecimal valor,
        LocalDateTime dataHora,
        Long contaId,
        Long contaOrigemId,
        Long contaDestinoId,
        Long gerenteExecutorId,
        String motivoMovimentacao
) {
}