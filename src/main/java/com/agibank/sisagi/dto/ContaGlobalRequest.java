package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ContaGlobalRequest(
        @NotBlank(message = "O número da conta é obrigatório")
        String numeroConta,
        
        @NotBlank(message = "A agência é obrigatória")
        String agencia,
        
        @NotNull(message = "O saldo inicial em dólares é obrigatório")
        @Positive(message = "O saldo deve ser positivo")
        BigDecimal saldoDolares,
        
        @NotNull(message = "Pelo menos um cliente titular é obrigatório")
        List<Long> clientesIds,
        
        @NotBlank(message = "O código SWIFT é obrigatório")
        String codigoSwift
) {}
