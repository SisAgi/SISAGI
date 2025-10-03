package com.agibank.sisagi.dto;

import com.agibank.sisagi.validator.Cpf;
import jakarta.validation.constraints.NotNull;

public record ContaUpdateRequest(String numeroConta,
                                 String agencia,
                                 @NotNull @Cpf String cpf) {}
