package com.esg.cidades.testes.passos;

import com.esg.cidades.testes.suporte.ClienteApi;
import com.esg.cidades.testes.suporte.ContextoCenario;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.module.jsv.JsonSchemaValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Passos genéricos: valem para qualquer rota da API (status code, corpo JSON, contrato, campos).
 */
public class PassosGerais {

    private final ContextoCenario contexto;
    private final ClienteApi api;

    public PassosGerais(ContextoCenario contexto, ClienteApi api) {
        this.contexto = contexto;
        this.api = api;
    }

    @Quando("eu envio uma requisição GET para {string}")
    public void enviarGet(String caminho) {
        api.chamar("GET", caminho, null, null);
    }

    @Então("o status code da resposta deve ser {int}")
    public void validarStatusCode(int esperado) {
        assertEquals(esperado, contexto.resposta.getStatusCode(),
                "Status code inesperado. Corpo recebido: " + contexto.resposta.asString());
    }

    @Então("o corpo da resposta deve ser um JSON válido")
    public void validarJsonValido() {
        String tipo = contexto.resposta.getContentType();
        assertTrue(tipo != null && tipo.contains("json"),
                "Content-Type deveria ser JSON, mas foi: " + tipo);
        try {
            new ObjectMapper().readTree(contexto.resposta.asString());
        } catch (Exception e) {
            fail("O corpo não é um JSON válido: " + contexto.resposta.asString());
        }
    }

    @Então("o corpo deve respeitar o contrato {string}")
    public void validarContrato(String arquivoSchema) {
        contexto.resposta.then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/" + arquivoSchema));
    }

    @Então("o campo {string} deve ser igual a {string}")
    public void validarCampoIgual(String campo, String esperado) {
        Object valor = contexto.resposta.jsonPath().get(campo);
        assertEquals(esperado, String.valueOf(valor), "Valor inesperado no campo '" + campo + "'");
    }

    @Então("o campo {string} não deve estar vazio")
    public void validarCampoPreenchido(String campo) {
        Object valor = contexto.resposta.jsonPath().get(campo);
        assertNotNull(valor, "O campo '" + campo + "' veio nulo ou ausente");
        assertFalse(String.valueOf(valor).isBlank(), "O campo '" + campo + "' veio vazio");
    }

    @Então("o campo {string} deve ser um ambiente conhecido")
    public void validarAmbienteConhecido(String campo) {
        String valor = contexto.resposta.jsonPath().getString(campo);
        assertNotNull(valor, "O campo '" + campo + "' veio nulo ou ausente");
        assertTrue(valor.matches("^(default|test|staging|production)(,[a-z]+)*$"),
                "Ambiente desconhecido: " + valor);
    }

    @Então("o corpo da resposta deve estar vazio")
    public void validarCorpoVazio() {
        assertTrue(contexto.resposta.asString().isEmpty(),
                "O corpo deveria estar vazio, mas veio: " + contexto.resposta.asString());
    }

    @Então("o corpo da resposta não deve conter {string}")
    public void validarCorpoNaoContem(String trecho) {
        assertFalse(contexto.resposta.asString().contains(trecho),
                "O corpo não deveria conter '" + trecho + "'");
    }

    @Então("o tempo de resposta deve ser menor que {int} milissegundos")
    public void validarTempoDeResposta(int limite) {
        long tempo = contexto.resposta.time();
        assertTrue(tempo < limite,
                "Resposta lenta: " + tempo + " ms (limite " + limite + " ms)");
    }
}
