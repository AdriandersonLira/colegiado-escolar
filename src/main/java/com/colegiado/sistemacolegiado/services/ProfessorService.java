package com.colegiado.sistemacolegiado.services;

import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Professor;
import com.colegiado.sistemacolegiado.models.User;
import com.colegiado.sistemacolegiado.models.dto.ProfessorDTO;
import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import com.colegiado.sistemacolegiado.repositories.ColegiadoRepositorio;
import com.colegiado.sistemacolegiado.repositories.ProfessorRepositorio;
import com.colegiado.sistemacolegiado.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfessorService {
    final ProfessorRepositorio professorRepositorio;
    final ColegiadoRepositorio colegiadoRepositorio;
    final UserRepository userRepository;


    @Transactional
    public Professor criarProfessor(UsuarioDTO professorDTO, Integer idcolegiado){
        if(idcolegiado != null){
            Colegiado colegiado = colegiadoRepositorio.findById(idcolegiado)
                    .orElseThrow(() -> new EntityNotFoundException("Colegiado não encontrado"));
            Professor professor = new Professor(professorDTO);

            professor.setColegiado(colegiado);
            colegiado.adicionarProfessorNoColegiado(professor);
            colegiadoRepositorio.save(colegiado);
            return professorRepositorio.save(professor);

        }

        Professor professor = new Professor(professorDTO);
        return professorRepositorio.save(professor);
    }

    public Professor encontrarPorId(int id){
        return professorRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    public List<Professor> listarProfessores(){
        return professorRepositorio.findAll();
    }

    public List<User> findEnabledUsers() {
        return userRepository.findByEnabledTrue();
    }

    public void deletarProfessores(Professor professor){
        professorRepositorio.delete(professor);
    }

    public boolean verificarTelefone(String fone){
        return professorRepositorio.existsByfone(fone);
    }
    public boolean verificarMatricula(String matricula) {
        return  professorRepositorio.existsBymatricula((matricula));
    }

    public  boolean verificarLogin(String login){
        return professorRepositorio.existsBylogin(login);
    }

    public Professor atualizarProfessor(Integer id, UsuarioDTO professorDTO) {
        Professor professor = encontrarPorId(id);
        professor.setNome(professorDTO.getNome());
        professor.setFone(professorDTO.getFone());
        professor.setMatricula(professorDTO.getMatricula());
        professor.setLogin(professorDTO.getLogin());
        professor.setUser(professorDTO.getUser());
        professor.setCoordenador(professorDTO.getCoordenador());
        return professorRepositorio.save(professor);
    }
}
