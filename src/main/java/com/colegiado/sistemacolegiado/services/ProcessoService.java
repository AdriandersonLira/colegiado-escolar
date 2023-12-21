package com.colegiado.sistemacolegiado.services;


import com.colegiado.sistemacolegiado.models.*;
import com.colegiado.sistemacolegiado.models.dto.CriarProcessoDTO;
import com.colegiado.sistemacolegiado.models.Voto.Voto;
import com.colegiado.sistemacolegiado.models.Voto.VotoId;
import com.colegiado.sistemacolegiado.models.dto.FiltrarProcessoDTO;
import com.colegiado.sistemacolegiado.models.dto.VotoDTO;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import com.colegiado.sistemacolegiado.models.enums.TipoDecisao;
import com.colegiado.sistemacolegiado.models.enums.TipoVoto;
import com.colegiado.sistemacolegiado.repositories.ProcessoRepositorio;
import com.colegiado.sistemacolegiado.repositories.ReuniaoRepositorio;
import com.colegiado.sistemacolegiado.repositories.VotoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProcessoService {
    final ProcessoRepositorio processoRepositorio;
    final ReuniaoRepositorio reuniaoRepositorio;
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

    public void votarProfessor(VotoDTO voto){
        Professor professor = this.professorService.encontrarPorId(voto.getIdProfessor());
        Processo processo = this.processoRepositorio.findById(voto.getIdProcesso())
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        VotoId votoId = new VotoId(voto.getIdProfessor(), voto.getIdProcesso());
        Voto votoFinal = new Voto(votoId, professor, processo, voto.getVoto(), voto.getTexto());

        this.votoRepositorio.save(votoFinal);
    }

    public Processo votarRelator(Integer idProcesso, TipoDecisao decisaoRelator, String texto){
        System.out.println(idProcesso);
        Processo processo = this.processoRepositorio.findById(idProcesso)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        processo.setParecer(decisaoRelator);
        processo.setJustificativa(texto);
        processo.setStatus(StatusProcesso.EM_JULGAMENTO);
        return this.processoRepositorio.save(processo);
    }

    public void votarColegiado(List<VotoDTO> votos){
        votos.forEach(this::votarProfessor);
        int votoComRelator = votos.stream().filter(voto -> voto.getVoto().equals(TipoVoto.COM_RELATOR)).toList().size();
        int votoDivergente = votos.stream().filter(voto -> voto.getVoto().equals(TipoVoto.DIVERGENTE)).toList().size();
        if(votoDivergente > votoComRelator){
            Processo processo = this.processoRepositorio.findById(votos.get(0).getIdProcesso())
                    .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
            processo.setParecer(processo.getParecer().equals(TipoDecisao.DEFERIDO) ? TipoDecisao.INDEFERIDO : TipoDecisao.DEFERIDO);
        }

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
        processo.setDataDistribuicao(LocalDate.now());
        processo.setStatus(StatusProcesso.DISTRIBUIDO);
        professor.setProcesso(processo);
        return processoRepositorio.save(processo);
    }

    public Optional<Processo> listprocessoscomoassunto (Assunto assunto){
        return processoRepositorio.findById(assunto.getId());

    }

    public List<Processo> filtrarprocesso (Aluno aluno, String nome, String data, StatusProcesso status){
        System.out.println(data);
        System.out.println(aluno);
        int idAluno = aluno.getId();

        if (StringUtils.hasText(nome) && StringUtils.hasText(data)  && status != null) {
            return processoRepositorio.filtrarRequerimentoDataStatus(idAluno, nome, status);
        } else if(StringUtils.hasText(nome)){
            return processoRepositorio.filtrarRequerimento(nome);
        } else if (StringUtils.hasText(data)) {
            return processoRepositorio.filtrarDataRecente(idAluno);
        } else if (status != null){
            return processoRepositorio.filtarStatus(status, aluno.getId());
        }

        return aluno.getProcessos();
    }

    public List<Processo> filtraprocessosdeumcolegiado (Integer colegiadoId, String statusFilter, String alunoFilter, String professorFilter){
        if (colegiadoId != null && (statusFilter != null || alunoFilter != null || professorFilter != null)) {
           /* return processoRepositorio.filtrarProcessos(
                    colegiadoId, statusFilter, alunoFilter, professorFilter);*/
        } else if (colegiadoId != null) {
            return processoRepositorio.filtrarProcessosDoColegiado(colegiadoId);
        } else {
            return processoRepositorio.findAll();
        }

        return processoRepositorio.findAll();
    }

    public List<Processo> filtraporcolegiado (Integer colegiadoId, Integer alunoId, Integer professorId, StatusProcesso statusProcesso){
        if(colegiadoId != null && alunoId != null && professorId != null && statusProcesso != null){
            return processoRepositorio.filtrarProcessostodos(colegiadoId, alunoId, professorId, statusProcesso);
        } else if (colegiadoId != null && statusProcesso != null) {
            return  processoRepositorio.filtrarprocessoPorStatusEColegiado(statusProcesso, colegiadoId);

        } else if (colegiadoId != null && alunoId != null && professorId != null) {
            // Ambos os parâmetros foram fornecidos, chame a consulta personalizada
            return processoRepositorio.filtrarPorColegiadoAlunoEProfessor(colegiadoId, alunoId, professorId);
        } else if (colegiadoId != null && alunoId != null) {
            return processoRepositorio.filtrarPorColegiadoEAluno(colegiadoId, alunoId);
        } else if (colegiadoId != null && professorId != null) {
            return processoRepositorio.filtrarPorColegiadoEProfessor(colegiadoId, professorId);
        } else if (colegiadoId != null) {
            // Somente colegiadoId foi fornecido, chame a consulta por colegiado
            return processoRepositorio.filtrarprocessocolegiado(colegiadoId);
        } else {
            // Nenhum parâmetro foi fornecido, retorne a lista completa de processos
            return listarProcessos();
        }
    }

    public void processarVotos(int processoId, Map<String, String> votos) {
        Processo processo = processoRepositorio.findById(processoId).orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
        processo.setStatus(StatusProcesso.JULGADO);
        Reuniao reuniao = processo.getReuniao();
        reuniao.setStatus(StatusReuniao.ENCERRADA);

        // Contadores de votos
        int votosComRelator = 0;
        int votosDivergentes = 0;

        // Loop através dos votos
        for (Map.Entry<String, String> entry : votos.entrySet()) {
            String professorNome = entry.getKey();
            String escolhaVoto = entry.getValue();

            // Verifica a escolha de voto e atualiza os contadores
            if ("COM_RELATOR".equals(escolhaVoto)) {
                votosComRelator++;
            } else if ("DIVERGENTE".equals(escolhaVoto)) {
                votosDivergentes++;
            }
        }

        // Define o novo parecer com base na maioria dos votos e no parecer do professor relator
        if (votosComRelator > votosDivergentes && processo.getParecer() == TipoDecisao.DEFERIDO) {
            processo.setParecer(TipoDecisao.DEFERIDO);
        } else if (votosDivergentes > votosComRelator && processo.getParecer() == TipoDecisao.DEFERIDO) {
            processo.setParecer(TipoDecisao.INDEFERIDO);
        } else if (votosComRelator > votosDivergentes && processo.getParecer() == TipoDecisao.INDEFERIDO) {
            processo.setParecer(TipoDecisao.INDEFERIDO);
        } else if (votosDivergentes > votosComRelator && processo.getParecer() == TipoDecisao.INDEFERIDO) {
            processo.setParecer(TipoDecisao.DEFERIDO);
        }

        // Salva o processo com o novo parecer
        reuniaoRepositorio.save(reuniao);
        processoRepositorio.save(processo);
    }
}
