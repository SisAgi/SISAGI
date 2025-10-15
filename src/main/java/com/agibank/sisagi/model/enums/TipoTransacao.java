package com.agibank.sisagi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoTransacao {

    // Enum para os tipos de transações
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    TRANSFERENCIA("Transferência"),
    TRANSFERENCIA_ENVIADA("Transferência enviada"),
    TRANSFERENCIA_RECEBIDA("Transferência recebida"),
    CONVERSAO_MOEDA("Conversão de Moeda"),
    DEPOSITO_INTERNACIONAL("Depósito em Real e Conversão para Dólar"),
    SAQUE_INTERNACIONAL("Saque em Real com Débito em Dólar");

    // Descrição legível do tipo de transação
    private final String descricao;
}