package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.StatusConta;

import java.math.BigDecimal;
import java.util.Set;

public record ContaJovemResponse(Long id,
                                 String numeroConta,
                                 String agencia,
                                 BigDecimal saldo,
                                 Long responsavelId,
                                 Set<Long> titularIds,
                                 StatusConta statusConta,
                                 String tipoConta){}


