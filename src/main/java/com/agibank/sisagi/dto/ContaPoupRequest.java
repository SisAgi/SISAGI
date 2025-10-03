package com.agibank.sisagi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaPoupRequest(@NotNull String numeroConta,
                               @NotNull String agencia,
                               @NotNull BigDecimal saldo,
                               @NotNull List<Long> titularIds,
                               //Atributo específico de Conta Poupança
                               @Min(value = 1, message = "O dia minimo de aniversário deve ser 1")
                               @Max(value = 31, message = "O dia máximo de aniversário deve ser 31")
                               @NotNull int diaAniversario) {
}
