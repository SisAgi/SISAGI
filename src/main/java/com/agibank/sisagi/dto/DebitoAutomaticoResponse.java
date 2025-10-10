package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.FrequenciaDebito;

public record DebitoAutomaticoResponse(
        Long id,
        Long contaId,
        Integer diaAgendado,
        FrequenciaDebito frequecia,
        String tipoServico,
        String status,
        String identificadorConvenio,
        String descricao
) {
}