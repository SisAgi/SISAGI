package com.agibank.SISAGI1.Cliente.Entity;
import com.agibank.SISAGI1.Conta.Entity.Usuarios;
import com.agibank.SISAGI1.Gerente.Entity.Gerente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clientes")
public class Cliente extends Usuarios {
    @Column(nullable = false)
    private String cpf;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gerente_id")
    private Gerente gerente;

}
