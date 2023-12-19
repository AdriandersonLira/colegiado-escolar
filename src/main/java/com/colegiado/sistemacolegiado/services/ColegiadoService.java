package com.colegiado.sistemacolegiado.services;

import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Professor;
import com.colegiado.sistemacolegiado.models.dto.CriarColegiadoDTO;
import com.colegiado.sistemacolegiado.repositories.ColegiadoRepositorio;
import com.colegiado.sistemacolegiado.repositories.ProfessorRepositorio;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ColegiadoService {
    final ColegiadoRepositorio colegiadoRepositorio;
    final ProfessorRepositorio professorRepositorio;

    public Colegiado criarColegiado(CriarColegiadoDTO colegiadoDTO){
        return this.colegiadoRepositorio.save(new Colegiado(colegiadoDTO));
    }

    public List<Colegiado> listarColegiado(){
        return this.colegiadoRepositorio.findAll();
    }

    public boolean temcolegiado(Integer id){
        Professor verificarprofessor = professorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        if(verificarprofessor.getColegiado() != null){
            return true;
        }
        return false;
    }

    public Colegiado encontrarPorId(int id){
        return this.colegiadoRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Colegiado não encotrado"));
    }

    public void deletarColegiado(Integer id){
        var colegiado = encontrarPorId(id);
        colegiado.getProfessores().clear();
        colegiadoRepositorio.save(colegiado);
        this.colegiadoRepositorio.delete(colegiado);
    }

    public Colegiado adicionarProfessor(int idColegiado, int idProfessor){
        Optional<Professor> professorOptional = professorRepositorio.findById(idProfessor);
        Professor professor = professorOptional.get();
        Colegiado colegiado = encontrarPorId(idColegiado);
        if(professor.getColegiado() != null){
            Colegiado antigocolegiado = professor.getColegiado();
            antigocolegiado.removerProfessorDoColegiado(professor);
        }
        colegiado.adicionarProfessorNoColegiado(professor);
        professor.setColegiado(colegiado);
        professorRepositorio.save(professor);
        return this.colegiadoRepositorio.save(colegiado);
    }

    public Colegiado removerProfessor(int idColegiado, int idProfessor){
        Professor professor = professorRepositorio.findById(idProfessor).orElseThrow(() -> new RuntimeException("Professor não encontrado"));
        Colegiado colegiado = encontrarPorId(idColegiado);
        if (colegiado.getProfessores().contains(professor)) {
            colegiado.getProfessores().remove(professor);
        } else {
            throw new RuntimeException("O professor não está associado a este colegiado");
        }
        return this.colegiadoRepositorio.save(colegiado);
    }

    public Colegiado atualizarColegiado(Integer id, CriarColegiadoDTO colegiadoDTO) {
        Colegiado colegiado = encontrarPorId(id);
        colegiado.setDescricao(colegiadoDTO.getDescricao());
        colegiado.setPortaria(colegiadoDTO.getPortaria());
        colegiado.setCurso(colegiadoDTO.getCurso());
        return colegiadoRepositorio.save(colegiado);
    }
}
