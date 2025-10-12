package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidarSenhaRequest(
        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}

