package com.agibank.sisagi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@DiscriminatorValue("JOVEM")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaJovem extends Conta {

    @ManyToOne
    @JoinColumn(name = "conta_responsavel_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conta responsavelContaId;
}