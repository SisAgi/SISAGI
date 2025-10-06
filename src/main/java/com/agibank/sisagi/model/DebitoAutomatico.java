package com.agibank.sisagi.model;

import java.time.LocalDate;

import com.agibank.sisagi.model.enums.StatusDebito;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "debito_automatico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitoAutomatico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataDebito;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDebito status;

    private String descricao;

    @Column(nullable = false, unique = true)
    private String numeroReferencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id")
    private Conta conta;

}
