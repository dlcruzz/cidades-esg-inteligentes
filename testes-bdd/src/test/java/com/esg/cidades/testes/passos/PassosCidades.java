package com.esg.cidades.testes.passos;

import com.esg.cidades.testes.suporte.ClienteApi;
import com.esg.cidades.testes.suporte.ContextoCenario;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Passos específicos do recurso /api/cidades (cadastro, consulta, atualização e remoção).
 */
public class PassosCidades {

    private static final String ROTA = "/api/cidades";
    private static final double TOLERANCIA = 0.001;

    private final ContextoCenario contexto;
    private final ClienteApi api;

    public PassosCidades(ContextoCenario contexto, ClienteApi api) {
        this.contexto = contexto;
        this.api = api;
    }

    // ------------------------------------------------------------------
    // Preparação dos dados (Dado)
    // ------------------------------------------------------------------

    @Dado("que existem as cidades cadastradas:")
    public void cadastrarVariasCidades(DataTable tabela) {
        for (Map<String, String> linha : tabela.asMaps()) {
            Response resposta = api.criarCidade(montarCorpo(linha, true));
            assertEquals(201, resposta.getStatusCode(),
                    "Falha ao preparar o cenário. Corpo: " + resposta.asString());
        }
    }

    @Dado("que existe uma cidade cadastrada chamada {string} na UF {string} com indicadores {int}, {int} e {int}")
    public void cadastrarUmaCidade(String nome, String uf, int ambiental, int social, int governanca) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", nome + " " + contexto.sufixo);
        corpo.put("uf", uf);
        corpo.put("populacao", 100000L);
        corpo.put("indicadorAmbiental", (double) ambiental);
        corpo.put("indicadorSocial", (double) social);
        corpo.put("indicadorGovernanca", (double) governanca);
        Response resposta = api.criarCidade(corpo);
        assertEquals(201, resposta.getStatusCode(),
                "Falha ao preparar o cenário. Corpo: " + resposta.asString());
    }

    // ------------------------------------------------------------------
    // Ações (Quando)
    // ------------------------------------------------------------------

    @Quando("eu cadastro uma cidade com os dados:")
    public void cadastrarCidadeComDados(DataTable tabela) {
        Map<String, String> linha = tabela.asMaps().get(0);
        api.criarCidade(montarCorpo(linha, true));
    }

    @Quando("eu cadastro uma cidade válida com o campo {string} igual a {string}")
    public void cadastrarCidadeComUmCampo(String campo, String valor) {
        Map<String, Object> corpo = corpoValidoPadrao();
        corpo.put(campo, converter(campo, valor));
        api.criarCidade(corpo);
    }

    @Quando("eu envio POST para {string} com o corpo bruto:")
    public void enviarPostComCorpoBruto(String caminho, String corpoBruto) {
        api.chamar("POST", caminho, corpoBruto, null);
    }

    @Quando("eu consulto a cidade cadastrada pelo seu id")
    public void consultarCidadeCadastrada() {
        api.chamar("GET", ROTA + "/" + contexto.idAtual, null, null);
    }

    @Quando("eu consulto a cidade pelo id {string}")
    public void consultarCidadePeloId(String id) {
        api.chamar("GET", ROTA + "/" + id, null, null);
    }

    @Quando("eu listo todas as cidades")
    public void listarTodas() {
        api.chamar("GET", ROTA, null, null);
    }

    @Quando("eu listo as cidades filtrando por UF {string}")
    public void listarPorUf(String uf) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("uf", uf);
        api.chamar("GET", ROTA, null, parametros);
    }

    @Quando("eu listo as cidades filtrando pelo nome {string}")
    public void listarPorNome(String nome) {
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("nome", nome);
        api.chamar("GET", ROTA, null, parametros);
    }

    @Quando("eu atualizo a cidade cadastrada com os dados:")
    public void atualizarCidadeComDados(DataTable tabela) throws InterruptedException {
        Map<String, String> linha = tabela.asMaps().get(0);
        Map<String, Object> corpo = montarCorpo(linha, true);
        Thread.sleep(20); // garante que a data de atualização seja de fato posterior à do cadastro
        api.chamar("PUT", ROTA + "/" + contexto.idAtual, corpo, null);
        contexto.corpoEnviado = corpo;
    }

    @Quando("eu atualizo a cidade cadastrada com o campo {string} igual a {string}")
    public void atualizarCidadeComUmCampo(String campo, String valor) {
        Map<String, Object> corpo = new LinkedHashMap<>(contexto.dadosOriginais);
        corpo.put(campo, converter(campo, valor));
        api.chamar("PUT", ROTA + "/" + contexto.idAtual, corpo, null);
        contexto.corpoEnviado = corpo;
    }

    @Quando("eu atualizo a cidade de id {string} com dados válidos")
    public void atualizarCidadeInexistente(String id) {
        api.chamar("PUT", ROTA + "/" + id, corpoValidoPadrao(), null);
    }

    @Quando("eu removo a cidade cadastrada")
    public void removerCidadeCadastrada() {
        api.chamar("DELETE", ROTA + "/" + contexto.idAtual, null, null);
    }

    @Quando("eu removo a cidade de id {string}")
    public void removerCidadePeloId(String id) {
        api.chamar("DELETE", ROTA + "/" + id, null, null);
    }

    // ------------------------------------------------------------------
    // Verificações (Então)
    // ------------------------------------------------------------------

    @Então("o id da cidade deve ser gerado pela API")
    public void validarIdGerado() {
        Number id = contexto.resposta.jsonPath().get("id");
        assertNotNull(id, "A API não devolveu o id");
        assertTrue(id.longValue() > 0, "O id deveria ser positivo, mas foi " + id);
    }

    @Então("o id da cidade deve continuar o mesmo")
    public void validarIdMantido() {
        Number id = contexto.resposta.jsonPath().get("id");
        assertEquals(contexto.idAtual.longValue(), id.longValue());
    }

    @Então("a cidade retornada deve corresponder aos dados enviados")
    public void validarDadosEnviados() {
        JsonPath json = contexto.resposta.jsonPath();
        Map<String, Object> enviado = contexto.corpoEnviado;
        assertEquals(enviado.get("nome"), json.getString("nome"));
        assertEquals(enviado.get("uf"), json.getString("uf"));
        assertEquals(((Number) enviado.get("populacao")).longValue(), json.getLong("populacao"));
        for (String pilar : List.of("indicadorAmbiental", "indicadorSocial", "indicadorGovernanca")) {
            assertEquals(((Number) enviado.get(pilar)).doubleValue(), json.getDouble(pilar), TOLERANCIA,
                    "Valor inesperado em " + pilar);
        }
    }

    @Então("o score ESG da cidade deve ser {string}")
    public void validarScoreEsg(String esperado) {
        double score = contexto.resposta.jsonPath().getDouble("scoreEsg");
        assertEquals(Double.parseDouble(esperado), score, TOLERANCIA, "Score ESG incorreto");
    }

    @Então("a lista deve conter a cidade cadastrada")
    public void validarListaContemCidadeCadastrada() {
        assertTrue(idsDaLista().contains(contexto.idAtual),
                "A lista não contém a cidade de id " + contexto.idAtual);
    }

    @Então("a lista deve conter todas as cidades cadastradas no cenário")
    public void validarListaContemTodas() {
        List<Long> ids = idsDaLista();
        for (Long id : contexto.idsCriados) {
            assertTrue(ids.contains(id), "A lista não contém a cidade de id " + id);
        }
    }

    @Então("a lista deve conter exatamente {int} cidades")
    public void validarTamanhoDaLista(int quantidade) {
        assertEquals(quantidade, idsDaLista().size(), "Quantidade de cidades inesperada");
    }

    @Então("a lista deve estar vazia")
    public void validarListaVazia() {
        assertTrue(idsDaLista().isEmpty(), "A lista deveria estar vazia");
    }

    @Então("todas as cidades da lista devem ter a UF {string}")
    public void validarUfDaLista(String uf) {
        List<String> ufs = contexto.resposta.jsonPath().getList("uf");
        assertFalse(ufs.isEmpty(), "A lista está vazia");
        for (String valor : ufs) {
            assertTrue(uf.equalsIgnoreCase(valor), "Encontrada UF diferente de " + uf + ": " + valor);
        }
    }

    @Então("todas as cidades da lista devem ter o nome contendo {string}")
    public void validarNomeDaLista(String trecho) {
        List<String> nomes = contexto.resposta.jsonPath().getList("nome");
        assertFalse(nomes.isEmpty(), "A lista está vazia");
        for (String nome : nomes) {
            assertTrue(nome.toLowerCase().contains(trecho.toLowerCase()),
                    "Nome sem '" + trecho + "': " + nome);
        }
    }

    @Então("o erro de validação deve informar {string} para o campo {string}")
    public void validarMensagemDeValidacao(String mensagem, String campo) {
        String atual = contexto.resposta.jsonPath().getString("campos." + campo);
        assertEquals(mensagem, atual, "Mensagem de validação incorreta para o campo " + campo);
    }

    @Então("a mensagem de erro deve citar o id {string}")
    public void validarMensagemDeNaoEncontrado(String id) {
        String mensagem = contexto.resposta.jsonPath().getString("mensagem");
        assertEquals("Cidade não encontrada com id: " + id, mensagem);
    }

    @Então("a cidade cadastrada deve continuar com os dados originais")
    public void validarDadosOriginais() {
        Response atual = api.chamar("GET", ROTA + "/" + contexto.idAtual, null, null);
        assertEquals(200, atual.getStatusCode());
        JsonPath json = atual.jsonPath();
        Map<String, Object> original = contexto.dadosOriginais;
        assertEquals(original.get("nome"), json.getString("nome"));
        assertEquals(original.get("uf"), json.getString("uf"));
        for (String pilar : List.of("indicadorAmbiental", "indicadorSocial", "indicadorGovernanca")) {
            assertEquals(((Number) original.get(pilar)).doubleValue(), json.getDouble(pilar), TOLERANCIA);
        }
    }

    @Então("a data de atualização deve ser posterior à data de cadastro")
    public void validarRastreabilidade() {
        LocalDateTime cadastro = LocalDateTime.parse(contexto.atualizadoEmCadastro);
        LocalDateTime atualizacao = LocalDateTime.parse(contexto.resposta.jsonPath().getString("atualizadoEm"));
        assertTrue(atualizacao.isAfter(cadastro),
                "atualizadoEm (" + atualizacao + ") deveria ser posterior a " + cadastro);
    }

    @Então("a cidade removida não deve mais ser encontrada")
    public void validarCidadeRemovida() {
        Response resposta = api.chamar("GET", ROTA + "/" + contexto.idAtual, null, null);
        assertEquals(404, resposta.getStatusCode(), "A cidade ainda existe após a remoção");
    }

    // ------------------------------------------------------------------
    // Funções auxiliares
    // ------------------------------------------------------------------

    private List<Long> idsDaLista() {
        List<Number> ids = contexto.resposta.jsonPath().getList("id");
        List<Long> resultado = new ArrayList<>();
        for (Number id : ids) {
            resultado.add(id.longValue());
        }
        return resultado;
    }

    /** Corpo válido usado quando o cenário só quer alterar um campo. */
    private Map<String, Object> corpoValidoPadrao() {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("nome", "Cidade Padrao " + contexto.sufixo);
        corpo.put("uf", "SP");
        corpo.put("populacao", 100000L);
        corpo.put("indicadorAmbiental", 70.0);
        corpo.put("indicadorSocial", 70.0);
        corpo.put("indicadorGovernanca", 70.0);
        return corpo;
    }

    /** Converte uma linha da tabela do Gherkin em corpo JSON, acrescentando o sufixo único ao nome. */
    private Map<String, Object> montarCorpo(Map<String, String> linha, boolean nomeUnico) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        for (Map.Entry<String, String> campo : linha.entrySet()) {
            Object valor = converter(campo.getKey(), campo.getValue());
            if (nomeUnico && campo.getKey().equals("nome") && valor != null && !((String) valor).isBlank()) {
                valor = valor + " " + contexto.sufixo;
            }
            corpo.put(campo.getKey(), valor);
        }
        return corpo;
    }

    /** Converte o texto da tabela no tipo certo. O texto NULO vira null. */
    private Object converter(String campo, String valor) {
        if ("NULO".equals(valor)) {
            return null;
        }
        switch (campo) {
            case "populacao":
                return Long.valueOf(valor);
            case "indicadorAmbiental":
            case "indicadorSocial":
            case "indicadorGovernanca":
                return Double.valueOf(valor);
            default:
                return valor;
        }
    }
}
