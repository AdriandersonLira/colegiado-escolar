package com.colegiado.sistemacolegiado.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column (nullable = false)
    private String nome;
    @Column (nullable = false, unique = true)
    private String fone;
    @Column (nullable = false, unique = true)
    private String matricula;
    @Column (nullable = false, unique = true)
    private String login;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    public String toString() {

        return "id: " + String.valueOf(id) + "\n" +
                "Nome: " + nome + "\n" +
                "fone: " + fone + "\n" +
                "matricula: " + matricula + "\n" +
                "login: " + login + "\n";
    }
}


