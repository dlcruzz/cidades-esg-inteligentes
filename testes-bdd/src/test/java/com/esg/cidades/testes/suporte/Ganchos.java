package com.esg.cidades.testes.suporte;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;

/**
 * Ganchos executados em volta de cada cenário.
 */
public class Ganchos {

    private final ContextoCenario contexto;

    public Ganchos(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @After
    public void aoFinalizar(Scenario cenario) {
        // 1) Anexa ao relatório HTML tudo o que foi enviado e recebido (evidência de execução)
        if (contexto.registro.length() > 0) {
            cenario.attach(contexto.registro.toString(), "text/plain", "Requisições e respostas da API");
        }
        // 2) Limpeza: remove as cidades criadas pelo cenário, deixando o ambiente como estava
        for (Long id : contexto.idsCriados) {
            ClienteApi.removerSilenciosamente(id);
        }
    }
}
