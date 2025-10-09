package com.agibank.sisagi.dto;

import com.agibank.sisagi.model.enums.TipoServico;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DebitoAutomaticoRequest(

        @NotNull(message = "O ID da conta não pode ser nulo.")
        Long contaId,

        @NotNull(message = "O dia agendado para o débito é obrigatório.")
        @Min(value = 1, message = "O dia agendado deve ser no mínimo 1.")
        @Max(value = 28, message = "O dia agendado deve ser no máximo 28.")
        Integer diaAgendado,

        @NotNull(message = "O tipo de serviço é obrigatório.")
        TipoServico tipoServico,

        @NotBlank(message = "O identificador do convênio é obrigatório.")
        String identificadorConvenio,

        String descricao){} //Descrição pode ser opcional