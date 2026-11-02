# language: pt
@status @governanca
Funcionalidade: Status e saúde da aplicação
  Como responsável pela governança da plataforma Cidades ESG Inteligentes
  Quero consultar o status e a saúde da API
  Para garantir a disponibilidade do serviço e saber em qual ambiente ele está rodando

  @positivo @contrato
  Cenário: Consultar o status da aplicação na rota raiz
    Quando eu envio uma requisição GET para "/"
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "status.schema.json"
    E o campo "aplicacao" deve ser igual a "Cidades ESG Inteligentes"
    E o campo "status" deve ser igual a "UP"
    E o campo "endpoints" deve ser igual a "/api/cidades"
    E o campo "ambiente" deve ser um ambiente conhecido

  @positivo @contrato @eficiencia
  Cenário: Verificar a saúde da aplicação pelo health check
    Quando eu envio uma requisição GET para "/actuator/health"
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve ser um JSON válido
    E o corpo deve respeitar o contrato "health.schema.json"
    E o campo "status" deve ser igual a "UP"
    E o tempo de resposta deve ser menor que 2000 milissegundos

  @negativo
  Cenário: Rota inexistente deve retornar 404 sem expor detalhes internos
    Quando eu envio uma requisição GET para "/rota-que-nao-existe"
    Então o status code da resposta deve ser 404
    E o corpo da resposta não deve conter "Exception"
