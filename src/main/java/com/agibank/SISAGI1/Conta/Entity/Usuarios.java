package com.agibank.SISAGI1.Conta.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@MappedSuperclass
public abstract class Usuarios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

}
