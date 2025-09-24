package com.agibank.sisagi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(String cep,
                             String logradouro,
                             String complemento,
                             String bairro,
                             String localidade,
                             String uf,
                             String erro) {
    
    public boolean isErro() {
        return erro != null;
    }
}
