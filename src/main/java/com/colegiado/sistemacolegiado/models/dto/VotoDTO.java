package com.colegiado.sistemacolegiado.models.dto;

import com.colegiado.sistemacolegiado.models.enums.TipoVoto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class VotoDTO {
    private Integer idProfessor;
    private Integer idProcesso;
    private TipoVoto voto;
    private String texto;
}
