# LMS Learning Platform - user-service

Microservico inicial do LMS para cadastro e consulta de usuarios, organizacoes e papeis.

## Stack

- Java 17
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL / Supabase
- Swagger / OpenAPI
- Maven Wrapper

## Estrutura

```text
user-service
\-- src/main/java/com/lms/userservice
    |-- config
    |-- controller
    |-- dto
    |-- entity
    |-- exception
    |-- repository
    \-- service
```

## Configuracao

O projeto le a conexao do banco via variaveis de ambiente.

### Opcao 1: conexao direta

Use esta opcao se a sua internet/rede suporta IPv6:

```powershell
$env:DB_URL="jdbc:postgresql://db.gvsimktwqxetrkkwarjq.supabase.co:5432/postgres?sslmode=require"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="SUA_SENHA_DO_BANCO"
```

### Opcao 2: session pooler

Use esta opcao se estiver rodando localmente e a conexao direta nao abrir.
Para este projeto, o `session pooler` informado foi:

```powershell
$env:DB_URL="jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:DB_USERNAME="postgres.gvsimktwqxetrkkwarjq"
$env:DB_PASSWORD="Lms_176_prototype_pedro"
```

## Como abrir no IntelliJ

1. Abra a pasta `user-service` no IntelliJ IDEA.
2. Aguarde a importacao Maven pelo `mvnw.cmd`.
3. Ative o annotation processing do Lombok se o IntelliJ pedir.
4. Em `Run/Debug Configurations`, adicione `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` nas environment variables.
5. Se quiser trocar a porta, adicione tambem `SERVER_PORT`.
6. Rode a classe `com.lms.userservice.UserServiceApplication`.

## Como rodar no terminal

```powershell
cd user-service
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
$env:SERVER_PORT="8083"
$env:DB_URL="jdbc:postgresql://aws-1-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:DB_USERNAME="postgres.gvsimktwqxetrkkwarjq"
$env:DB_PASSWORD="Lms_176_prototype_pedro"
.\mvnw.cmd spring-boot:run
```

Se o terminal disser que `JAVA_HOME` nao esta definido, mantenha a linha acima antes do `mvnw.cmd`.

## Como saber se subiu

Quando estiver tudo certo, o log termina com uma linha parecida com esta:

```text
Tomcat started on port 8083 (http) with context path '/'
Started UserServiceApplication in ...
```

## Como testar

Com a aplicacao rodando na porta `8083`, voce pode usar:

- Swagger UI: `http://localhost:8083/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8083/v3/api-docs`
- Roles: `http://localhost:8083/api/v1/roles`
- Users: `http://localhost:8083/api/v1/users`

Exemplo no navegador, Postman ou PowerShell:

```powershell
Invoke-WebRequest -Uri "http://localhost:8083/api/v1/roles" -UseBasicParsing
```

## Documentacao tecnica

- Fluxo tecnico de criacao de usuario: [docs/FLUXO_CRIACAO_USUARIO.md](</D:/Projetos/LMS_Learning_Service/User_service/docs/FLUXO_CRIACAO_USUARIO.md>)

## Endpoints iniciais

- `GET /api/v1/roles`
- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `POST /api/v1/users`
- `PATCH /api/v1/users/{id}/status`

## Exemplo de criacao de usuario

```json
{
  "organizationId": "11111111-1111-1111-1111-111111111111",
  "fullName": "Pedro da Silva",
  "email": "pedro@empresa.com",
  "password": "SenhaSegura123",
  "phone": "+55 11 99999-0000",
  "roles": ["ADMIN", "MANAGER"]
}
```
