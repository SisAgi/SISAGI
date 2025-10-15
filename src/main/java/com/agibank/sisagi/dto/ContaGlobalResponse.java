package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaGlobalResponse(
        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        BigDecimal saldoDolar,
        String codigoSwift,
        Set<String> titularCpfs, // Alterado para CPF
        StatusConta statusConta,
        String tipoConta,
        SegmentoCliente segmentoCliente,
        BigDecimal taxaManutencao
) {
}