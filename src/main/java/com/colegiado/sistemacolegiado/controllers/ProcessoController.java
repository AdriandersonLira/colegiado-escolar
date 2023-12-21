package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.*;
import com.colegiado.sistemacolegiado.models.dto.CriarProcessoDTO;
import com.colegiado.sistemacolegiado.models.dto.FiltrarProcessoDTO;
import com.colegiado.sistemacolegiado.models.dto.ProcessoDTO;
import com.colegiado.sistemacolegiado.models.dto.VotoDTO;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import com.colegiado.sistemacolegiado.models.enums.TipoDecisao;
import com.colegiado.sistemacolegiado.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/processos")
@AllArgsConstructor
public class ProcessoController {
    final ProcessoService processoService;
    final ColegiadoService colegiadoService;
    final ProfessorService professorService;
    final AlunoService alunoService;
    final AssuntoService assuntoService;

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{idAluno}")
    public ProcessoDTO criarProcesso(@PathVariable Integer idAluno, @RequestBody CriarProcessoDTO processo){
        processo.setIdAluno(idAluno);
        return new ProcessoDTO(processoService.criarProcesso(processo));
    }

    @GetMapping("/filtro/aluno/{idAluno}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessoDTO> listarProcessosAluno(@PathVariable Integer idAluno,@RequestBody FiltrarProcessoDTO filtro){
        filtro.setIdAluno(idAluno);
        return processoService.listarProcessos(filtro).stream().map(ProcessoDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/filtro/professor/{idProfessor}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessoDTO> listarProcessosProcesso(@PathVariable Integer idProfessor, @RequestBody FiltrarProcessoDTO filtro){
        filtro.setIdProfessor(idProfessor);
        return processoService.listarProcessos(filtro).stream().map(ProcessoDTO::new).collect(Collectors.toList());
    }


    @GetMapping("/filtro/coordenador")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<ProcessoDTO> listarProcessosCoordenador(@RequestBody FiltrarProcessoDTO filtro){
        return processoService.listarProcessos(filtro).stream().map(ProcessoDTO::new).collect(Collectors.toList());
    }

    @PostMapping("/atribuir")
    public ModelAndView atribuirProcesso(@RequestParam Integer idProfessor,
                                         @RequestParam Integer idProcesso, ModelAndView modelAndView, BindingResult bindingResult, RedirectAttributes attr){

        //new ProcessoDTO(processoService.atribuirProcesso(idProcesso, idProfessor));

        if(!colegiadoService.temcolegiado(idProfessor)){
            attr.addFlashAttribute("message", "Error: Professor não faz parte do colegiado!");
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/professores");
            return modelAndView;
        }

        try {
            processoService.atribuirProcesso(idProcesso, idProfessor);
            attr.addFlashAttribute("message", "OK: Processo atribuído com sucesso!");
            attr.addFlashAttribute("error", "false");
        } catch (RuntimeException e) {
            e.printStackTrace();
            attr.addFlashAttribute("message", "Error: " + e.getMessage());
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/professores");
        }

        modelAndView.setViewName("redirect:/professores");
        return modelAndView;
    }

    @GetMapping
    public ModelAndView listarprocesso (){
        ModelAndView mv = new ModelAndView("processos/index");
        List<Processo> processos = processoService.listarProcessos();
        mv.addObject("processos", processos);
        return mv;
    }

    @GetMapping("/cadastrar")
    public ModelAndView cadastrarprocesso(){
        ModelAndView mv = new ModelAndView("processos/new");


        return mv;
    }

    @PostMapping("/criarprocesso/aluno")
    public ModelAndView criarprocessoaluno(@RequestParam("alunoId") int alunoId,
                                           @RequestParam("idAssunto") int idAssunto){
        ModelAndView mv = new ModelAndView("alunos/listarprocessoaluno");

        //Aluno aluno = (Aluno) session.getAttribute("aluno");
        //Assunto assunto = (Assunto) session.getAttribute("assunto");

        Aluno aluno = alunoService.encontrarPorId(alunoId);
        Assunto assunto = assuntoService.encontrarPorId(idAssunto);


        //System.out.println(aluno);
        //System.out.println(assunto);

        CriarProcessoDTO processoDTO = new CriarProcessoDTO(assunto.getAssunto(), assunto.getId(), aluno.getId());
        Processo processo = processoService.criarProcesso(processoDTO);
        Aluno testalunoBanco = processoService.setProcessoNoAluno(aluno, processo);

        mv.addObject("processos", testalunoBanco.getProcessos());
        mv.addObject("Aluno", aluno);

        System.out.println(processoDTO);
        System.out.println(testalunoBanco);

        return mv;
    }

    @GetMapping("/filtrar/{id}")
    public ModelAndView filtrar (@PathVariable int id, @RequestParam(name = "requerimentoFilter", required = false) String nome,
                                 @RequestParam(name = "dataFilter", required = false) String dataFilter,
                                 @RequestParam (name = "statusFilter", required = false) StatusProcesso status) {

        ModelAndView mv = new ModelAndView("alunos/listarprocessoaluno");
        System.out.println("oiiiiii");
        System.out.println(dataFilter);
        System.out.println(nome);
        System.out.println(id);
        Aluno aluno = alunoService.encontrarPorId(id);
        List<Processo> processosfiltrados = processoService.filtrarprocesso(aluno, nome, dataFilter, status);


        for(Processo processo : processosfiltrados){
            System.out.println(processo.toString());
        }


        mv.addObject("processos", processosfiltrados);
        mv.addObject("statusProcesso", StatusProcesso.values());
        mv.addObject("Aluno", aluno);
        return mv;
    }

    @PostMapping("/colegiado/votar")
    public ModelAndView votarColegiado(List<VotoDTO> votos){
        processoService.votarColegiado(votos);
        return null;
    }

    @GetMapping ("{id}/listarprocessosdoprofessor")
    public ModelAndView listarprocesso (@PathVariable Integer id){
        ModelAndView mv = new ModelAndView("professores/processosdoprofessor");
        Professor professor = professorService.encontrarPorId(id);
        List<Processo> processos = professor.getProcessos();
        mv.addObject("processos", processos);
        mv.addObject("professor", professor);
        return mv;
    }

    @GetMapping("votar/{id}")
    public ModelAndView votarprocesso (@PathVariable Integer id){
        ModelAndView mv = new ModelAndView("professores/votar");
        Optional<Processo> processoOptional = processoService.encontrarPorId(id);

        if(processoOptional.isPresent()){
            mv.addObject("ProcessoEmVoto", processoOptional.get());
            mv.addObject("statusDecisao", TipoDecisao.values());
            return mv;
        }
        mv.setViewName("professores/processosdoprofessor");
        return mv;
    }

    @PostMapping("/votar")
    public ModelAndView realizarvoto (@ModelAttribute("ProcessoEmVoto") Processo processoEmVoto){
        System.out.println(processoEmVoto.getId());

        Processo processo = processoService.votarRelator(processoEmVoto.getId(), processoEmVoto.getParecer(), processoEmVoto.getJustificativa());
        System.out.println(processo.getParecer().getTipoDecisao());
        ModelAndView mv = new ModelAndView("professores/processosdoprofessor");
        Professor professor = professorService.encontrarPorId(processo.getProfessor().getId());
        List<Processo> processos = professor.getProcessos();
        mv.addObject("processos", processos);
        mv.addObject("professor", professor);
        return mv;
    }

    @PostMapping("/votar/professorcolegiado")
    public ModelAndView votoprofessoresdocolegiado(@RequestParam int processoId, @RequestParam Map<String, String> votos) {
        // Lógica para processar os votos
        ModelAndView mv = new ModelAndView("processos/index");
        List<Processo> processos = processoService.listarProcessos();
        processoService.processarVotos(processoId, votos);
        mv.addObject("processos", processos);

        // Redireciona de volta para a página de listagem de processos
        return mv;
    }

    @GetMapping("/iniciar-reuniao/votacaoprocesso/{id}")
    public ModelAndView iniciarvotacaoprocessodeumareuniao (@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView("reunioes/iniciodareuniao");

        Optional<Processo> processoOptional = processoService.encontrarPorId(id);
        if(processoOptional.isPresent()){
            Processo processo = processoOptional.get();
            List<Professor> professoresdocolegiado = processo.getProfessor().getColegiado().getProfessores();
            mv.addObject("processo", processo);
            mv.addObject("professores", professoresdocolegiado);

        }
        return mv;
    }

    @GetMapping("/telaconsultaprocessoscolegiado")
    public ModelAndView telaconsultaprocessoscolegiado (){
        ModelAndView mv = new ModelAndView("processos/telacoordenadorverprocessosdocolegiado");

        //List<Processo> processos = processoService.listarProcessos();
        List<Colegiado> colegiados = colegiadoService.listarColegiado();
        List<Aluno> alunos = alunoService.listarAlunos();
        List<Professor> professores = professorService.listarProfessores();

        mv.addObject("statusProcesso", StatusProcesso.values());
        mv.addObject("colegiados", colegiados);
        mv.addObject("alunos", alunos);
        mv.addObject("professores", professores);

        return mv;
    }

    @GetMapping("/listarPorColegiado")
    public ModelAndView listarPorColegiado() {
        ModelAndView mv = new ModelAndView("processos/telacoordenadorverprocessosdocolegiado");
        List<Colegiado> colegiados = colegiadoService.listarColegiado();
        List<Aluno> alunos = alunoService.listarAlunos();
        List<Professor> professores = professorService.listarProfessores();
       mv.addObject("colegiados", colegiados);
        mv.addObject("alunos", alunos);
        mv.addObject("professores", professores);
        mv.addObject("Statusprocesso", StatusProcesso.values());
        return mv;
    }

    @GetMapping("/porColegiado")
    public ModelAndView filtrarPorColegiado(@RequestParam(name = "colegiadoId", required = false) Integer colegiadoId,  @RequestParam(name = "alunoId", required = false) Integer alunoId, @RequestParam(name = "professorId", required = false) Integer professorId, @RequestParam(name = "statusId", required = false) StatusProcesso statusId) {
        ModelAndView mv = new ModelAndView("processos/telacoordenadorverprocessosdocolegiado");
        List<Processo> processos = processoService.filtraporcolegiado(colegiadoId, alunoId, professorId, statusId);
        List<Colegiado> colegiados = colegiadoService.listarColegiado();
        List<Professor> professores = professorService.listarProfessores();
        List<Aluno> alunos = alunoService.listarAlunos();
        mv.addObject("processos", processos);

        mv.addObject("colegiados", colegiados);
        mv.addObject("alunos", alunos);
        mv.addObject("professores", professores);
        mv.addObject("Statusprocesso", StatusProcesso.values());

        return mv;
    }


}
