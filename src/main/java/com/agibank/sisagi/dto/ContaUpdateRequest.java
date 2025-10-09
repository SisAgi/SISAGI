package com.agibank.sisagi.dto;

import com.agibank.sisagi.validator.Cpf;
import jakarta.validation.constraints.NotNull;

public record ContaUpdateRequest(

        String numeroConta, // Campo opcional para atualização
        String agencia, // Campo opcional para atualização
        @NotNull @Cpf String cpf){}
