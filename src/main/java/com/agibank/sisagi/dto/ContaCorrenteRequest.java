package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaCorrenteRequest(

        @NotBlank(message = "Numero da conta é um campo obrigatório")
        String numeroConta,

        @NotBlank(message = "Agência é um campo obrigatório")
        String agencia,

        @NotBlank(message = "A lista de titulares não pode ser vazia")
        List<Long> titularIds,

        //Atributo específico de Conta Corrente
        @NotNull
        BigDecimal limiteChequeEspecial){}
