package com.esg.cidades.testes.suporte;

import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Guarda o estado de UM cenário (a última resposta, os ids criados, etc.).
 * O PicoContainer cria uma instância nova por cenário e a injeta nas classes de passos,
 * então os cenários nunca compartilham dados entre si.
 */
public class ContextoCenario {

    /** Sufixo aleatório para que os nomes de cidades de um cenário não colidam com os de outro. */
    public final String sufixo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();

    /** Última resposta recebida da API. */
    public Response resposta;

    /** Id da última cidade criada com sucesso. */
    public Long idAtual;

    /** Nome (já com sufixo) da última cidade criada com sucesso. */
    public String nomeAtual;

    /** Valor de "atualizadoEm" devolvido no cadastro (usado na checagem de rastreabilidade). */
    public String atualizadoEmCadastro;

    /** Dados enviados no último cadastro ou atualização. */
    public Map<String, Object> corpoEnviado;

    /** Dados enviados no cadastro original (para provar que uma atualização inválida não altera nada). */
    public Map<String, Object> dadosOriginais;

    /** Todas as cidades criadas no cenário; são removidas ao final para manter o ambiente limpo. */
    public final List<Long> idsCriados = new ArrayList<>();

    /** Histórico legível das requisições e respostas, anexado ao relatório HTML. */
    public final StringBuilder registro = new StringBuilder();
}
