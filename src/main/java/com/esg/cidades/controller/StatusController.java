package com.esg.cidades.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class StatusController {

    private final Environment environment;

    public StatusController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/")
    public Map<String, Object> raiz() {
        // Mostra em qual ambiente (staging / production) a API está rodando.
        // Útil para os prints de evidência de cada ambiente.
        String[] perfis = environment.getActiveProfiles();
        String ambiente = perfis.length > 0 ? String.join(",", perfis) : "default";

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("aplicacao", "Cidades ESG Inteligentes");
        resposta.put("versao", "1.0.0");
        resposta.put("ambiente", ambiente);
        resposta.put("status", "UP");
        resposta.put("endpoints", "/api/cidades");
        return resposta;
    }
}
