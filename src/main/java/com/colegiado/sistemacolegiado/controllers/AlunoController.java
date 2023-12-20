package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.*;
import com.colegiado.sistemacolegiado.models.dto.AlunoDTO;
import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import com.colegiado.sistemacolegiado.services.AlunoService;
import com.colegiado.sistemacolegiado.services.AssuntoService;
import com.colegiado.sistemacolegiado.ui.NavPage;
import com.colegiado.sistemacolegiado.ui.NavPageBuilder;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/alunos")
public class AlunoController {

    final AlunoService alunoService;
    final AssuntoService assuntoService;

    public AlunoController(AlunoService alunoService, AssuntoService assuntoService) {
        this.alunoService = alunoService;
        this.assuntoService = assuntoService;
    }

    @PostMapping
    public ModelAndView criarAluno(ModelAndView modelAndView, @Valid UsuarioDTO aluno,  BindingResult bindingResult, RedirectAttributes attr) {

        if (alunoService.verificarTelefone(aluno.getFone())) {
            attr.addFlashAttribute("message", "Conflict: Telefone já existe no banco");
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/alunos/new");
        }

        if (alunoService.verificarMatricula(aluno.getMatricula())) {
            attr.addFlashAttribute("message", "Conflict: Matricula já existe no banco");
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/alunos/new");
        }

        if (alunoService.verificarLogin(aluno.getLogin())) {
            attr.addFlashAttribute("message", "Conflict: Login já existe no banco");
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/alunos/new");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("alunos/new");
        } else {
            alunoService.criarAluno(aluno);
            attr.addFlashAttribute("message", "Aluno cadastrado com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/alunos");
        }

        return modelAndView;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAluno(@PathVariable(value = "id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(alunoService.encontrarPorId(id));
    }

    @GetMapping("listarprocesso/{id}")
    public ModelAndView listarprocessoAluno(@PathVariable int id){
        ModelAndView mv = new ModelAndView("alunos/listarprocessoaluno");
            Aluno aluno = alunoService.encontrarPorId(id);
            mv.addObject("processos", aluno.getProcessos());
            mv.addObject("statusProcesso", StatusProcesso.values());
            mv.addObject("Aluno", aluno);

        return mv;
    }

    @GetMapping("/new")
    public ModelAndView current(ModelAndView modelAndView, UsuarioDTO alunoDTO) {
        modelAndView.setViewName("alunos/new");
        modelAndView.addObject("alunoDTO", alunoDTO);

        return modelAndView;
    }

    @GetMapping
    public ModelAndView getAlunos(
            ModelAndView modelAndView,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "4") int size
        ) {
        Pageable paging = PageRequest.of(page-1, size);
        Page<Aluno> pageAlunos = alunoService.listarAlunosPagination(paging);

        NavPage navPage = NavPageBuilder.newNavPage(pageAlunos.getNumber() + 1,
                pageAlunos.getTotalElements(), pageAlunos.getTotalPages(), size);

        modelAndView.addObject("navPage", navPage);
        modelAndView.addObject("alunos", pageAlunos);
        modelAndView.setViewName("alunos/index");
        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable int id, ModelAndView modelAndView, RedirectAttributes attr) {
        try {
            Aluno aluno = alunoService.encontrarPorId(id);

            var request = new AlunoDTO(aluno);

            modelAndView.setViewName("alunos/edit");
            modelAndView.addObject("alunoId", aluno.getId());
             modelAndView.addObject("aluno", request);

        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/alunos");
        }

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView atualizarAluno(ModelAndView modelAndView, @PathVariable Integer id, @Valid UsuarioDTO aluno, BindingResult bindingResult, RedirectAttributes attr){

        if (bindingResult.hasErrors()) {
            ModelAndView mv = new ModelAndView ("alunos/edit");
            Aluno aluno1 = alunoService.encontrarPorId(id);
            var request = new AlunoDTO(aluno1);
            mv.addObject("alunoId", aluno1.getId());
            mv.addObject("aluno", request);
            return mv;
        } else {

            attr.addFlashAttribute("message", "OK: Aluno editado com sucesso!");
            attr.addFlashAttribute("error", "false");
            Aluno alunoEdit = alunoService.atualizarAluno(id, aluno);
            System.out.println(alunoEdit);

            modelAndView.setViewName("redirect:/alunos");
        }

        return modelAndView;
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deletarAluno(ModelAndView modelAndView, @PathVariable (value = "id") int id, RedirectAttributes attr){
        try {
            var alunoExistente = alunoService.encontrarPorId(id);
            alunoService.deletarAluno(alunoExistente);
            attr.addFlashAttribute("message", "OK: Aluno excluído com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/alunos");
        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/alunos");
        }

        return modelAndView;
    }

    @GetMapping("/cadastrarprocesso/{alunoId}")
    public ModelAndView cadastrarprocesso(@PathVariable int alunoId, RedirectAttributes attr){
        ModelAndView mv = new ModelAndView("redirect:/alunos/cadastrarprocesso");
        try{
            Aluno aluno = alunoService.encontrarPorId(alunoId);
            List<Assunto> assuntos = assuntoService.listarAssuntos();

            mv.addObject("alunoId", aluno.getId());  // Adicione o ID do aluno ao modelo
            mv.addObject("alunoNome", aluno.getNome());  // Adicione o nome do aluno ao modelo
            mv.addObject("assuntos", assuntos);
            /*System.out.println("oiiii");
            System.out.println(aluno);*/




        }catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            mv.setViewName("redirect:/alunos");
        }
        return mv;
    }

    @GetMapping("/processocadastro/{id}")
    public ModelAndView processocadastro(@PathVariable int id){
        ModelAndView mv = new ModelAndView("alunos/cadastraprocesso");
        try{
            Aluno aluno = alunoService.encontrarPorId(id);
            List<Assunto> assuntos = assuntoService.listarAssuntos();

            mv.addObject("alunoId", aluno.getId());  // Adicione o ID do aluno ao modelo
            mv.addObject("aluno", aluno);  // Adicione o nome do aluno ao modelo
            mv.addObject("assuntos", assuntos);
            /*System.out.println("oiiii");
            System.out.println(aluno);*/




        }catch (Exception e) {
            /*attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");*/
            mv.setViewName("redirect:/alunos");
        }
        return mv;
    }



    @PostMapping("/cadastrarprocesso/{id}")
    public ModelAndView cadastrarProcessoAluno (@PathVariable int id){

        ModelAndView mv = new ModelAndView("alunos/cadastrarprocesso");
        alunoService.encontrarPorId(id);

        return mv;
    }

    @ModelAttribute("users")
    public List<User> getUserOptions() {
        return alunoService.findEnabledUsers();
    }
}