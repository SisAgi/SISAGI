package com.agibank.sisagi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusDebito {

    // Enum com os status possíveis para o débito automático
    ATIVO("Ativo"),
    SUSPENSO("Suspenso"),
    CANCELADO("Cancelado"),
    ERRO_PROCESSAMENTO("Erro no Processamento");

    // Descrição legível do status
    private final String descricao;
}