# language: pt
@ciclo-de-vida @esg
Funcionalidade: Ciclo de vida completo de uma cidade monitorada
  Como gestor público
  Quero cadastrar, consultar, atualizar, listar e remover uma cidade
  Para garantir que todas as operações da API funcionam em conjunto

  @positivo @contrato
  Cenário: Cadastrar, consultar, atualizar, listar e remover uma cidade
    Quando eu cadastro uma cidade com os dados:
      | nome        | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Cidade Ciclo | MG | 300000   | 40                 | 50              | 60                  |
    Então o status code da resposta deve ser 201
    E o corpo deve respeitar o contrato "cidade.schema.json"
    E o score ESG da cidade deve ser "50"

    Quando eu consulto a cidade cadastrada pelo seu id
    Então o status code da resposta deve ser 200
    E o corpo deve respeitar o contrato "cidade.schema.json"

    Quando eu atualizo a cidade cadastrada com os dados:
      | nome        | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Cidade Ciclo | MG | 310000   | 70                 | 80              | 90                  |
    Então o status code da resposta deve ser 200
    E o score ESG da cidade deve ser "80"

    Quando eu listo as cidades filtrando por UF "MG"
    Então o status code da resposta deve ser 200
    E o corpo deve respeitar o contrato "lista-cidades.schema.json"
    E a lista deve conter a cidade cadastrada

    Quando eu removo a cidade cadastrada
    Então o status code da resposta deve ser 204
    E a cidade removida não deve mais ser encontrada
