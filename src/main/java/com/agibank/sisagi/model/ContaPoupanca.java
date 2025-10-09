package com.agibank.sisagi.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("POUPANCA")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContaPoupanca extends Conta{

    // Atributos específicos de conta poupança
    @Column(name = "data_aniversario")
    @Builder.Default
    private int dataAniversario = LocalDate.now().getDayOfMonth();

    @Column(name = "rendimento")
    private double rendimento = 0.05;
}