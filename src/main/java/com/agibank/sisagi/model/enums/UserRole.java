package com.agibank.sisagi.model.enums;

public enum UserRole {
    CLIENTE("ROLE_CLIENTE"),
    GERENTE("ROLE_GERENTE");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
