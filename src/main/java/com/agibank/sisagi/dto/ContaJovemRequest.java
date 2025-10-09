package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaJovemRequest(

        @NotBlank(message = "Numero da conta é um campo obrigatório")
        String numeroConta,

        @NotBlank(message = "Agência é um campo obrigatório")
        String agencia,

        @NotEmpty(message = "A lista de titulares não pode ser vazia")
        List<Long> titularIds,

        //Atributo específico) de Conta Jovem
        @NotNull(message = "O campo de titular responsavel é obrigatório")
        Long responsavelId){}
