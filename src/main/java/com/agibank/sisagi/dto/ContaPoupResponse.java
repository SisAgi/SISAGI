package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaPoupResponse(

        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        int diaAniversario,
        double rendimento,
        String tipoConta,
        Set<Long> titularIds,
        StatusConta statusConta){}
