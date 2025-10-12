package com.agibank.sisagi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gerentes")
public class Gerente extends Usuarios {

    //Atributos específicos de Gerente
    @Column(nullable = false, length = 60) // Tamanho 60 para suportar BCrypt
    private String senha;

    @Column(nullable = false, unique = true)
    private String matricula;
}
