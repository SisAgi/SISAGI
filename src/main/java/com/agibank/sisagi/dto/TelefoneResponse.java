package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoTelefone;

public record TelefoneResponse(

        String ddi,
        String ddd,
        String numero,
        TipoTelefone tipoTelefone) {}
