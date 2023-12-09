package com.colegiado.sistemacolegiado.services;

import com.colegiado.sistemacolegiado.models.Colegiado;
import com.colegiado.sistemacolegiado.models.Processo;
import com.colegiado.sistemacolegiado.models.Reuniao;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import com.colegiado.sistemacolegiado.repositories.ColegiadoRepositorio;
import com.colegiado.sistemacolegiado.repositories.ProcessoRepositorio;
import com.colegiado.sistemacolegiado.repositories.ReuniaoRepositorio;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReuniaoService {
    final ReuniaoRepositorio reuniaoRepositorio;
    final ColegiadoRepositorio colegiadoRepositorio;
    final ProcessoRepositorio processoRepositorio;


    @Transactional
    public Reuniao criarReuniao(Reuniao reuniao, List<Processo> processos){
        for (Processo Tprocesso : processos){
            Tprocesso.setReuniao(reuniao);
        }

        for (Processo processo : processos) {
            this.processoRepositorio.save(processo);
        }

        return this.reuniaoRepositorio.save(reuniao);
    }

    public Optional<Reuniao> encontrarPorId(int id){
        return this.reuniaoRepositorio.findById(id);
    }

    public List<Reuniao> listarReunioes(){
        return this.reuniaoRepositorio.findAll();
    };

    public void deletarReuniao(Reuniao reuniao){
        this.reuniaoRepositorio.delete(reuniao);
    }

    public Enum obterStatus(int idReuniao){
        Optional<Reuniao> reuniao = this.reuniaoRepositorio.findById(idReuniao);
        if (reuniao.isPresent()) {
            Reuniao reuniaoValidada = reuniao.get();
            return reuniaoValidada.getStatus();
        }
        else{
            throw new RuntimeException("reunião não encontrada");
        }

    }
    public void adicionarColegiado(int idReuniao, int idColegiado) {
        Optional<Reuniao> reuniaoOptional = reuniaoRepositorio.findById(idReuniao);
        Optional<Colegiado> colegiadoOptional = colegiadoRepositorio.findById(idColegiado);

        if (reuniaoOptional.isPresent() && colegiadoOptional.isPresent()) {
            Reuniao reuniao = reuniaoOptional.get();
            Colegiado colegiado = colegiadoOptional.get();
            reuniao.setColegiado(colegiado);
            reuniaoRepositorio.save(reuniao);
        } else {
            throw new RuntimeException("Reunião ou Colegiado não encontrado");
        }
    }

    public void adicionarProcesso(int idReuniao, int idProcesso) {
        Optional<Reuniao> reuniaoOptional = reuniaoRepositorio.findById(idReuniao);
        Optional<Processo> processoOptional = processoRepositorio.findById(idProcesso);

        if (reuniaoOptional.isPresent() && processoOptional.isPresent()) {
            Reuniao reuniao = reuniaoOptional.get();
            Processo processo = processoOptional.get();

            reuniao.getProcessos().add(processo);

            reuniaoRepositorio.save(reuniao);
        } else {
            throw new RuntimeException("Reunião ou Processo não encontrado");
        }
    }

    public List<Reuniao> listarReuniaoPorStatusOuColegiado(List<StatusReuniao> status, int idColegiado){
        Optional<Colegiado> colegiadoOptional = colegiadoRepositorio.findById(idColegiado);
        if (colegiadoOptional.isPresent()){
            return reuniaoRepositorio.findAllByStatusInAndColegiado(status, colegiadoOptional.get());
        } else {
            throw new RuntimeException("Colegiado não encontrado");
        }
    }

    public void iniciarReuniao(Integer idReuniao){
        if(reuniaoRepositorio.findByStatus(StatusReuniao.INICIADA).isEmpty()){
            Optional<Reuniao> reuniaoOptional = reuniaoRepositorio.findById(idReuniao);

            if (reuniaoOptional.isPresent()) {
                Reuniao reuniao = reuniaoOptional.get();
                reuniao.setStatus(StatusReuniao.INICIADA);
                reuniaoRepositorio.save(reuniao);
            } else {
                throw new RuntimeException("Reunião não encontrada");
            }
        }else {
            throw new RuntimeException("Já existe Reunião em andamento");
        }
    }

    public void encerrarReuniao(Integer idReuniao){
        Optional<Reuniao> reuniaoOptional = reuniaoRepositorio.findById(idReuniao);

        if (reuniaoOptional.isPresent()) {
            Reuniao reuniao = reuniaoOptional.get();
            if(reuniao.getStatus().equals(StatusReuniao.INICIADA)){
                reuniao.setStatus(StatusReuniao.ENCERRADA);
                reuniaoRepositorio.save(reuniao);
            }
        } else {
            throw new RuntimeException("Reunião não encontrada");
        }
    }

    public List<Reuniao> reunioesdocolegiado (int idColegiado){
        return reuniaoRepositorio.listareuniaocolegiado(idColegiado);
    }

    public List<Reuniao> filtrarreuniao (StatusReuniao status, int idcolegiado){
        System.out.println(status);

       if (status == null) {
           return reunioesdocolegiado(idcolegiado);
       }

       return reuniaoRepositorio.filtrarStatus(status, idcolegiado);
    }

}
