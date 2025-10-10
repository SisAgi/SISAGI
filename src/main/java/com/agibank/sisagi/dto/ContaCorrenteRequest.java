package com.agibank.sisagi.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaCorrenteRequest(

        @NotEmpty(message = "A lista de titulares não pode ser vazia")
        List<Long> titularIds,

        @NotNull(message = "O limite do cheque especial é obrigatório")
        BigDecimal limiteChequeEspecial,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}
