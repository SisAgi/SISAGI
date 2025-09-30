package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public record ContaGlobalRequest(@NotBlank String numeroConta,
                                 @NotBlank String agencia,
                                 @NotBlank BigDecimal saldoDolares,
                                 @NotBlank List<Long> clientesIds,
                                 //Atributo específico de Conta Global
                                 @NotBlank String codigoSwift){}
