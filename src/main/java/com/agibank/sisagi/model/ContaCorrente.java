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
    @Column
    private BigDecimal limiteChequeEspecial;
}
