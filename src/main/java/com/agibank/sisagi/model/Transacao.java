package com.agibank.sisagi.model;

import com.agibank.sisagi.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@NoArgsConstructor
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ns_unico", nullable = false, unique = true, length = 50)
    private String nsUnico;

    @Column(name = "tipo_operacao", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "id_gerente_executor", nullable = false)
    private Long idGerenteExecutor;

    @Column(name = "motivo_movimentacao", columnDefinition = "TEXT")
    private String motivoMovimentacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_origem_fk", nullable = true)
    private Conta contaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_destino_fk", nullable = true)
    private Conta contaDestino;

    @PrePersist
    public void prePersist() {
        //Define a data/hora da transação (se não estiver definida)
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }

        //Gera o NSU alfanumérico único
        if (this.nsUnico == null || this.nsUnico.isEmpty()) {
            // Gera um UUID (32 caracteres alfanuméricos), remove os hífens
            // e converte para maiúsculas.
            this.nsUnico = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .toUpperCase();
        }
    }
}
