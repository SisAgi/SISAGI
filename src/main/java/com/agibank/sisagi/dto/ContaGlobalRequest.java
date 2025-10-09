package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ContaGlobalRequest(

        @NotBlank(message = "O número da conta é um campo obrigatório obrigatório")
        String numeroConta,

        @NotBlank(message = "A agência é um campo obrigatório obrigatória")
        String agencia,

        @NotNull(message = "Pelo menos um cliente titular é obrigatório")
        List<Long> titularIds){}
