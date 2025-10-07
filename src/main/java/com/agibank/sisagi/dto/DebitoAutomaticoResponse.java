package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoServico;

public record DebitoAutomaticoResponse(
        Long id,
        Long contaId,
        Integer diaAgendado,
        String tipoServico, // Retorna a descrição do Enum
        String status,      // Retorna a descrição do Enum
        String identificadorConvenio,
        String descricao
) {}