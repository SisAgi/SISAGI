package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Gerente ID é obrigatório") Long gerenteId,
        @NotBlank(message = "Senha é obrigatória") String senha
) {}