# Beans no Spring Boot e neste projeto

Este documento explica o que e um Bean no Spring Boot, por que ele e importante e como ele esta sendo usado no `user-service`.

## O que e um Bean

No Spring, um Bean e um objeto gerenciado pelo container do framework.

Isso significa que o Spring:

- cria a instancia
- controla o ciclo de vida
- injeta dependencias
- disponibiliza esse objeto para outras classes

Em Java puro, normalmente voce faria algo assim:

```java
PasswordEncoder encoder = new BCryptPasswordEncoder();
UserService service = new UserService(userRepository, organizationRepository, roleRepository, encoder);
```

No Spring Boot, voce nao precisa criar esses objetos manualmente quando eles sao Beans.
O Spring faz isso por voce.

## Por que Bean e importante no Spring Boot

Beans sao uma das bases do Spring porque permitem:

- inversao de controle
- injecao de dependencia
- baixo acoplamento entre classes
- configuracao centralizada
- substituicao e teste mais facil

Sem Bean, cada classe precisaria instanciar suas proprias dependencias. Isso deixa o codigo mais rigido, mais dificil de testar e mais dificil de evoluir.

## O que e o container do Spring

Quando a aplicacao sobe, o Spring cria um contexto chamado `ApplicationContext`.

Esse contexto:

- descobre classes anotadas para serem gerenciadas
- registra Beans
- monta as dependencias entre eles
- entrega tudo pronto para uso

No seu projeto, isso comeca em:

- [src/main/java/com/lms/userservice/UserServiceApplication.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/UserServiceApplication.java>)

Com esta anotacao:

```java
@SpringBootApplication
```

Essa anotacao, entre outras coisas:

- habilita component scan
- registra configuracoes automáticas do Spring Boot
- procura classes do pacote `com.lms.userservice` para baixo

## Como um Bean nasce

Em Spring Boot, um Bean pode nascer de duas formas mais comuns:

### 1. Por anotacao estereotipada

Exemplos:

- `@Service`
- `@Repository`
- `@RestController`
- `@Configuration`
- `@RestControllerAdvice`

Quando o Spring encontra uma classe com essas anotacoes durante o scan, ele registra a classe como Bean.

### 2. Por metodo anotado com `@Bean`

Dentro de uma classe `@Configuration`, voce pode ter metodos que retornam objetos que o Spring deve gerenciar.

Exemplo do projeto:

- [src/main/java/com/lms/userservice/config/SecurityConfig.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/config/SecurityConfig.java>)

Nela existem metodos como:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Nesse caso, o objeto retornado passa a ser um Bean gerenciado pelo Spring.

## Tipos de Beans que existem neste projeto

## 1. Beans de controller

Exemplos:

- [src/main/java/com/lms/userservice/controller/UserController.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/controller/UserController.java>)
- [src/main/java/com/lms/userservice/controller/RoleController.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/controller/RoleController.java>)

Essas classes usam:

- `@RestController`

O Spring cria esses Beans para receber requisicoes HTTP.

Papel tecnico:

- expor endpoints
- receber request
- validar entrada
- delegar para a camada de service

## 2. Beans de service

Exemplos:

- [src/main/java/com/lms/userservice/service/UserService.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/service/UserService.java>)
- [src/main/java/com/lms/userservice/service/RoleService.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/service/RoleService.java>)

Essas classes usam:

- `@Service`

O Spring cria esses Beans para encapsular regra de negocio.

No `UserService`, por exemplo, o Bean precisa de:

- `UserRepository`
- `OrganizationRepository`
- `RoleRepository`
- `PasswordEncoder`

O Spring injeta tudo isso automaticamente.

## 3. Beans de repository

Exemplos:

- [src/main/java/com/lms/userservice/repository/UserRepository.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/repository/UserRepository.java>)
- [src/main/java/com/lms/userservice/repository/RoleRepository.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/repository/RoleRepository.java>)
- [src/main/java/com/lms/userservice/repository/OrganizationRepository.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/repository/OrganizationRepository.java>)

