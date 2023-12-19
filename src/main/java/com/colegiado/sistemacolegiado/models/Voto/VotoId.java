package com.colegiado.sistemacolegiado.models.Voto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VotoId implements Serializable {
    @Column(name = "id_professor")
    private Integer idProfessor;

    @Column(name = "id_processo")
    private Integer idProcesso;
}