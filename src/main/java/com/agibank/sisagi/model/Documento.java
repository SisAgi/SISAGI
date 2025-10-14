package com.agibank.sisagi.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeArquivo;

    private String tipoArquivo;


    //Caracteristicas de armazenamento da imagem no banco, @Lob é para "avisar" que será enviado arquivos pesado
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] dados;
}
