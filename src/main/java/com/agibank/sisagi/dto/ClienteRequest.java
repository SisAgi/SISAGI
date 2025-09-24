package com.agibank.sisagi.dto;

public record ClienteRequest(String nome,
                             String senha,
                             String email,
                             String cpf,
                             Long gerenteId) {
}
