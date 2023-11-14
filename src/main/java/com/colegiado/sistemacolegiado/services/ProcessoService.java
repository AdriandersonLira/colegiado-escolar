package com.colegiado.sistemacolegiado.services;


import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.Assunto;
import com.colegiado.sistemacolegiado.models.dto.CriarProcessoDTO;
import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.Professor;
import com.colegiado.sistemacolegiado.models.Voto.Voto;
import com.colegiado.sistemacolegiado.models.Voto.VotoId;
import com.colegiado.sistemacolegiado.models.dto.FiltrarProcessoDTO;
import com.colegiado.sistemacolegiado.models.enums.TipoDecisao;
import com.colegiado.sistemacolegiado.models.enums.TipoVoto;
import com.colegiado.sistemacolegiado.repositories.ProcessoRepositorio;
import com.colegiado.sistemacolegiado.repositories.VotoRepositorio;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProcessoService {
    final ProcessoRepositorio processoRepositorio;
    final ProfessorService professorService;
    final VotoRepositorio votoRepositorio;
    final AlunoService alunoService;
    final AssuntoService assuntoService;

    public Processo criarProcesso(CriarProcessoDTO processoDTO){
        var assunto = assuntoService.encontrarPorId(processoDTO.getIdAssunto());
        var aluno = alunoService.encontrarPorId(processoDTO.getIdAluno());
        return this.processoRepositorio.save(new Processo(processoDTO, aluno, assunto));
    }

    @Transactional
    public Aluno setProcessoNoAluno(Aluno aluno, Processo processo){
        Aluno alunoX  = alunoService.encontrarPorId(aluno.getId());
        alunoX.setProcessoDoAluno(processo);
        return alunoX;
    }

    public Processo criarProcessoaluno(CriarProcessoDTO processoDTO){
        var assunto = assuntoService.encontrarPorId(processoDTO.getIdAssunto());
        var aluno = alunoService.encontrarPorId(processoDTO.getIdAluno());
        return this.processoRepositorio.save(new Processo(processoDTO, aluno, assunto));
    }

    public Optional<Processo> encontrarPorId(int id){
        return this.processoRepositorio.findById(id);
    }

    public List<Processo> listarProcessos(){
        return this.processoRepositorio.findAll();
    }

    public void deletarProcesso(Processo processo){
        if (processo.getId() != null && this.processoRepositorio.existsById(processo.getId())) {
            processo.setAssunto(null);
            processo.setAluno(null);
            processo.setReuniao(null);
            processo.setProfessor(null);
            this.processoRepositorio.delete(processo);
        } else {
            throw new RuntimeException("O processo não existe");
        }
    }

    public Processo mudarDecisao(int id, TipoDecisao novaDecisao){
        Optional<Processo> processoOptional = this.processoRepositorio.findById(id);
        if (processoOptional.isPresent()) {
            Processo processo = processoOptional.get();
            processo.setParecer(novaDecisao);
            return this.processoRepositorio.save(processo);
        } else {
            throw new RuntimeException("Processo não encontrado");
        }
    };

    public void votar(int idProfessor, int idProcesso, TipoVoto voto){
        Professor professor = this.professorService.encontrarPorId(idProfessor);
        Processo processo = this.processoRepositorio.findById(idProcesso)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        VotoId votoId = new VotoId(idProfessor, idProcesso);
        Voto votoFinal = new Voto(votoId, professor, processo, voto);
        this.votoRepositorio.save(votoFinal);
    }

    public List<Processo> listarProcessos(FiltrarProcessoDTO filtro) {
        Assunto assunto = null;
        if (filtro.getIdAssunto() != null){
            assunto = assuntoService.encontrarPorId(filtro.getIdAssunto());
        }
        if (filtro.getIdAluno() != null){
            var aluno = alunoService.encontrarPorId(filtro.getIdAluno());
            return processoRepositorio.findByStatusAndAssuntoAndAlunoOrderByDataRecepcao(filtro.getStatus(), assunto, aluno);
        } else if (filtro.getIdProfessor() != null){
            var professor = professorService.encontrarPorId(filtro.getIdProfessor());
            return processoRepositorio.findByStatusAndAssuntoAndProfessorOrderByDataRecepcao(filtro.getStatus(), assunto, professor);
        } else {
            throw new RuntimeException("Aluno ou professor não informados.");
        }
    }

    public List<Processo> listarProcessosCoordenador(FiltrarProcessoDTO filtro) {
        Aluno aluno = null;
        Professor professor = null;
        if (filtro.getIdAluno() != null){
            aluno = alunoService.encontrarPorId(filtro.getIdAluno());
        }
        if (filtro.getIdProfessor() != null){
            professor = professorService.encontrarPorId(filtro.getIdProfessor());
        }
        return processoRepositorio.findAllByAlunoAndProfessorAndStatusOrderByDataRecepcao(aluno, professor, filtro.getStatus());
    }

    public Processo atribuirProcesso(Integer idProcesso, Integer idProfessor) {
        var processo = processoRepositorio.findById(idProcesso).orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        var professor = professorService.encontrarPorId(idProfessor);
        if (professor.getColegiado() == null){
            throw new RuntimeException("Professor não faz parte de colegiado");
        }
        processo.setProfessor(professor);
        return processoRepositorio.save(processo);
    }

    public Optional<Processo> listprocessoscomoassunto (Assunto assunto){
        return processoRepositorio.findById(assunto.getId());

    }
}
