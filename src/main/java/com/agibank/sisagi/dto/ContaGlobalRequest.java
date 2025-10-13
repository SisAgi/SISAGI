package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ContaGlobalRequest(
        @NotNull(message = "A agência é um campo obrigatório")
        String agencia,

        @NotEmpty(message = "A lista de CPFs de titulares não pode ser vazia")
        List<String> titularCpfs,

        @NotBlank(message = "A senha é obrigatória")
        @Pattern(regexp = "\\d{6}", message = "A senha deve conter exatamente 6 dígitos numéricos")
        String senha
) {
}