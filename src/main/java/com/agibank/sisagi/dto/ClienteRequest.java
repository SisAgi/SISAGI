package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTelefone;
import com.agibank.sisagi.validator.Cpf;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClienteRequest(
        @NotBlank(message = "Nome é um campo obrigatório")
        @Pattern(regexp = "^[A-Za-zÀ-ú ]+$", message = "Nome deve conter apenas letras e espaços")
        String nomeCompleto,

        @NotBlank(message = "Email é um campo obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "CPF não pode ser vazio")
        @Cpf
        String cpf,
        @NotNull(message = "ID do gerente é um campo obrigatório")
        Long gerenteId,

        @NotBlank(message = "DDI é um campo obrigatório")
        String ddi,
        @NotBlank(message = "DDD é um campo obrigatório")
        String ddd,
        @NotBlank(message = "Número de Telefone é um campo obrigatório")
        String numeroTelefone,
        @NotNull(message = "O tipo de telefone é obrigatório")
        TipoTelefone tipoTelefone,

        @NotBlank(message = "CEP é um campo obrigatório")
        String cep,
        String logradouro,
        String cidade,
        String uf,
        String bairro,
        @NotBlank String tipoEndereco,
        @NotBlank String numero,
        String complemento,

        // --- Campos Adicionados para corresponder à entidade Usuarios ---
        @NotNull(message = "Data de Nascimento é obrigatório")
        LocalDate dataNascimento,

        @NotBlank(message = "RG é um campo obrigatório")
        String rg,

        @NotNull(message = "Data de Emissão do Documento é obrigatório")
        LocalDate dataEmissaoDocumento,

        @NotBlank(message = "Nome do Pai é um campo obrigatório")
        String nomePai,

        @NotBlank(message = "Nome da Mãe é um campo obrigatório")
        String nomeMae,

        @NotBlank(message = "Estado Civil é um campo obrigatório")
        String estadoCivil,

        String nomeSocial, // Opcional

        String profissao, // Opcional

        String empresaAtual, // Opcional

        String cargo, // Opcional

        BigDecimal rendaMensal, // Opcional

        Integer tempoEmprego, // Opcional

        BigDecimal patrimonioEstimado, // Opcional

        @NotNull(message = "Possui Restrições Bancárias é um campo obrigatório")
        Boolean possuiRestricoesBancarias,

        @NotNull(message = "E PPE é um campo obrigatório")
        Boolean ePpe
) {
}