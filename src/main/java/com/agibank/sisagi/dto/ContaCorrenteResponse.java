package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaCorrenteResponse(
        Long id,
        String numeroConta,
        String agencia,
        BigDecimal saldo,
        BigDecimal limiteChequeEspecial,
        Set<String> titularCpfs,
        StatusConta statusConta,
        String tipoConta,
        SegmentoCliente segmentoCliente,
        BigDecimal taxaManutencao
) {
}
