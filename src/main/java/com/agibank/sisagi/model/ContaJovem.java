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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_responsavel_id", referencedColumnName = "id_conta", nullable = false)
    Long responsavelId;

    @Builder.Default
    private boolean possuiLimite = false; // sempre falso
}
