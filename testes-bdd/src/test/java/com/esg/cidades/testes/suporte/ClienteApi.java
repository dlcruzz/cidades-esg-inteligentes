package com.esg.cidades.testes.suporte;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cliente HTTP da API (REST Assured). Todas as chamadas passam por aqui para que
 * cada requisição e resposta fique registrada no contexto do cenário.
 */
public class ClienteApi {

    private static final ObjectMapper MAPEADOR = new ObjectMapper();

    private final ContextoCenario contexto;

    public ClienteApi(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    /** URL base: propriedade -Dapi.url, depois variável API_URL, depois http://localhost:8080. */
    public static String urlBase() {
        String url = System.getProperty("api.url");
        if (url == null || url.isBlank()) {
            url = System.getenv("API_URL");
        }
        if (url == null || url.isBlank()) {
            url = "http://localhost:8080";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /** Executa uma requisição e guarda a resposta no contexto. */
    public Response chamar(String metodo, String caminho, Object corpo, Map<String, Object> parametros) {
        RequestSpecification requisicao = RestAssured.given()
                .baseUri(urlBase())
                .accept(ContentType.JSON);

        if (parametros != null && !parametros.isEmpty()) {
            requisicao.queryParams(parametros);
        }
        if (corpo != null) {
            requisicao.contentType(ContentType.JSON).body(corpo);
        }

        Response resposta = requisicao.request(metodo, caminho);
        contexto.resposta = resposta;
        registrar(metodo, caminho, parametros, corpo, resposta);
        return resposta;
    }

    /** Cadastra uma cidade e, se a API responder 201, guarda id, nome e data de cadastro no contexto. */
    public Response criarCidade(Map<String, Object> dados) {
        Response resposta = chamar("POST", "/api/cidades", dados, null);
        contexto.corpoEnviado = dados;
        contexto.dadosOriginais = new LinkedHashMap<>(dados);
        if (resposta.getStatusCode() == 201) {
            Number id = resposta.jsonPath().get("id");
            contexto.idAtual = id.longValue();
            contexto.nomeAtual = resposta.jsonPath().getString("nome");
            contexto.atualizadoEmCadastro = resposta.jsonPath().getString("atualizadoEm");
            contexto.idsCriados.add(contexto.idAtual);
        }
        return resposta;
    }

    /** Remove uma cidade sem registrar no histórico (usado só na limpeza ao fim do cenário). */
    public static void removerSilenciosamente(long id) {
        try {
            RestAssured.given().baseUri(urlBase()).delete("/api/cidades/" + id);
        } catch (Exception ignorada) {
            // a limpeza nunca deve derrubar o teste
        }
    }

    private void registrar(String metodo, String caminho, Map<String, Object> parametros,
                           Object corpo, Response resposta) {
        StringBuilder sb = contexto.registro;
        sb.append(">>> ").append(metodo).append(' ').append(urlBase()).append(caminho);
        if (parametros != null && !parametros.isEmpty()) {
            sb.append("  parametros=").append(parametros);
        }
        sb.append('\n');
        if (corpo != null) {
            sb.append("Corpo enviado:\n").append(formatar(corpo)).append('\n');
        }
        sb.append("<<< HTTP ").append(resposta.getStatusCode())
                .append(" (").append(resposta.time()).append(" ms)\n");
        String texto = resposta.asString();
        if (texto != null && !texto.isBlank()) {
            sb.append(formatar(texto)).append('\n');
        }
        sb.append('\n');
    }

    private static String formatar(Object conteudo) {
        try {
            JsonNode no = conteudo instanceof String
                    ? MAPEADOR.readTree((String) conteudo)
                    : MAPEADOR.valueToTree(conteudo);
            return MAPEADOR.writerWithDefaultPrettyPrinter().writeValueAsString(no);
        } catch (Exception e) {
            return String.valueOf(conteudo);
        }
    }
}
