package com.esg.cidades.controller;

import com.esg.cidades.model.Cidade;
import com.esg.cidades.repository.CidadeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CidadeRepository cidadeRepository;

    @Test
    void deveCriarEConsultarCidade() throws Exception {
        Cidade cidade = new Cidade("São Paulo", "SP", 12325232L, 60.0, 65.0, 70.0);

        mockMvc.perform(post("/api/cidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cidade)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("São Paulo")))
                .andExpect(jsonPath("$.uf", is("SP")));

        mockMvc.perform(get("/api/cidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())));
    }

    @Test
    void deveRetornar404ParaCidadeInexistente() throws Exception {
        mockMvc.perform(get("/api/cidades/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", is("Recurso não encontrado")));
    }

    @Test
    void deveRetornar400ParaDadosInvalidos() throws Exception {
        Cidade cidadeInvalida = new Cidade("", "SP", -10L, 200.0, 65.0, 70.0);

        mockMvc.perform(post("/api/cidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cidadeInvalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarStatusDaAplicacaoNaRaiz() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacao", is("Cidades ESG Inteligentes")))
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
