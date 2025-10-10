package com.agibank.sisagi.dto;

public record DebitoAutomaticoResponse(
        Long id,
        Long contaId,
        Integer diaAgendado,
        String frequecia,
        String tipoServico, // Retorna a descrição do Enum
        String status,      // Retorna a descrição do Enum
        String identificadorConvenio,
        String descricao
) {
}