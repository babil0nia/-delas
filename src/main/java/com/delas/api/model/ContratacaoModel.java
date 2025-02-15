package com.delas.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contratacao")
public class ContratacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcontratacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel id;

    @ManyToOne
    @JoinColumn(name = "idservicos", nullable = false)
    private ServicosModel idservicos;


    @Column(name = "status", length = 20, nullable = false)
    private String status;


    @Column(name = "datacriacao", nullable = false)
    private LocalDateTime dataContratacao = LocalDateTime.now();


    @Column(name = "comentarios")
    private String comentarios;
}
