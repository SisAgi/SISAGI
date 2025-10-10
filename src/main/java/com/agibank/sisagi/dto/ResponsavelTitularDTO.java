package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponsavelTitularDTO(
        @NotNull
        Long id,
        @NotBlank
        String nome
) {
}
