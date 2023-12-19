package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.dto.AlunoDTO;
import com.colegiado.sistemacolegiado.models.dto.CriarProcessoDTO;
import com.colegiado.sistemacolegiado.models.enums.TipoDecisao;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Processo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(nullable = false)
    private LocalDate dataRecepcao;
    private LocalDate dataDistribuicao;
    private LocalDate dataParecer;
    private TipoDecisao parecer;
    @ManyToOne
    private Professor professor;
    @ManyToOne
    @JoinColumn(name = "aluno_id", referencedColumnName = "id")
    private Aluno aluno;
    //lembrar de fazer a relacao na criacao da reuniao. colocar a reuniao no processo
    @ManyToOne
    @JoinColumn(name = "reuniao_id", referencedColumnName = "id")
    private Reuniao reuniao;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assunto_id", referencedColumnName = "id")
    private Assunto assunto;
    private String requerimento;
    private StatusProcesso status;
    @Getter
    private String justificativa;

    public Processo(CriarProcessoDTO processoDTO, Aluno aluno, Assunto assunto) {
        this.aluno = aluno;
        this.assunto = assunto;
        this.requerimento = processoDTO.getRequerimento();
        this.status = StatusProcesso.CRIADO;
        this.dataRecepcao = LocalDate.now();
    }

    public void setAluno (Aluno newaluno){
        this.aluno = newaluno;

    }

    public String getStatus(){
        return status != null ? status.getStatuString() : "";
    }

    public String getDecisao(){
        return parecer != null ? parecer.getTipoDecisao() : "";
    }

    public String getRequerimento(){
        return requerimento;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String toString() {
        return "ID: " + id + "\n" +
                "Data: " + dataRecepcao + "\n" +
                "Requerimento: " + requerimento;

    }


}
