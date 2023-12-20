package com.colegiado.sistemacolegiado.models.dto;

import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private int id;
    private String nome;
    private String fone;
    private String matricula;
    private String login;
    private User user;

    public AlunoDTO (Aluno aluno){
        this.id = aluno.getId();
        this.nome = aluno.getNome();
        this.fone = aluno.getFone();
        this.matricula = aluno.getMatricula();
        this.login = aluno.getLogin();
        this.user = aluno.getUser();
    }

    public String toString(){
        return "id: " + id + "\n" +
                "Nome: " + nome + "\n" +
                "fone: " + fone + "\n" +
                "matricula: " + matricula + "\n" +
                "login: " + login + "\n";
    }
}
