package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.Voto.Voto;
import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Professor extends Usuario{

    private boolean coordenador;
    @ManyToOne
    @JoinColumn(name = "colegiado_id")
    private Colegiado colegiado;
    @OneToMany
    private List<Processo> processos;
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private List<Voto> votos;

    public Professor(int id, String nome, String fone, String matricula, String login, User user, boolean coordenador){
        super(id, nome, fone, matricula, login, user);
        this.coordenador = coordenador;
        this.votos = new ArrayList<>();
    }

    public Professor(UsuarioDTO professorDTO) {
        this.setNome(professorDTO.getNome());
        this.setFone(professorDTO.getFone());
        this.setMatricula(professorDTO.getMatricula());
        this.setLogin(professorDTO.getLogin());
        this.setUser(professorDTO.getUser());
        this.coordenador = professorDTO.getCoordenador();
    }

    public boolean professorcordenador(){
        return this.coordenador;
    }

    public void setProcesso (Processo processo){
        processos.add(processo);
    }

    public void adicionarVoto(Voto voto) {
        votos.add(voto);
    }
}
