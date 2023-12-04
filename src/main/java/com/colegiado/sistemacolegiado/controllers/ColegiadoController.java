package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.Assunto;
import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Professor;
import com.colegiado.sistemacolegiado.models.dto.ColegiadoDTO;
import com.colegiado.sistemacolegiado.models.dto.CriarAssuntoDTO;
import com.colegiado.sistemacolegiado.models.dto.CriarColegiadoDTO;
import com.colegiado.sistemacolegiado.services.ColegiadoService;
import com.colegiado.sistemacolegiado.services.ProfessorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/colegiados")
@AllArgsConstructor
public class ColegiadoController {

    private final ColegiadoService colegiadoService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    public ModelAndView getColegiados(ModelAndView modelAndView) {
        List<Colegiado> colegiados = colegiadoService.listarColegiado();

        modelAndView.setViewName("colegiados/index");
        modelAndView.addObject("colegiados", colegiados);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView criarColegiado(ModelAndView modelAndView, ColegiadoDTO colegiadoDTO) {
        List<Colegiado> colegiados = colegiadoService.listarColegiado();

        modelAndView.setViewName("colegiados/new");
        modelAndView.addObject("colegiados", colegiados);
        modelAndView.addObject("colegiadoDTO", colegiadoDTO);

        return modelAndView;
    }

    @GetMapping("/{id}/delete")
    public ModelAndView deletarAssunto(ModelAndView modelAndView, @PathVariable (value = "id") int id, RedirectAttributes attr){
        try {
            colegiadoService.deletarColegiado(id);
            attr.addFlashAttribute("message", "OK: Colegiado excluído com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/colegiados");
        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/colegiados");
        }

        return modelAndView;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable int id, ModelAndView modelAndView, RedirectAttributes attr) {
        try {
            Colegiado colegiado = colegiadoService.encontrarPorId(id);

            var request = new CriarColegiadoDTO(colegiado);

            modelAndView.setViewName("colegiados/edit");
            modelAndView.addObject("colegiadoId", colegiado.getId());
            modelAndView.addObject("colegiado", request);

        } catch (Exception e) {
            attr.addFlashAttribute("message", "Error: "+e);
            attr.addFlashAttribute("error", "true");
            modelAndView.setViewName("redirect:/colegiados");
        }

        return modelAndView;
    }

    @PostMapping("/{id}")
    public ModelAndView atualizarColegiado(ModelAndView modelAndView, @PathVariable Integer id, @Valid CriarColegiadoDTO colegiado, BindingResult bindingResult, RedirectAttributes attr){

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("redirect:/colegiados/{id}/edit");
        } else {

            attr.addFlashAttribute("message", "OK: Colegiado editado com sucesso!");
            attr.addFlashAttribute("error", "false");
            colegiadoService.atualizarColegiado(id, colegiado);

            modelAndView.setViewName("redirect:/colegiados");
        }

        return modelAndView;
    }

    @PostMapping
    public ModelAndView criarColegiado(ModelAndView modelAndView, @Valid CriarColegiadoDTO colegiadoDTO, BindingResult bindingResult, RedirectAttributes attr) {

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("colegiados/new");
        } else {
            colegiadoService.criarColegiado(colegiadoDTO);
            attr.addFlashAttribute("message", "Colegiado cadastrado com sucesso!");
            attr.addFlashAttribute("error", "false");
            modelAndView.setViewName("redirect:/colegiados");
        }

        return modelAndView;
    }

    @PostMapping("/atribuir")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ModelAndView atribuirProfessorNoColegiado(@RequestParam Integer idProfessor,
                                         @RequestParam Integer idColegiado, ModelAndView modelAndView, BindingResult bindingResult, RedirectAttributes attr){

        //new ProcessoDTO(processoService.atribuirProcesso(idProcesso, idProfessor));
        //modelAndView.setViewName("redirect:/professores");

        ModelAndView mv = new ModelAndView("redirect:/professores");


        try {
            colegiadoService.adicionarProfessor(idColegiado, idProfessor);
            attr.addFlashAttribute("message", "OK: Colegiado atribuído com sucesso!");
            attr.addFlashAttribute("error", "false");
        } catch (RuntimeException e) {
            e.printStackTrace();
            attr.addFlashAttribute("message", "Error: " + e.getMessage());
            attr.addFlashAttribute("error", "true");
        }


        return mv;
    }

//    @PostMapping
//    @ResponseBody
//    @ResponseStatus(HttpStatus.CREATED)
//    public Colegiado criarColegiado(@RequestBody @Valid CriarColegiadoDTO colegiadoDTO){
//        return colegiadoService.criarColegiado(colegiadoDTO);
//    }

    @PatchMapping("/professor/{idColegiado}/{idProfessor}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ColegiadoDTO adicionaProfessor(@PathVariable Integer idColegiado,
                                          @PathVariable Integer idProfessor){
        return new ColegiadoDTO(colegiadoService.adicionarProfessor(idColegiado, idProfessor));
    }

    @DeleteMapping("/professor/{idColegiado}/{idProfessor}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ColegiadoDTO removeProfessor(@PathVariable Integer idColegiado,
                                     @PathVariable Integer idProfessor){
        return new ColegiadoDTO(colegiadoService.removerProfessor(idColegiado, idProfessor));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarColegiado(@PathVariable Integer id){
        colegiadoService.deletarColegiado(id);
    }

    @GetMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Colegiado buscaColegiado(@PathVariable Integer id){
        return colegiadoService.encontrarPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Colegiado atualizaColegiado(@PathVariable Integer id,
                                    @RequestBody @Valid CriarColegiadoDTO colegiadoDTO){
        return colegiadoService.atualizarColegiado(id, colegiadoDTO);
    }

    public boolean temcolegiado(Integer id){
        return colegiadoService.temcolegiado(id);
    }
}
