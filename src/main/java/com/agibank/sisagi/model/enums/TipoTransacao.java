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
    TRANSFERENCIA_RECEBIDA("Transferência recebida");

    // Descrição legível do tipo de transação
    private final String descricao;
}
