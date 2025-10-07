package com.agibank.sisagi.model.enums;

public enum TipoServico {
    ENERGIA("Energia Elétrica"),
    AGUA_SANEAMENTO("Água e Saneamento"),
    TELEFONIA_FIXA_MOVEL("Telefonia Fixa/Móvel"),
    INTERNET_TV("Internet e TV"),
    IPVA("IPVA"),
    OUTROS("Outros Serviços Recorrentes");

    private final String descricao;

    TipoServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}