# BANK API
## Introdução
Este projeto tem por objetivo a evolução profissional no desenvolvimento de aplicações back-end reais por meio da criação
de um projeto que simule um sistema bancário.

## Ambiente de desenvolvimento
Tecnologias utilizadas:
* Java 8
* Kotlin
* Maven
* Docker/Docker compose
* JUnit
* Mockito
* Swagger
* Apache Kafka

O arquivo docker-compose.yml é utilizado para definir as configurações do banco de dados necessárias para o funcionamento da API.
 
Antes de rodar o projeto no ambiente de desenvolvimento, é necessário executar o seguinte comando:
```
docker-compose --file docker-compose-prod.yml up
```
## Build
Todos os comandos listados a seguir, devem ser executados a partir da raíz do projeto.

Para compilar o projeto, utilize:
```
mvn build
```

Para a execução dos testes, utilize:
```
mvn test
```

Para execução da aplicação, utilize:
```
mvn spring-boot:run -Dspring.profiles.active=prod
```