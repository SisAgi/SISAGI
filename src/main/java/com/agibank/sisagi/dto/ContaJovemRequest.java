package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ContaJovemRequest(
        @NotNull(message = "A agência é um campo obrigatório")
        String agencia,

        @NotEmpty(message = "A lista de CPFs de titulares não pode ser vazia")
        List<String> titularCpfs,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O ID da conta do responsável é obrigatório")
        Long responsavelId
) {
}