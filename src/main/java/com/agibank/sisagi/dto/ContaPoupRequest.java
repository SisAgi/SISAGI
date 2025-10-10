package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ContaPoupRequest(
        @NotNull(message = "A agência é um campo obrigatório")
        String agencia,

        @NotNull(message = "A lista de CPFs de titulares não pode ser vazia")
        List<String> titularCpfs,

        @NotNull(message = "A senha é um campo obrigatório")
        String senha
) {
}