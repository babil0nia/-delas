package com.delas.api.model;

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
@Table(name = "avaliacao")
public class AvaliacaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idavaliacao;

    // Relacionamento com a tabela Contratacao
    @OneToOne
    @JoinColumn(name = "idcontratacao", nullable = false)
    private ContratacaoModel idcontratacao;

    // Outros campos da avaliação
    @Column(name = "nota")
    private int nota;


}
