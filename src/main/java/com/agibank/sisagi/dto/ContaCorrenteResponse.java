package com.agibank.sisagi.dto;

import java.math.BigDecimal;
import java.util.Set;

public record ContaCorrenteResponse(Long id,
                                    String numeroConta,
                                    String agencia,
                                    BigDecimal saldo,
                                    BigDecimal limiteChequeEspecial,
                                    String tipoConta,
                                    Set <Long> titularIds) {}
