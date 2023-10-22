package com.colegiado.sistemacolegiado.controllers;

import com.colegiado.sistemacolegiado.models.dto.CriarProcessoDTO;
import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.dto.FiltrarProcessoDTO;
import com.colegiado.sistemacolegiado.services.ProcessoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/processo")
@AllArgsConstructor
public class ProcessoController {
    final ProcessoService processoService;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Processo criarProcesso(@RequestBody @Valid CriarProcessoDTO processo){
       return processoService.criarProcesso(processo);
    }

    @GetMapping("/filtro")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Processo> listarProcessos(FiltrarProcessoDTO filtro){
        return processoService.listarProcessos(filtro);
    }

    @GetMapping("/filtro/coordendador")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<Processo> listarProcessosCoordenador(FiltrarProcessoDTO filtro){
        return processoService.listarProcessosCoordenador(filtro);
    }

    @PostMapping("atribuir/{idProcesso}/{idProfessor}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Processo atribuirProcesso(@PathVariable Integer idProcesso,
                                     @PathVariable Integer idProfessor){
        return processoService.atribuirProcesso(idProcesso, idProfessor);
    }
}
