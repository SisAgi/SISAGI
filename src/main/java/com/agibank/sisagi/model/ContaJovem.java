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

    // Relação com o ID do responsável titular
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conta_responsavel_id", referencedColumnName = "id_conta", nullable = false)

    // Atributo específico da conta jovem
    private Conta responsavelId;
}
