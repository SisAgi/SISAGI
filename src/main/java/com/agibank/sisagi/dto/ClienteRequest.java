package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTelefone;
import com.agibank.sisagi.validator.Cpf;
import jakarta.validation.constraints.*;

public record ClienteRequest(@NotBlank(message = "Nome é um campo obrigatório")
                             @Pattern(regexp = "^[A-Za-zÀ-ú ]+$", message = "Nome deve conter apenas letras e espaços")
                             String nome,
                             @NotBlank(message = "Senha é um campo obrigatório")
                             @Size(min = 6, message = "Senha precisa ter no minimo 6 caracteres")
                             String senha,
                             @NotBlank(message = "Email é um campo obrigatório")
                             @Email(message = "Email inválido")
                             String email,
                             @NotBlank(message = "CPF não pode ser vazio")
                             @Cpf
                             String cpf,
                             Long gerenteId,

                             @NotBlank (message = "DDI é um campo obrigatório")
                             String ddi,
                             @NotBlank(message = "DDD é um campo obrigatório")
                             String ddd,
                             @NotBlank(message = "Número de Telefone é um campo obrigatório")
                             String numeroTelefone,
                             @NotNull(message = "O tipo de telefone é obrigatório")
                             TipoTelefone tipoTelefone,

                             @NotBlank (message = "CEP é um campo obrigatório")
                             String cep,
                             @NotBlank String logradouro,
                             String complemento,
                             @NotBlank String cidade,
                             @NotBlank String tipoEndereco,
                             @NotBlank String uf,
                             @NotBlank String numero) {}
