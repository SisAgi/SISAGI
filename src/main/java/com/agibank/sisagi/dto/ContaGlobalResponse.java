package com.agibank.sisagi.dto;

import java.math.BigDecimal;

public record ContaGlobalResponse(Long id,
                                  String numeroConta,
                                  String agencia,
                                  BigDecimal saldoDolares,
                                  String tipoConta,
                                  String codigoSwift) {}
