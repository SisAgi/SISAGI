package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DebitoAutomaticoRequest(
        @NotBlank(message = "O código da empresa é obrigatório.")
        @Size(min = 5, max = 20, message = "O código da empresa deve ter entre 5 e 20 caracteres.")
        String codigoEmpresa,

        @NotBlank(message = "O número de identificação do cliente é obrigatório.")
        @Size(min = 5, max = 50, message = "O número de identificação do cliente deve ter entre 5 e 50 caracteres.")
        String numeroIdentificacaoCliente,

        @NotNull(message = "O ID da conta bancária é obrigatório.")
        Long contaBancariaId
) {
}


