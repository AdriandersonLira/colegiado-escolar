package com.colegiado.sistemacolegiado.models.Voto;

import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.Professor;
import com.colegiado.sistemacolegiado.models.Reuniao;
import com.colegiado.sistemacolegiado.models.enums.TipoVoto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Voto {

    @EmbeddedId
    private VotoId id;

    private String opcao;

    @ManyToOne
    @MapsId("id_professor")
    @JoinColumn(name="id_professor")
    private Professor professor;

    @ManyToOne
    @MapsId("id_processo")
    @JoinColumn(name= "id_processo")
    private Processo processo;

    @ManyToOne
    @JoinColumn(name = "reuniao_id")
    private Reuniao reuniao;

    private TipoVoto voto;

    private String texto;

    public Voto(Professor professor, Processo processo, TipoVoto voto, String texto) {
        this.professor = professor;
        this.processo = processo;
        this.voto =voto;
        this.texto = texto;
    }


    public Voto(VotoId votoId, Professor professor, Processo processo, TipoVoto voto, String texto) {
        this.id = votoId;
        this.professor = professor;
        this.processo = processo;
        this.voto =voto;
        this.texto = texto;
    }
}


