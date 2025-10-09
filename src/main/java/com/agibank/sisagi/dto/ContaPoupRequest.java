package com.agibank.sisagi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaPoupRequest(

        @NotNull(message = "Numero de conta é um campo obrigatório")
        String numeroConta,

        @NotNull(message = "Agência é um campo obrigatório")
        String agencia,

        @NotNull(message = "A lista de titulares não pode ser vazia")
        List<Long> titularIds){}
