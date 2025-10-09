package com.agibank.sisagi.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AgendamentoRequest(

        @NotNull(message = "Conta de origem é obrigatória")
        Long contaOrigemId,

        @NotNull(message = "Conta destino é obrigatória")
        Long contaDestinoId,

        @NotNull(message = "O valor não pode ser nulo")
        @Positive
        BigDecimal valor,

        @NotBlank
        @Pattern(regexp = "SEMANAL|MENSAL|ANUAL",
                message = "A frequência deve ser um dos seguintes valores: SEMANAL, MENSAL, ANUAL")
        String frequencia,

        @Min(1) @Max(28)
        @NotNull(message = "Data da cobrança é obrigatória")
        int diaDoMes) {
}
