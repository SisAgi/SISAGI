package com.agibank.sisagi.dto;

import java.math.BigDecimal;

public record ContaPoupResponse(Long id,
                                String numeroConta,
                                String agencia,
                                BigDecimal saldo,
                                int diaAniversario,
                                double rendimento) {}
