package com.agibank.sisagi.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CORRENTE")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaCorrente extends Conta {

    //Atributo específico da conta corrente
    @Column(name = "limite-cheque-especial", nullable = false)
    private BigDecimal limiteChequeEspecial;
}
