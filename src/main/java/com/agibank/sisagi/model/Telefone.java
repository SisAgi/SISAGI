package com.agibank.sisagi.model;


import com.agibank.sisagi.model.enums.TipoTelefone;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable //notação usada para que possamos transferir atributos de telefone para outras classes, no casso telefone.
public class Telefone {

    private String ddi;
    private String ddd;
    private String numero;

    @Enumerated(EnumType.STRING)
    private TipoTelefone tipoTelefone;

}
