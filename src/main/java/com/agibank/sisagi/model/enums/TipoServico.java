package com.agibank.sisagi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoServico {

    // Enum com descrição para cada tipo de serviço
    ENERGIA("Energia Elétrica"),
    AGUA_SANEAMENTO("Água e Saneamento"),
    TELEFONIA_FIXA_MOVEL("Telefonia Fixa/Móvel"),
    INTERNET_TV("Internet e TV"),
    IPVA("IPVA"),
    OUTROS("Outros Serviços Recorrentes");

    // Descrição legível do tipo de serviço
    private final String descricao;
}