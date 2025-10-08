package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;

public record ContaGlobalResponse(
        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        BigDecimal saldoDolares,
        String codigoSwift,
        StatusConta statusConta,
        String tipoConta) {}
