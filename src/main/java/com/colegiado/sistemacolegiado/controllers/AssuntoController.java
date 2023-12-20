package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.Aluno;
import com.colegiado.sistemacolegiado.models.Assunto;
import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.dto.AlunoDTO;
import com.colegiado.sistemacolegiado.models.dto.CriarAssuntoDTO;
import com.colegiado.sistemacolegiado.models.dto.ProcessoDTO;
import com.colegiado.sistemacolegiado.models.dto.UsuarioDTO;
import com.colegiado.sistemacolegiado.services.AssuntoService;
import com.colegiado.sistemacolegiado.services.ProcessoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/assuntos")
public class AssuntoController {
    private final AssuntoService assuntoService;

    @Autowired
    ProcessoService processoService;


    @GetMapping
    public ModelAndView listarAssuntos(ModelAndView modelAndView) {
        List<Assunto> assuntos = assuntoService.listarAssuntos();

        modelAndView.setViewName("assuntos/index");
        modelAndView.addObject("assuntos", assuntos);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView criarassunto() {

        ModelAndView mv = new ModelAndView("assuntos/new");
        CriarAssuntoDTO assuntoDTO = new CriarAssuntoDTO();
        mv.addObject("assuntoDTO", assuntoDTO);

        return mv;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable int id, ModelAndView modelAndView, RedirectAttributes attr) {
        try {
            Assunto assunto = assuntoService.encontrarPorId(id);

            var request = new CriarAssuntoDTO(assunto);

            modelAndView.setViewName("assuntos/edit");
            modelAndView.addObject("assuntoId", assunto.getId());
            modelAndView.addObject("assunto", request);

        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/assuntos");
        }

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView atualizarAluno(ModelAndView modelAndView, @PathVariable Integer id, @Valid CriarAssuntoDTO assunto, BindingResult bindingResult, RedirectAttributes attr){

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:/assuntos/{id}/edit");
        } else {

            attr.addFlashAttribute("message", "OK: Assunto editado com sucesso!");
            attr.addFlashAttribute("error", "false");
            assuntoService.atualizarAssunto(id, assunto);

            modelAndView.setViewName("redirect:/assuntos");
        }

        return modelAndView;
    }

    @PostMapping
    public ModelAndView criarAssunto(ModelAndView modelAndView, @Valid CriarAssuntoDTO assuntoDTO, BindingResult bindingResult, RedirectAttributes attr, HttpSession session) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("assuntos/new");
        } else {
            /*Aluno aluno = (Aluno) session.getAttribute("aluno");
            System.out.println(aluno);*/
            Assunto assunto = assuntoService.criarAssunto(assuntoDTO);
            //session.setAttribute("assunto", assunto);
            attr.addFlashAttribute("message", "Assunto cadastrado com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/assuntos");
        }

        return modelAndView;
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deletarAssunto(ModelAndView modelAndView, @PathVariable (value = "id") int id, RedirectAttributes attr){
        try {
            var assuntoExistente = assuntoService.encontrarPorId(id);

            if(assuntoExistente != null){
                Optional<Processo> processoscomassunto = processoService.listprocessoscomoassunto(assuntoExistente);
                /*processoscomassunto.get().setAssunto(null);
                processoscomassunto.get().setAluno(null);
                processoscomassunto.get().setReuniao(null);
                processoscomassunto.get().setProfessor(null);*/
                processoService.deletarProcesso(processoscomassunto.get());
            }
            assuntoService.deletarAssunto(assuntoExistente);
            attr.addFlashAttribute("message", "OK: Assunto excluído com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/assuntos");
        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/assuntos");
        }

        return modelAndView;
    }

    @PutMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Assunto atualizarAssunto(@PathVariable Integer id,
                                    @RequestBody @Valid CriarAssuntoDTO assuntoDTO){
        return assuntoService.atualizarAssunto(id, assuntoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarAssunto(@PathVariable (value = "id") int id){
        var assuntoExistente = assuntoService.encontrarPorId(id);

        assuntoService.deletarAssunto(assuntoExistente);
        return ResponseEntity.status(HttpStatus.OK).body("OK: Assunto excluído com sucesso!");
    }

    @GetMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Assunto buscaAssunto(@PathVariable Integer id){
        return assuntoService.encontrarPorId(id);
    }


}
