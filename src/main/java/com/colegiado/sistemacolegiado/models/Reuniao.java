package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.enums.StatusReuniao;
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
    @OneToMany(mappedBy = "reuniao")
    private List<Processo> processos;

    public Reuniao (Colegiado colegiado, List<Processo> processosPassados, StatusReuniao statuscriacao){
        this.colegiado = colegiado;
        this.processos = processosPassados;
        this.status = statuscriacao;
        this.dataReuniao = LocalDate.now();
    }

}
