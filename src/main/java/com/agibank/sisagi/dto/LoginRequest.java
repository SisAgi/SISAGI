package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotNull(message = "Gerente ID é obrigatório") Long gerenteId,
        @NotBlank(message = "Senha é obrigatória") String senha
) {}
