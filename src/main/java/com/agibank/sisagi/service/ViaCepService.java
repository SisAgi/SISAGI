package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViaCepService {
    
    private final RestTemplate restTemplate;
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";
    
    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        try {
            // Remove caracteres não numéricos do CEP
            String cepLimpo = cep.replaceAll("[^0-9]", "");
            
            // Valida se o CEP tem 8 dígitos
            if (cepLimpo.length() != 8) {
                throw new IllegalArgumentException("CEP deve ter 8 dígitos");
            }
            
            log.info("Buscando endereço para CEP: {}", cepLimpo);
            
            ViaCepResponse response = restTemplate.getForObject(VIA_CEP_URL, ViaCepResponse.class, cepLimpo);
            
            if (response == null || response.isErro()) {
                throw new IllegalArgumentException("CEP não encontrado: " + cep);
            }
            
            log.info("Endereço encontrado: {} - {}", response.logradouro(), response.localidade());
            return response;
            
        } catch (Exception e) {
            log.error("Erro ao buscar CEP {}: {}", cep, e.getMessage());
            throw new IllegalArgumentException("Erro ao buscar CEP: " + e.getMessage());
        }
    }
}
