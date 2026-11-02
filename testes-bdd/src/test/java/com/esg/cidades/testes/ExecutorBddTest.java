package com.esg.cidades.testes;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Ponto de entrada da suíte BDD.
 * Executa todos os arquivos .feature da pasta src/test/resources/features.
 * Para filtrar por tag: mvn test -Dcucumber.filter.tags="@negativo"
 */
@Suite
@SuiteDisplayName("Testes BDD da API Cidades ESG Inteligentes")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class ExecutorBddTest {
}
