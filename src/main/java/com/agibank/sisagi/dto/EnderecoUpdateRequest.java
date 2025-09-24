package com.agibank.sisagi.dto;

public record EnderecoUpdateRequest(String cep,
                                    String logradouro,
                                    String numero,
                                    String complemento,
                                    String cidade,
                                    String estado,
                                    String tipoEndereco) {
}
