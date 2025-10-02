package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        String nsUnico,
        TipoTransacao tipoTransacao,
        BigDecimal valor,
        LocalDateTime dataHora,
        Long contaId,
        String numeroConta,
        Long gerenteExecutorId,
        String nomeGerenteExecutor,
        String motivoMovimentacao,
        Long contaOrigemId,
        String numeroContaOrigem,
        Long contaDestinoId,
        String numeroContaDestino
) {}

