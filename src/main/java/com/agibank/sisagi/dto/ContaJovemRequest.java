package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaJovemRequest(@NotBlank String numeroConta,
                                @NotBlank String agencia,
                                @NotBlank BigDecimal saldo,
                                @NotBlank List<Long> clientesIds,
                                //Atributo específico) de Conta Jovem
                                @NotNull(message = "O campo de titular responsavel é obrigatório")
                                ResponsavelTitularDTO titularResponsavel){}
