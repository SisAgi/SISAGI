package com.agibank.sisagi.dto;

public record EnderecoUpdateRequest(

        //Campos opcionais para atualizar o endereço
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String cidade,
        String estado,
        String tipoEndereco){}
