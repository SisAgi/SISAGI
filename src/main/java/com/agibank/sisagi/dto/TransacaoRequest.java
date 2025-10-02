package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransacaoRequest(
        @NotNull(message = "O tipo de transação é obrigatório")
        TipoTransacao tipoTransacao,
        
        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor,
        
        @NotNull(message = "O ID da conta é obrigatório")
        Long contaId,
        
        @NotNull(message = "O ID do gerente executor é obrigatório")
        Long gerenteExecutorId,
        
        String motivoMovimentacao,
        
        // Para transferências
        Long contaOrigemId,
        
        Long contaDestinoId
) {}

