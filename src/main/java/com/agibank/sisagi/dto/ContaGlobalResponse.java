package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaGlobalResponse(

        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        BigDecimal saldoDolares,
        String codigoSwift,
        Set<Long> titularIds,
        StatusConta statusConta,
        String tipoConta) {}
