package com.agibank.SISAGI1.Gerente.DTOs;

public record GerenteRequest(String nome,
                             String senha,
                             String email,
                             String matricula) {
}
