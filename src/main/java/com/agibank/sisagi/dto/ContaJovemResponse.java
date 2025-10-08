package com.agibank.sisagi.dto;

import java.math.BigDecimal;
import java.util.Set;

public record ContaJovemResponse(Long id,
                                 String numeroConta,
                                 String agencia,
                                 BigDecimal saldo,
                                 Long responsavelId,
                                 String tipoConta,
                                 Set<Long> titularIds) {

}
