package com.agibank.sisagi.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("GLOBAL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaGlobal extends Conta {
    
    @Column(name = "saldo_dolares", precision = 19, scale = 2)
    private BigDecimal saldoDolares;
    
    @Column(name = "codigo_swift", length = 11)
    private String codigoSwift;
    
    @Column(name = "cotacao_atual", precision = 10, scale = 4)
    private BigDecimal cotacaoAtual;
}

