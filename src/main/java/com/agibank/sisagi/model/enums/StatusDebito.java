package com.agibank.sisagi.model.enums;

public enum StatusDebito {
    ATIVO("Ativo"),
    SUSPENSO("Suspenso"),
    CANCELADO("Cancelado"),
    ERRO_PROCESSAMENTO("Erro no Processamento");

    private final String descricao;

    StatusDebito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}