package com.agibank.sisagi.dto;

import java.math.BigDecimal;

public record ContaCorrenteResponse(Long id,
                                    String numeroConta,
                                    String agencia,
                                    BigDecimal saldo,
                                    BigDecimal limiteChequeEspecial) {}
