package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {
    
    private final RestTemplate restTemplate;
    
    @Value("${exchangerate.api.key:09f12f5cae46fe29a08e0fb2}")
    private String apiKey;
    
    @Value("${exchangerate.api.url:https://v6.exchangerate-api.com/v6}")
    private String apiUrl;
    
    public ExchangeRateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Obtém a taxa de câmbio do USD para BRL
     * O resultado é cacheado por 1 hora
     */
    @Cacheable(value = "exchangeRates", key = "'USD_to_BRL'")
    public BigDecimal getUsdToBrlRate() {
        String url = String.format("%s/%s/latest/USD", apiUrl, apiKey);
        
        try {
            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
            
            if (response != null && "success".equals(response.result())) {
                return response.getRateFor("BRL");
            }
            
            throw new RuntimeException("Falha ao obter taxa de câmbio da API");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao consultar API de câmbio: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converte um valor em dólares para reais
     * @param valorDolares Valor em USD
     * @return Valor equivalente em BRL
     */
    public BigDecimal converterDolarParaReal(BigDecimal valorDolares) {
        if (valorDolares == null || valorDolares.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal taxa = getUsdToBrlRate();
        return valorDolares.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Converte um valor em reais para dólares
     * @param valorReais Valor em BRL
     * @return Valor equivalente em USD
     */
    public BigDecimal converterRealParaDolar(BigDecimal valorReais) {
        if (valorReais == null || valorReais.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal taxa = getUsdToBrlRate();
        return valorReais.divide(taxa, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtém a taxa de câmbio atual
     * @return Taxa de conversão USD -> BRL
     */
    public BigDecimal getCotacaoAtual() {
        return getUsdToBrlRate();
    }
}

