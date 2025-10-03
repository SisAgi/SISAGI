package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ContaJovemRequest(@NotBlank String numeroConta,
                                @NotBlank String agencia,
                                @NotBlank BigDecimal saldo,
                                @NotEmpty List<Long> titularIds,
                                //Atributo específico) de Conta Jovem
                                @NotNull(message = "O campo de titular responsavel é obrigatório")
                                Long responsavelId){
}
