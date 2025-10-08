package com.agibank.sisagi.model;

import com.agibank.sisagi.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
public abstract class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    
    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;
    
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;
    
    @Column(nullable = false, length = 20)
    private String rg;
    
    @Column(name = "data_emissao_documento", nullable = false)
    private LocalDate dataEmissaoDocumento;
    
    @Column(name = "nome_pai", nullable = false, length = 150)
    private String nomePai;
    
    @Column(name = "nome_mae", nullable = false, length = 150)
    private String nomeMae;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "estado_civil", nullable = false, length = 50)
    private String estadoCivil;
    
    @Column(name = "nome_social", length = 150)
    private String nomeSocial;
    
    @Column(length = 100)
    private String profissao;
    
    @Column(name = "empresa_atual", length = 100)
    private String empresaAtual;
    
    @Column(length = 100)
    private String cargo;
    
    @Column(name = "salario_mensal", precision = 10, scale = 2)
    private BigDecimal salarioMensal;
    
    @Column(name = "tempo_emprego")
    private Integer tempoEmprego;
    
    @Column(name = "patrimonio_estimado", precision = 15, scale = 2)
    private BigDecimal patrimonioEstimado;
    
    @Column(name = "possui_restricoes_bancarias", nullable = false)
    private Boolean possuiRestricoesBancarias;
    
    @Column(name = "e_ppe", nullable = false)
    private Boolean ePpe;
    
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}
