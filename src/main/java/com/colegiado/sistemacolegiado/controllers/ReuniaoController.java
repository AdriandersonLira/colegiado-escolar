package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.*;
import com.colegiado.sistemacolegiado.models.Voto.Voto;
import com.colegiado.sistemacolegiado.models.Voto.VotoId;
import com.colegiado.sistemacolegiado.models.dto.VotoDTO;
import com.colegiado.sistemacolegiado.models.enums.StatusProcesso;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import com.colegiado.sistemacolegiado.services.ColegiadoService;
import com.colegiado.sistemacolegiado.services.ProcessoService;
import com.colegiado.sistemacolegiado.services.ProfessorService;
import com.colegiado.sistemacolegiado.services.ReuniaoService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.LocalDate;
import java.util.*;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/reunioes")
public class ReuniaoController {
    @Autowired
    ReuniaoService reuniaoService;
    @Autowired
    ProcessoService processoService;
    @Autowired
    ColegiadoService colegiadoService;
    @Autowired
    ProfessorService professorService;

    @PostMapping("/criar")
    public ModelAndView criarReuniao(
            @RequestParam Integer idColegiadoReuniao,
            @RequestParam Integer[] idProcessoReuniao,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataReuniao,
            RedirectAttributes attr
    ) {
        Colegiado colegiado = colegiadoService.encontrarPorId(idColegiadoReuniao);
        List<Processo> processos = new ArrayList<>();

        for (Integer processoVerificar : idProcessoReuniao) {
            Optional<Processo> processoOptional = processoService.encontrarPorId(processoVerificar);
            Processo processo = processoOptional.orElseThrow(() -> new NoSuchElementException("Processo não encontrado"));

            if (processo.getProfessor() == null) {
                attr.addFlashAttribute("message", "Error: Processo não tem professor");
                attr.addFlashAttribute("error", "true");
                return new ModelAndView("redirect:/processos");
            }

            if (processo.getProfessor().getColegiado() == null) {
                attr.addFlashAttribute("message", "Error: Professor não faz parte do colegiado!");
                attr.addFlashAttribute("error", "true");
                return new ModelAndView("redirect:/processos");
            }

            if (!processo.getProfessor().getColegiado().getCurso().equals(colegiado.getCurso())) {
                attr.addFlashAttribute("message", "Error: Professor não faz parte do colegiado!");
                attr.addFlashAttribute("error", "true");
                return new ModelAndView("redirect:/processos");
            }
            System.out.println(processo.toString());

            processos.add(processo);
        }

        try {
            Reuniao reuniao = new Reuniao(colegiado, processos, StatusReuniao.PROGRAMADA, dataReuniao);
            reuniaoService.criarReuniao(reuniao, processos);
            attr.addFlashAttribute("message", "Reunião criada com sucesso!");
            attr.addFlashAttribute("error", false); // Defina como falso para indicar sucesso
        } catch (RuntimeException e) {
            e.printStackTrace();
            attr.addFlashAttribute("message", "Error: " + e.getMessage());
            attr.addFlashAttribute("error", true); // Mantenha como verdadeiro para indicar erro
        }
        return new ModelAndView("redirect:/reunioes");
    }

    @GetMapping
    public ModelAndView listaReunioes(ModelAndView modelAndView) {
        List<Reuniao> reunioes = reuniaoService.listarReunioes();
        for (Reuniao reuniao : reunioes) {
            System.out.println(reuniao.toString());
            System.out.println(); // Adiciona uma linha em branco entre as reuniões
        }

        modelAndView.addObject("reunioes", reunioes);
        modelAndView.setViewName("reunioes/listarreunioes");
        return modelAndView;
    }

    @GetMapping ("/{id}/listreuniaocolegiado")
    public ModelAndView listaReunioesDoColegiado(@PathVariable int id, ModelAndView modelAndView){

        Professor professor = professorService.encontrarPorId(id);

        List<Reuniao> reunioes = reuniaoService.reunioesdocolegiado(professor.getColegiado().getId());


        modelAndView.addObject("reunioescolegiado", reunioes);
        modelAndView.addObject("statusReuniao", StatusReuniao.values());
        modelAndView.addObject("professor", professor);
        modelAndView.setViewName("reunioes/listarreunioescolegiado");

        return modelAndView;

    }

    @GetMapping("/filtrar/{id}")
    public ModelAndView filtrar (@PathVariable Integer id, @RequestParam (name = "statusFilter", required = false) StatusReuniao status) {

        ModelAndView mv = new ModelAndView("reunioes/listarreunioescolegiado");
        System.out.println(status);
        Professor professor = professorService.encontrarPorId(id);
        int idcolegiado = professor.getColegiado().getId();
        System.out.println("oiiiiii");
        List<Reuniao> reunioesfiltradas = reuniaoService.filtrarreuniao(status, idcolegiado);

        mv.addObject("reunioescolegiado", reunioesfiltradas);
        mv.addObject("statusReuniao", StatusReuniao.values());
        mv.addObject("professor", professor);
        return mv;

    }

    @GetMapping("/iniciarReuniao")
    public ModelAndView iniciarReuniao (ModelAndView modelAndView){
        List<Reuniao> reunioes = reuniaoService.listarReunioes();
        for (Reuniao reuniao : reunioes) {
            System.out.println(reuniao.toString());
            System.out.println(); // Adiciona uma linha em branco entre as reuniões
        }

        modelAndView.addObject("reunioes", reunioes);
        modelAndView.setViewName("reunioes/index");
        return modelAndView;
    }

    @GetMapping("/iniciar-reuniao/{id}")
    public ModelAndView iniciarReuniao(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView("reunioes/iniciodareuniao");

        List<Reuniao> reunioes = reuniaoService.listarReunioes();
        boolean reuniaoIniciada = false;

        for (Reuniao reuniao : reunioes) {
            if (reuniao.getStatus().equals(StatusReuniao.INICIADA)) {
                reuniaoIniciada = true;

                if (!Objects.equals(reuniao.getId(), id)) {
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("message", "A reunião já foi iniciada. Não é possível iniciar outra reunião.");
                    mv.addObject("reunioes", reunioes);
                    return new ModelAndView("redirect:/reunioes");
                }
            }
        }

        Optional<Reuniao> reuniaoOptional = reuniaoService.encontrarPorId(id);

        if (reuniaoOptional.isPresent()) {
            Reuniao reuniao = reuniaoOptional.get();
            reuniaoService.iniciarReuniao(id);
            List<Processo> processos = reuniao.getProcessos();

            if (processos.size() > 1) {
                mv.setViewName("/reunioes/escolherprocessoparavotar");
                mv.addObject("processos", processos);
                return mv;
            } else {
                Processo processo = processos.get(0);
                List<Professor> professoresdocolegiado = reuniao.getColegiado().getProfessores();
                mv.addObject("processo", processo);
                mv.addObject("professores", professoresdocolegiado);
            }
        }

        return mv;
    }

    /*@PostMapping("/reuniao/iniciar")
    public ModelAndView iniciarReuniao(Integer idReuniao){
        reuniaoService.iniciarReuniao(idReuniao);
        return null;
    }*/

    @PostMapping("/reuniao/encerrar")
    public ModelAndView encerrarReuniao(@RequestParam("idReuniao")Integer idReuniao){
        reuniaoService.encerrarReuniao(idReuniao);
        return new ModelAndView("redirect:/reunioes");
    }

    @PostMapping("/contarvotos")
    public String processarVotos(@ModelAttribute("votos") List<VotoDTO> votos) {
        // Lógica para processar os votos
        // ...
        return "redirect:/pagina-de-sucesso";
    }
}