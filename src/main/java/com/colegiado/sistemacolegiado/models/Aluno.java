package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Aluno extends Usuario{

    public Aluno(){
    }
    public Aluno(int id, String nome, String fone, String matricula, String login, User user){
        super(id, nome, fone, matricula, login, user);
    }
    @OneToMany (mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Processo> processos;

    public Aluno(UsuarioDTO alunoDTO) {
        this.setNome(alunoDTO.getNome());
        this.setFone(alunoDTO.getFone());
        this.setMatricula(alunoDTO.getMatricula());
        this.setLogin(alunoDTO.getLogin());
        this.setUser(alunoDTO.getUser());
    }

    public void setProcessoDoAluno (Processo processo){
        this.processos.add(processo);
    }

    public List<Processo> getProcessos (){
        return processos;
    }

    @Override
    public String toString() {
        StringBuilder processostext = new StringBuilder();

        if (processos != null) {
            for (Processo processo : processos) {
                processostext.append("Id processo: ").append(processo.getId()).append("Aluno: ").append(processo.getAluno().getNome());
            }

        }
        return super.toString() + "Processos do Aluno: " + processostext.toString();
    }
}
