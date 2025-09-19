package com.agibank.SISAGI1.Cliente.DTOs;

public record ClienteResponse(Long id,
                              String nome,
                              String email,
                              String cpf) {
}
