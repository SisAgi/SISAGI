package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaPoupResponse(
        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        Integer diaAniversario,
        BigDecimal rendimento,
        String tipoConta,
        Set<String> titularCpfs,
        StatusConta statusConta,
        SegmentoCliente segmentoCliente
) {
}