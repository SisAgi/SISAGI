package com.agibank.sisagi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

    // Definição dos papéis de usuário
    CLIENTE("ROLE_CLIENTE"),
    GERENTE("ROLE_GERENTE");
    // Descrição do papel
    private final String role;
}
