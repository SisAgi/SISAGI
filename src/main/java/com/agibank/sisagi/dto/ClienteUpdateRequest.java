package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTelefone;

import java.math.BigDecimal;
import java.util.List;

// Este DTO pode incluir todos os campos que o gerente pode atualizar
public record ClienteUpdateRequest(
        String nomeCompleto,
        String email,
        String rg,
        String estadoCivil,
        String nomeSocial,
        String profissao,
        String empresaAtual,
        String cargo,
        BigDecimal rendaMensal,
        Integer tempoEmprego,
        BigDecimal patrimonioEstimado,
        Boolean possuiRestricoesBancarias,
        Boolean ePpe,
        String ddi,
        String ddd,
        String numeroTelefone,
        TipoTelefone tipoTelefone,
        List<EnderecoRequest> enderecos
) {}