package com.agibank.sisagi.model;

import com.agibank.sisagi.model.enums.FrequenciaDebito;
import com.agibank.sisagi.model.enums.StatusDebito;
import com.agibank.sisagi.model.enums.TipoServico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "debito_automatico")
@Getter
@Setter
@NoArgsConstructor
public class DebitoAutomatico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // O dia do mês em que o débito deve ser executado (Ex: dia 5)
    @Column(name = "dia_agendado", nullable = false)
    private Integer diaAgendado;

    // Tipo de serviço (água, luz, telefone, etc.)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico", nullable = false)
    private TipoServico tipoServico;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequencia", nullable = false)
    private FrequenciaDebito frequencia;

    // O status da regra de débito (ATIVO, SUSPENSO, CANCELADO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDebito status;

    // Código do consumidor ou número da fatura/convênio, que é único para o serviço.
    // É o código que o banco usa para identificar o boleto/fatura a ser pago.
    @Column(name = "identificador_convenio", nullable = false, unique = true)
    private String identificadorConvenio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;
}
