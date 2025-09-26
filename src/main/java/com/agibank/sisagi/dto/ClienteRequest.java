package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTelefone;
import com.agibank.sisagi.validator.Cpf;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(@NotBlank String nome,
                             @NotBlank @Size(min = 6) String senha,
                             @NotBlank @Email String email,
                             @NotBlank(message = "CPF não pode ser vazio")
                             @Cpf
                             String cpf,
                             Long gerenteId,

                             @NotBlank String ddi,
                             @NotBlank String ddd,
                             @NotBlank String numeroTelefone,
                             @NotNull(message = "O tipo de telefone é obrigatório")
                             TipoTelefone tipoTelefone,

                             @NotBlank String cep,
                             @NotBlank String logradouro,
                             String complemento,
                             @NotBlank String cidade,
                             @NotBlank String tipoEndereco,
                             @NotBlank String uf,
                             @NotBlank String numero) {}
