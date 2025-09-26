package com.agibank.sisagi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "enderecos")
public class Endereco {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco")
    private Long idEndereco;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @Column(name = "cep", nullable = false, length = 10)
    private String cep;
    
    @Column(name = "logradouro", nullable = false, length = 150)
    private String logradouro;
    
    @Column(name = "numero", nullable = false, length = 20)
    private String numero;
    
    @Column(name = "complemento", nullable = false, length = 100)
    private String complemento;
    
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;
    
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;
    
    @Column(name = "tipo_endereco", nullable = false, length = 50)
    private String tipoEndereco;
}
