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
    @Column(nullable = false, unique = true)
    private String matricula;
}
