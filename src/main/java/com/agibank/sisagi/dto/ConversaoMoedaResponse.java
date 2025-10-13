package com.agibank.sisagi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConversaoMoedaResponse(
        Long transacaoId,
        Long contaId,
        String numeroConta,
        BigDecimal valorReais,
        BigDecimal valorDolares,
        BigDecimal cotacaoUsada,
        BigDecimal saldoReaisAtual,
        BigDecimal saldoDolarAtual,
        LocalDateTime dataHoraConversao,
        String nsuOperacao
) {
}