Essas interfaces herdam de `JpaRepository`.

Voce nao escreveu a implementacao concreta delas.
O Spring Data JPA cria proxies dinamicos e registra esses proxies como Beans.

Ou seja:

- `UserRepository` e um Bean
- `RoleRepository` e um Bean
- `OrganizationRepository` e um Bean

## 4. Beans de configuracao

Exemplos:

- [src/main/java/com/lms/userservice/config/SecurityConfig.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/config/SecurityConfig.java>)
- [src/main/java/com/lms/userservice/config/OpenApiConfig.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/config/OpenApiConfig.java>)

Essas classes usam:

- `@Configuration`

Elas servem para declarar Beans de infraestrutura.

### Beans definidos em `SecurityConfig`

Hoje essa classe cria:

- `SecurityFilterChain`
- `PasswordEncoder`
- `CorsConfigurationSource`

Sem esses Beans:

- o Spring Security nao saberia como montar o filtro HTTP
- a senha do usuario nao poderia ser codificada via injeção de dependencia
- o CORS nao estaria configurado

### Bean definido em `OpenApiConfig`

Hoje essa classe cria:

- `OpenAPI`

Esse Bean e consumido pela biblioteca do Swagger/OpenAPI para montar a documentacao da API.

## 5. Beans de tratamento global

Exemplo:

- [src/main/java/com/lms/userservice/exception/GlobalExceptionHandler.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/exception/GlobalExceptionHandler.java>)

Essa classe usa:

- `@RestControllerAdvice`

O Spring registra isso como Bean especial de tratamento global de excecoes.

Funcao:

- capturar excecoes lancadas durante o fluxo HTTP
- transformar excecao Java em resposta HTTP padronizada

## 6. Beans automaticos do Spring Boot

Nem todo Bean do projeto foi escrito manualmente.

O Spring Boot auto-configura varios Beans, por exemplo:

- `DataSource`
- `EntityManagerFactory`
- `PlatformTransactionManager`
- componentes do Jackson
- componentes do Tomcat embarcado
- infraestrutura do Spring MVC

Esses Beans aparecem porque:

- voce adicionou starters no `pom.xml`
- voce configurou `application.yml`

Exemplo:

Como existe configuracao de datasource em:

- [src/main/resources/application.yml](</D:/Projetos/LMS_Learning_Service/User_service/src/main/resources/application.yml>)

o Spring Boot monta automaticamente:

- pool Hikari
- datasource JDBC
- integracao com Hibernate

## Como a injecao de dependencia acontece no projeto

O projeto usa principalmente injecao por construtor.

Exemplo conceitual:

```java
private final UserService userService;
```

Com:

```java
@RequiredArgsConstructor
```

o Lombok gera o construtor automaticamente.

Depois, o Spring chama esse construtor e injeta o Bean correto.

### Exemplo real: `UserController`

O `UserController` depende de:

- `UserService`

Como `UserService` e um Bean `@Service`, o Spring consegue injetar.

Fluxo:

1. Spring cria `UserService`
2. Spring cria `UserController`
3. no construtor do `UserController`, injeta `UserService`

### Exemplo real: `UserService`

O `UserService` depende de:

- `UserRepository`
- `OrganizationRepository`
- `RoleRepository`
- `PasswordEncoder`

O Spring monta isso porque:

- repositories sao Beans do Spring Data JPA
- `PasswordEncoder` e Bean declarado em `SecurityConfig`

## Exemplo pratico com `PasswordEncoder`

No `UserService.create(...)`, existe esta linha:

```java
passwordEncoder.encode(request.password())
```

Esse `passwordEncoder` nao foi criado dentro do `UserService`.
Ele foi injetado pelo Spring.

A origem dele esta em:

