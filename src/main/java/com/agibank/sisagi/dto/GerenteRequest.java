package com.agibank.sisagi.dto;

import jakarta.validation.constraints.NotBlank;

public record GerenteRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        
        @NotBlank(message = "A senha é obrigatória")
        String senha,
        
        @NotBlank(message = "O email é obrigatório")
        String email,
        
        @NotBlank(message = "A matrícula é obrigatória")
        String matricula,
        
        @NotBlank(message = "O CPF é obrigatório")
        String cpf
) {
}
