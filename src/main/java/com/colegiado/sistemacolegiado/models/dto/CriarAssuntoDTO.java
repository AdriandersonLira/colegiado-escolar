package com.colegiado.sistemacolegiado.models.dto;


import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.Assunto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarAssuntoDTO {
    private int id;
    private String assunto;
    private String descricao;

    public CriarAssuntoDTO (Assunto assunto){
        this.id = assunto.getId();
        this.assunto = assunto.getAssunto();
    }

    public Assunto toAssunto(){
        Assunto newassunto = new Assunto();
        newassunto.setId(id);
        newassunto.setAssunto(assunto);
        return newassunto;
    }

    public String toString(){
        return "id: " + id + "\n" +
                "assunto: " + assunto + "\n";
    }


}
