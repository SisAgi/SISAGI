package com.agibank.sisagi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO para resposta da API ExchangeRate-API
 * API: https://www.exchangerate-api.com/
 */
public record ExchangeRateResponse(

        String result,
        String documentation,

        @JsonProperty("terms_of_use")
        String termsOfUse,
        @JsonProperty("time_last_update_unix")
        Long timeLastUpdateUnix,
        @JsonProperty("time_last_update_utc")
        String timeLastUpdateUtc,
        @JsonProperty("time_next_update_unix")
        Long timeNextUpdateUnix,
        @JsonProperty("time_next_update_utc")
        String timeNextUpdateUtc,
        @JsonProperty("base_code")
        String baseCode,
        @JsonProperty("conversion_rates")
        Map<String, BigDecimal> conversionRates
) {
    /**
     * Obtém a taxa de conversão para uma moeda específica
     * @param currencyCode Código da moeda (ex: "BRL", "EUR")
     * @return Taxa de conversão
     */
    public BigDecimal getRateFor(String currencyCode) {
        return conversionRates != null ? conversionRates.get(currencyCode) : BigDecimal.ZERO;
    }
}

