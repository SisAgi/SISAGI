package com.agibank.sisagi.dto;

public record ClienteResponse(Long id,
                              String nome,
                              String email,
                              String cpf) {
}