- [src/main/java/com/lms/userservice/config/SecurityConfig.java](</D:/Projetos/LMS_Learning_Service/User_service/src/main/java/com/lms/userservice/config/SecurityConfig.java>)

Com este Bean:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Importancia:

- separa configuracao da regra de negocio
- facilita trocar implementacao depois
- facilita testes

## Exemplo pratico com `UserRepository`

No `UserService`, esta chamada:

```java
userRepository.save(user)
```

funciona porque o Spring Data cria uma implementacao dinamica do repository e registra essa implementacao como Bean.

Sem isso, voce teria que:

- escrever uma classe concreta manualmente
- gerenciar `EntityManager`
- abrir e fechar recursos por conta propria

## Escopo padrao dos Beans

Por padrao, os Beans do Spring sao `singleton`.

Isso significa:

- o Spring cria uma unica instancia por contexto
- essa mesma instancia e reutilizada

No seu projeto, isso vale para:

- controllers
- services
- configuracoes
- handlers globais
- boa parte da infraestrutura

Isso melhora:

- desempenho
- organizacao
- reutilizacao

## Ciclo de vida de um Bean

De forma simplificada:

1. Spring encontra a definicao do Bean
2. cria a instancia
3. resolve dependencias
4. inicializa o Bean
5. disponibiliza para uso
6. destroi ao encerrar a aplicacao, quando aplicavel

No startup da sua aplicacao, isso acontece em cadeia.

Exemplo:

1. Spring sobe o contexto
2. cria Beans de configuracao
3. cria Beans de infraestrutura
4. cria repositories
5. cria services
6. cria controllers
7. sobe o Tomcat

## O que aconteceria sem Beans

Sem o modelo de Beans do Spring, voce precisaria:

- instanciar controllers manualmente
- instanciar services manualmente
- montar repositories manualmente
- controlar dependencias entre objetos
- gerenciar infraestrutura de seguranca e transacao

Isso aumentaria muito a complexidade do codigo.

## Bean e desacoplamento

Um dos grandes ganhos de Bean e desacoplamento.

Exemplo:

O `UserService` depende da abstracao:

- `PasswordEncoder`

e nao da implementacao concreta escrita diretamente dentro dele.

Hoje voce usa:

- `BCryptPasswordEncoder`

Mas no futuro poderia trocar para outra implementacao mudando a configuracao do Bean, sem reescrever a regra de negocio.

## Bean e testes

Beans tambem ajudam muito em testes porque:

- voce pode mockar dependencias
- pode substituir Beans em contexto de teste
- pode testar cada camada separadamente

Exemplo conceitual:

- testar `UserService` simulando `UserRepository`
- testar `UserController` simulando `UserService`

## Resumo dos Beans mais importantes neste projeto

### Beans de negocio

- `UserService`
- `RoleService`

### Beans HTTP

- `UserController`
- `RoleController`
- `GlobalExceptionHandler`

### Beans de persistencia

- `UserRepository`
- `RoleRepository`
- `OrganizationRepository`

### Beans de configuracao

- `SecurityFilterChain`
- `PasswordEncoder`
- `CorsConfigurationSource`
- `OpenAPI`

### Beans de infraestrutura criados automaticamente

- `DataSource`
- `EntityManagerFactory`
- `TransactionManager`
- componentes MVC
- Tomcat embarcado

## Resumo final

Neste projeto, Bean e importante porque e o mecanismo que permite ao Spring Boot:

1. criar e conectar controllers, services e repositories
2. aplicar injecao de dependencia sem codigo manual de montagem
3. registrar configuracoes como seguranca, CORS e Swagger
4. integrar a aplicacao com banco, transacao e servidor HTTP
5. manter o codigo organizado, testavel e desacoplado

Em outras palavras:

- sem Bean, o Spring Boot praticamente perde a principal vantagem arquitetural dele
- com Bean, voce consegue focar na regra de negocio e deixar o framework cuidar da composicao da aplicacao
