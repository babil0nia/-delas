package com.delas.api.controller;

import com.delas.api.model.AvaliacaoModel;
import com.delas.api.model.ContratacaoModel;
import com.delas.api.repository.AvaliacaoRepository;
import com.delas.api.repository.ContratacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AvaliacaoControllerTest {

    @Autowired
    private AvaliacaoController avaliacaoController;

    @Autowired
    private ContratacaoController contratacaoController;

    private ContratacaoModel contratacaoModel;
    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Autowired
    ContratacaoRepository contratacaoRepository;
    @BeforeEach
    void setUp() {
        // Criar uma ContratacaoModel para associar à AvaliacaoModel
        ContratacaoModel contratacao = new ContratacaoModel();
        contratacao.setStatus("Concluído");
        contratacao.setComentarios("Serviço excelente");
        contratacao.setDataContratacao(LocalDateTime.now());
        contratacao = contratacaoRepository.save(contratacao);
    }

    @Test
    void testSaveAvaliacao() {
        AvaliacaoModel avaliacao = new AvaliacaoModel();
        avaliacao.setIdcontratacao(contratacaoModel);
        avaliacao.setNota(5);

        AvaliacaoModel savedAvaliacao = avaliacaoRepository.save(avaliacao);

        assertNotNull(savedAvaliacao);
        assertEquals(5, savedAvaliacao.getNota());
        assertEquals(contratacaoModel.getIdcontratacao(), savedAvaliacao.getIdcontratacao().getIdcontratacao());
    }

    @Test
    void testFindAvaliacaoById() {
        AvaliacaoModel avaliacao = new AvaliacaoModel();
        avaliacao.setIdcontratacao(contratacaoModel);
        avaliacao.setNota(4);
        AvaliacaoModel savedAvaliacao = avaliacaoRepository.save(avaliacao);

        Optional<AvaliacaoModel> foundAvaliacao = avaliacaoRepository.findById(savedAvaliacao.getIdavaliacao());

        assertTrue(foundAvaliacao.isPresent());
        assertEquals(4, foundAvaliacao.get().getNota());
    }
}

