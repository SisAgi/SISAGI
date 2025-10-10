package com.agibank.sisagi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("JOVEM")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaJovem extends Conta {

    // Atributo específico da conta jovem
    @OneToOne
    @JoinColumn(name = "conta_responsavel_id", nullable = true)
    private Conta responsavelContaId;
}
