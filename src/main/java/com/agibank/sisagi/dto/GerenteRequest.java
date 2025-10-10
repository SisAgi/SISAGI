package com.agibank.sisagi.dto;

public record GerenteRequest(
        String nome,
        String senha,
        String email,
        String matricula
) {
}
