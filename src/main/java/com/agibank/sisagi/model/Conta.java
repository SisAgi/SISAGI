package com.agibank.sisagi.model;

import com.agibank.sisagi.exception.SaldoInsuficienteException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "contas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_conta", discriminatorType = DiscriminatorType.STRING)
@EqualsAndHashCode(of = "id")
@ToString(exclude = "titulares")
public abstract class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long id;

    @Column(name = "numero_conta", nullable = false, unique = true)
    private String numeroConta;

    @Column(name = "agencia", nullable = false)
    private String agencia;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "conta_titulares",
            joinColumns = @JoinColumn(name = "conta_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> titulares = new HashSet<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    public void creditar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do crédito deve ser positivo.");
        }
        this.saldo = this.saldo.add(valor);
    }

    public void debitar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser positivo.");
        }
        if (this.saldo.compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente.");
        }
        this.saldo = this.saldo.subtract(valor);
    }

}
