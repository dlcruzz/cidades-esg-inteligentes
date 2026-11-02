# language: pt
@consulta
Funcionalidade: Consulta e filtros de cidades
  Como analista de sustentabilidade
  Quero listar, filtrar e consultar cidades cadastradas
  Para comparar os indicadores ESG entre municípios

  Contexto: Cidades cadastradas para as consultas
    Dado que existem as cidades cadastradas:
      | nome        | uf | populacao | indicadorAmbiental | indicadorSocial | indicadorGovernanca |
      | Verdelandia | ZZ | 120000    | 90                 | 80              | 85                  |
      | Solaris     | ZZ | 250000    | 70                 | 75              | 80                  |
      | Ventania    | YY | 80000     | 60                 | 65              | 70                  |

  @positivo @contrato
  Cenário: Listar todas as cidades
    Quando eu listo todas as cidades
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "lista-cidades.schema.json"
    E a lista deve conter todas as cidades cadastradas no cenário
    E o tempo de resposta deve ser menor que 2000 milissegundos

  @positivo @contrato
  Cenário: Filtrar cidades por UF ignorando maiúsculas e minúsculas
    Quando eu listo as cidades filtrando por UF "zz"
    Então o status code da resposta deve ser 200
    E o corpo deve respeitar o contrato "lista-cidades.schema.json"
    E a lista deve conter exatamente 2 cidades
    E todas as cidades da lista devem ter a UF "ZZ"

  @positivo @contrato
  Cenário: Filtrar cidades por parte do nome
    Quando eu listo as cidades filtrando pelo nome "verdela"
    Então o status code da resposta deve ser 200
    E o corpo deve respeitar o contrato "lista-cidades.schema.json"
    E todas as cidades da lista devem ter o nome contendo "verdela"

  @positivo @contrato
  Cenário: Filtro sem resultados devolve lista vazia
    Quando eu listo as cidades filtrando por UF "QQ"
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "lista-cidades.schema.json"
    E a lista deve estar vazia

  @positivo @contrato
  Cenário: Consultar uma cidade pelo id
    Quando eu consulto a cidade cadastrada pelo seu id
    Então o status code da resposta deve ser 200
    E o corpo deve respeitar o contrato "cidade.schema.json"
    E o id da cidade deve continuar o mesmo
    E o campo "uf" deve ser igual a "YY"

  @negativo @contrato
  Cenário: Consultar uma cidade que não existe
    Quando eu consulto a cidade pelo id "999999999"
    Então o status code da resposta deve ser 404
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "erro-nao-encontrado.schema.json"
    E o campo "erro" deve ser igual a "Recurso não encontrado"
    E a mensagem de erro deve citar o id "999999999"

  @negativo
  Cenário: Consultar uma cidade com id que não é número
    Quando eu consulto a cidade pelo id "abc"
    Então o status code da resposta deve ser 400
