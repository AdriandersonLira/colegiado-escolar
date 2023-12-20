package com.colegiado.sistemacolegiado.models;

import com.colegiado.sistemacolegiado.models.dto.CriarAssuntoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assunto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    String assunto;

    private String descricao;  // Novo campo de descrição

    public Assunto(CriarAssuntoDTO assuntoDTO) {
        this.assunto = assuntoDTO.getAssunto();
        this.descricao = assuntoDTO.getDescricao();  // Inicializar o novo campo de descrição
    }

    public void setAssunto(String Assunto){
        this.assunto =  Assunto;
    }

    public String toString(){
        return "id: " + id + "\n" +
                "assunto: " + assunto + "\n";
    }
}
