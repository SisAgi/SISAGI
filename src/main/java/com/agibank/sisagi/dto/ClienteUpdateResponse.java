package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.UserRole;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ClienteUpdateResponse(

        Long id,
        String nomeCompleto,
        String email,
        String cpf,
        LocalDate dataNascimento,
        String rg,
        LocalDate dataEmissaoDocumento,
        String nomePai,
        String nomeMae,
        String estadoCivil,
        String nomeSocial,
        String profissao,
        String empresaAtual,
        String cargo,
        BigDecimal salarioMensal,
        Integer tempoEmprego,
        BigDecimal patrimonioEstimado,
        Boolean possuiRestricoesBancarias,
        Boolean ePpe,
        UserRole role,
        List<EnderecoResponse> enderecos,
        TelefoneResponse telefoneResponse){}
