package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTransacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransacaoRequest(
        @NotNull(message = "O ID da conta de origem é obrigatório")
        Long contaOrigemId,
        
        @NotNull(message = "O ID da conta de destino é obrigatório")
        Long contaDestinoId,
        
        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser positivo")
        BigDecimal valor,
        
        // Campos adicionais para enriquecer a funcionalidade
        TipoTransacao tipoTransacao,
        
        Long gerenteExecutorId,
        
        String motivoMovimentacao
) {}

