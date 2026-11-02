# language: pt
@atualizacao @remocao
Funcionalidade: Atualização e remoção de cidades com rastreabilidade
  Como responsável pela governança dos dados ESG
  Quero atualizar e remover cidades mantendo o registro de quando cada dado foi alterado
  Para garantir integridade, rastreabilidade e conformidade das informações

  Contexto: Uma cidade cadastrada
    Dado que existe uma cidade cadastrada chamada "Aurora" na UF "SC" com indicadores 60, 55 e 70

  @positivo @contrato @esg
  Cenário: Atualizar os indicadores de uma cidade existente
    Quando eu atualizo a cidade cadastrada com os dados:
      | nome             | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Aurora Renovada  | SC | 95000     | 80                 | 82              | 90                  |
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "cidade.schema.json"
    E o id da cidade deve continuar o mesmo
    E a cidade retornada deve corresponder aos dados enviados
    E o score ESG da cidade deve ser "84"

  @positivo @governanca
  Cenário: A atualização registra a data da alteração para fins de rastreabilidade
    Quando eu atualizo a cidade cadastrada com os dados:
      | nome             | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Aurora Auditada  | SC | 100000    | 61                 | 56              | 71                  |
    Então o status code da resposta deve ser 200
    E o campo "atualizadoEm" não deve estar vazio
    E a data de atualização deve ser posterior à data de cadastro

  @negativo @contrato
  Cenário: Recusar atualização com indicador inválido e preservar os dados originais
    Quando eu atualizo a cidade cadastrada com o campo "indicadorSocial" igual a "150"
    Então o status code da resposta deve ser 400
    E o corpo deve respeitar o contrato "erro-validacao.schema.json"
    E o erro de validação deve informar "O indicador social deve ser entre 0 e 100" para o campo "indicadorSocial"
    E a cidade cadastrada deve continuar com os dados originais

  @negativo @contrato
  Cenário: Atualizar uma cidade que não existe
    Quando eu atualizo a cidade de id "999999999" com dados válidos
    Então o status code da resposta deve ser 404
    E o corpo deve respeitar o contrato "erro-nao-encontrado.schema.json"
    E a mensagem de erro deve citar o id "999999999"

  @positivo
  Cenário: Remover uma cidade existente
    Quando eu removo a cidade cadastrada
    Então o status code da resposta deve ser 204
    E o corpo da resposta deve estar vazio
    E a cidade removida não deve mais ser encontrada

  @negativo
  Cenário: Remover a mesma cidade duas vezes
    Quando eu removo a cidade cadastrada
    Então o status code da resposta deve ser 204
    Quando eu removo a cidade cadastrada
    Então o status code da resposta deve ser 404
    E o corpo deve respeitar o contrato "erro-nao-encontrado.schema.json"

  @negativo @contrato
  Cenário: Remover uma cidade que não existe
    Quando eu removo a cidade de id "999999999"
    Então o status code da resposta deve ser 404
    E o corpo deve respeitar o contrato "erro-nao-encontrado.schema.json"
    E a mensagem de erro deve citar o id "999999999"
