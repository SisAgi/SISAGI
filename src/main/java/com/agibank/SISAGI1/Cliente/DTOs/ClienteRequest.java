package com.agibank.SISAGI1.Cliente.DTOs;

public record ClienteRequest(String nome,
                             String senha,
                             String email,
                             String cpf,
                             Long gerenteId) {
}
