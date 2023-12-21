package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.Voto.Voto;
import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
import com.colegiado.sistemacolegiado.models.enums.TipoDecisao;
import com.colegiado.sistemacolegiado.models.enums.TipoVoto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reuniao {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dataReuniao;
    @Column (nullable = false)
    private StatusReuniao status;
    @Column ()
    private byte[] ata;
    @ManyToOne
    private Colegiado colegiado;
    @OneToMany(mappedBy = "reuniao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Processo> processos;

    @OneToMany(mappedBy = "reuniao", cascade = CascadeType.ALL)
    private List<Voto> votos;

    public Reuniao (Colegiado colegiado, List<Processo> processosPassados, StatusReuniao statuscriacao, LocalDate dataInicioReuniao ){
        this.colegiado = colegiado;
        this.processos = processosPassados;
        this.status = statuscriacao;
        this.dataReuniao = dataInicioReuniao;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id).append("\n");
        sb.append("Data da Reunião: ").append(dataReuniao).append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("Colegiado: ").append(colegiado.getCurso()).append("\n");

        // Adiciona os detalhes de cada processo
        if(processos != null){
            sb.append("Processos:\n");
            for (Processo processo : processos) {
                sb.append("  - ID: ").append(processo.getId()).append("\n");
                sb.append("    Requerimento: ").append(processo.getRequerimento()).append("\n");
                // Adicione mais informações do processo conforme necessário
            }

        }


        return sb.toString();
    }

    public void adicionarVoto(Voto voto) {
        votos.add(voto);
    }

    public int contarVotosComRelator() {
        return (int) votos.stream().filter(v -> v.getVoto() == TipoVoto.COM_RELATOR).count();
    }

    public int contarVotosDivergentes() {
        return (int) votos.stream().filter(v -> v.getVoto() == TipoVoto.DIVERGENTE).count();
    }

    public TipoDecisao calcularResultadoVotacao() {
        int votosComRelator = contarVotosComRelator();
        int votosDivergentes = contarVotosDivergentes();

        // Lógica para determinar o resultado da votação
        if (votosComRelator > votosDivergentes) {
            return TipoDecisao.DEFERIDO;
        } else {
            return TipoDecisao.INDEFERIDO;
        }
    }

    public void setProcesso (Processo processo){
        processos.add(processo);
    }

}
