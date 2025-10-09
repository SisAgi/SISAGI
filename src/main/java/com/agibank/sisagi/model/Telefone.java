package com.agibank.sisagi.model;


import com.agibank.sisagi.model.enums.TipoTelefone;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable // Anotação usada para transferir atributos de telefone para outras entidades
public class Telefone {

    private String ddi;
    private String ddd;
    private String numero;

    // Recebe um enum para definir o tipo de telefone
    @Enumerated(EnumType.STRING)
    private TipoTelefone tipoTelefone;

}
