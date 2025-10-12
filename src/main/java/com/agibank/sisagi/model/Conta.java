package com.agibank.sisagi.model;

import com.agibank.sisagi.exception.SaldoInsuficienteException;
import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "contas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Estratégia de herança: tabela única
@DiscriminatorColumn(name = "tipo_conta", discriminatorType = DiscriminatorType.STRING) // Coluna para diferenciar os tipos de conta
@EqualsAndHashCode(of = "id") // Gera equals e hashCode baseados no campo 'id'
@ToString(exclude = "titulares") // Evita recursão infinita ao imprimir titulares
@RequiredArgsConstructor
public abstract class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta") // Nome da coluna no banco de dados
    private Long id;

    @Column(name = "numero_conta", nullable = false, unique = true, length = 20) // Número da conta deve ser único, não nulo e com tamanho máximo de 20 caracteres
    private String numeroConta;

    @Column(nullable = false, length = 60) // Senha deve ser não nula e com tamanho máximo de 60 caracteres (BCrypt)
    private String senha;

    @Column(name = "agencia", nullable = false) // Agência deve ser não nula
    private String agencia;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2) // Saldo não pode ser nulo, com precisão de 15 dígitos e 2 casas decimais
    private BigDecimal saldo;

    @Column(name = "data_abertura", nullable = false) // Data de abertura da conta
    private LocalDate dataAbertura;

    @Column(name = "segmento_cliente", nullable = false) // Segmento que indica o padrão de benefícios do cliente
    private SegmentoCliente segmentoCliente;

    @Column(name = "taxa_manutencao", nullable = false, precision = 10, scale = 2) // Taxa de manutenção sob os custos mensais de operação de conta
    private BigDecimal taxaManutencao;

    @Column(name = "status", nullable = false, length = 50) // Status da conta deve indicar se a mesma está "ATIVA" ou "DESATIVADA"
    private StatusConta statusConta;

    @ManyToMany(fetch = FetchType.LAZY) // Relação muitos para muitos com Cliente
    @JoinTable(
            name = "conta_titulares",
            joinColumns = @JoinColumn(name = "conta_id"),
            inverseJoinColumns = @JoinColumn(name = "cliente_id")
    )
    private Set<Cliente> titulares = new HashSet<>(); // Inicializa o conjunto para evitar NullPointerException

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relação um para muitos com Transacões
    private List<Transacao> transacoes;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DebitoAutomatico> debitoAutomaticos;

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
