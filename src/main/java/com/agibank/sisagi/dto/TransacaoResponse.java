package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponse(
        Long id,
        TipoTransacao tipo,
        BigDecimal valor,
        LocalDateTime dataHora,
        
        String nsUnico,
        Long contaOrigemId,
        String numeroContaOrigem,
        Long contaDestinoId,
        String numeroContaDestino,
        Long gerenteExecutorId,
        String nomeGerenteExecutor,
        String motivoMovimentacao
) {}

