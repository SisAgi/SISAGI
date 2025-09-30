package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;

public record ResponsavelTitularDTO(@NotBlank Long id,
                                    @NotBlank String nome) {}
