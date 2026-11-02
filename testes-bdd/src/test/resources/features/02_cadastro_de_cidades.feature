# language: pt
@cadastro
Funcionalidade: Cadastro de cidades com indicadores ESG
  Como gestor público
  Quero cadastrar cidades com seus indicadores ambiental, social e de governança
  Para acompanhar o desempenho ESG de cada município

  @positivo @contrato @esg
  Cenário: Cadastrar uma cidade com dados válidos
    Quando eu cadastro uma cidade com os dados:
      | nome     | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Curitiba | PR | 1963726   | 85                 | 78              | 90                  |
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "cidade.schema.json"
    E o id da cidade deve ser gerado pela API
    E a cidade retornada deve corresponder aos dados enviados
    E o score ESG da cidade deve ser "84.33"
    E o campo "atualizadoEm" não deve estar vazio
    E o tempo de resposta deve ser menor que 2000 milissegundos

  @positivo @esg
  Esquema do Cenário: Calcular o score ESG como média dos três pilares
    Quando eu cadastro uma cidade com os dados:
      | nome         | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Cidade Teste | SP | 50000     | <ambiental>        | <social>        | <governanca>        |
    Então o status code da resposta deve ser 201
    E o corpo deve respeitar o contrato "cidade.schema.json"
    E o score ESG da cidade deve ser "<score>"

    Exemplos:
      | ambiental | social | governanca | score |
      | 85        | 78     | 90         | 84.33 |
      | 100       | 100    | 100        | 100   |
      | 0         | 0      | 0          | 0     |
      | 50        | 60     | 70         | 60    |
      | 30        | 45     | 90         | 55    |

  @negativo @contrato
  Esquema do Cenário: Recusar indicador ESG fora da faixa de 0 a 100
    Quando eu cadastro uma cidade válida com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "erro-validacao.schema.json"
    E o campo "erro" deve ser igual a "Erro de validação"
    E o erro de validação deve informar "<mensagem>" para o campo "<campo>"

    Exemplos:
      | campo               | valor | mensagem                                          |
      | indicadorAmbiental  | 100.1 | O indicador ambiental deve ser entre 0 e 100      |
      | indicadorAmbiental  | -0.1  | O indicador ambiental deve ser entre 0 e 100      |
      | indicadorSocial     | 150   | O indicador social deve ser entre 0 e 100         |
      | indicadorSocial     | -5    | O indicador social deve ser entre 0 e 100         |
      | indicadorGovernanca | 101   | O indicador de governança deve ser entre 0 e 100  |
      | indicadorGovernanca | -1    | O indicador de governança deve ser entre 0 e 100  |

  @negativo @contrato
  Esquema do Cenário: Recusar cadastro sem os campos obrigatórios
    Quando eu cadastro uma cidade válida com o campo "<campo>" igual a "<valor>"
    Então o status code da resposta deve ser 400
    E o corpo deve respeitar o contrato "erro-validacao.schema.json"
    E o erro de validação deve informar "<mensagem>" para o campo "<campo>"

    Exemplos:
      | campo     | valor | mensagem                                  |
      | nome      |       | O nome da cidade é obrigatório            |
      | uf        |       | O estado (UF) é obrigatório               |
      | populacao | NULO  | A população é obrigatória                 |
      | populacao | 0     | A população deve ser um número positivo   |
      | populacao | -10   | A população deve ser um número positivo   |

  @negativo
  Cenário: Recusar cadastro com JSON malformado
    Quando eu envio POST para "/api/cidades" com o corpo bruto:
      """
      { "nome": "Curitiba", "uf":
      """
    Então o status code da resposta deve ser 400
