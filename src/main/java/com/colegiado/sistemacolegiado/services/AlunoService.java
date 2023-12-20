package com.colegiado.sistemacolegiado.services;

import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.User;
import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import com.colegiado.sistemacolegiado.repositories.AlunoRepositorio;
import com.colegiado.sistemacolegiado.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    final AlunoRepositorio alunoRepository;
    final UserRepository userRepository;



    public AlunoService (AlunoRepositorio alunoRepository, UserRepository userRepository){
        this.alunoRepository = alunoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Aluno criarAluno(UsuarioDTO alunoDTO){
        return  this.alunoRepository.save(new Aluno(alunoDTO));
    }

    public Aluno encontrarPorId(int id){
        return alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    public List<Aluno> listarAlunos(){
        return  alunoRepository.findAll();
    }

    public Page<Aluno> listarAlunosPagination(Pageable paging){
        return  alunoRepository.findAll(paging);
    }

    public List<User> findEnabledUsers() {
        return userRepository.findByEnabledTrue();
    }

    public void deletarAluno(Aluno aluno){
        alunoRepository.delete(aluno);
    }

    public boolean verificarTelefone(String fone){
        return alunoRepository.existsByfone(fone);
    }

    public boolean verificarMatricula(String matricula) {
        return  alunoRepository.existsBymatricula((matricula));
    }

    public  boolean verificarLogin(String login){
        return alunoRepository.existsBylogin(login);
    }


    public Aluno atualizarAluno(Integer id, UsuarioDTO alunoDTO) {
        Aluno aluno = encontrarPorId(id);
        aluno.setNome(alunoDTO.getNome());
        aluno.setFone(alunoDTO.getFone());
        aluno.setMatricula(alunoDTO.getMatricula());
        aluno.setLogin(alunoDTO.getLogin());
        aluno.setUser(alunoDTO.getUser());
        return alunoRepository.save(aluno);
    }


}
