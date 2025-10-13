package com.agibank.sisagi.model;

import com.agibank.sisagi.exception.SaldoInsuficienteException;
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
    @Column(name = "limite_cheque_especial", nullable = true, precision = 15, scale = 2)
    private BigDecimal limiteChequeEspecial;


    public void debitarCorrente(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser positivo.");
        }
        BigDecimal limiteTotal = getSaldo().add(this.limiteChequeEspecial);
        if(valor.compareTo(limiteTotal) > 0){
            throw new SaldoInsuficienteException("Limite e saldo insuficiente");
        }
        setSaldo(getSaldo().subtract(valor));
    }


}
