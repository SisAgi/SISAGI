package com.agibank.sisagi.dto;

public record EnderecoResponse(Long idEndereco,
                               String cep,
                               String logradouro,
                               String numero,
                               String complemento,
                               String cidade,
                               String estado,
                               String tipoEndereco,
                               Long clienteId) {
}
