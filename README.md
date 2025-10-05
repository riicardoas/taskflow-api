# Taskflow API

API de gerenciamento de tarefas internas (Tasks e Subtasks) com usuários vinculados.

- **Stack:** Java 21 + Spring Boot 3, MongoDB
- **Padrões:** REST, validação com Bean Validation, paginação e filtros
- **Doc:** OpenAPI (Swagger UI)
- **Entrega:** Dockerfile + docker-compose
- **Testes:** JUnit 5, Mockito, MockMvc
---

## Como executar

A API pode ser executada de duas formas:
- **Modo local (sem Docker)** — ideal para desenvolvimento rápido.
- **Modo containerizado (Docker Compose)** — ideal para ambiente isolado e reprodutível.


### Pré‑requisitos
- **Java 21** e **Maven 3.9+**
- **MongoDB** local ou via Docker
- (Opcional) **Docker** e **Docker Compose**

A aplicação lê a URI do Mongo pela variável:  
`SPRING_DATA_MONGODB_URI` (default: `mongodb://localhost:27017/taskflow-api`).

### 1) Rodando em modo local (sem Docker)
```bash
mvn clean test
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.docker.compose.enabled=false"

```

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`

### 2) Rodando com Docker Compose

```bash
# build e sobe app + mongodb
mvn clean package -DskipTests
docker compose build --no-cache
docker compose up -d

# Undeploy:
docker-compose down

```

- URL base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`
- MongoDB: `localhost:27017` (db: `taskflow-api`)

> O `docker-compose.yml` já injeta `SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/taskflow-api` para o container da API.

---

## Endpoints

Base path: `/v1`

### Users
- **POST** `/users` — cria usuário  
  **Body:** `{ "name": "Alice", "email": "alice@email.com" }`  
  **Retorno:** `201 Created`, body `{ "idNewUser": "..." }`  
  **Erros:** `409 Conflict` (e-mail já cadastrado), `400 Bad Request` (formato inválido)

- **GET** `/users/{idUser}` — detalhes do usuário  
  **Retorno:** `200 OK`  
  **Erros:** `400 Bad Request` (id inválido), `404 Not Found` (não existe)

### Tasks
- **POST** `/tasks` — cria tarefa  
  **Body (ex.):**  
  ```json
  {
    "titulo": "Implementar autenticação",
    "descricao": "JWT + filtro",
    "statusEnum": "PENDENTE",
    "usuarioId": "66f...."  // opcional; deve existir se informado
  }
  ```
  **Retorno:** `201 Created`, body `{ "idNewTask": "..." }`  
  **Erros:** `400 Bad Request` (usuarioId inválido), `422 Unprocessable Entity` (status CONCLUIDA na criação), `422` (usuarioId não existe)

- **GET** `/tasks` — lista paginada/filtrada  
  **Query params (opcionais):** `status`, `usuarioId`, `page`, `size`  
  **Ex.:** `/v1/tasks?status=EM_ANDAMENTO&usuarioId=66f...&page=0&size=10`  
  **Retorno:** `200 OK`

- **PATCH** `/tasks/{idTask}/status` — atualiza status da tarefa  
  **Query param obrigatório:** `status` (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`)  
  **Regra:** só pode concluir se **todas** as subtarefas estiverem `CONCLUIDA`  
  **Retorno:** `204 No Content`  
  **Erros:** `404 Not Found`, `422 Unprocessable Entity` (há subtarefas pendentes)

### Subtasks
- **POST** `/tasks/{taskId}/subtasks` — cria subtarefa vinculada a uma tarefa  
  **Body (ex.):**  
  ```json
  {
    "titulo": "Criar endpoint",
    "descricao": "POST /login",
    "statusEnum": "PENDENTE"
  }
  ```
  **Retorno:** `201 Created`, body `{ "idSubTask": "..." }`  
  **Erros:** `404 Not Found` (tarefa não existe), `400 Bad Request` (status CONCLUIDA na criação)

- **GET** `/tasks/{taskId}/subtasks` — lista subtarefas da tarefa  
  **Query params:** `page`, `size` (default `0`, `10`)  
  **Retorno:** `200 OK`

- **PATCH** `/subtasks/{idSubTask}/status` — atualiza status da subtarefa  
  **Query param obrigatório:** `status` (`PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`)  
  **Regra:** se `CONCLUIDA`, `dataConclusao` é setada com a data/hora atual  
  **Retorno:** `204 No Content`  
  **Erros:** `404 Not Found`

---

## Regras de negócio-chave

- **Task não pode ser concluída** enquanto existir **qualquer** Subtask com status diferente de `CONCLUIDA`.
- **Subtask concluída** seta `dataConclusao`; alterar para outro status limpa `dataConclusao`.
- **User.email** único (índice no Mongo) e formatado (`@Email` no DTO).

---

## Validações & Tratamento de erros

- Bean Validation nos DTOs (`@NotBlank`, `@Email`, etc.)
- `@RestControllerAdvice` (`ValidationExceptionHandler`) para erros 400 de validação (mapa `campo -> mensagem`)
- `ResponseStatusException` para cenários de domínio:  
  - `400 Bad Request` (ex.: ObjectId inválido)  
  - `404 Not Found` (entidade não existe)  
  - `409 Conflict` (e-mail já cadastrado)  
  - `422 Unprocessable Entity` (regras de negócio)

---

## Testes

- **Service layer (Mockito/JUnit 5):** fluxos felizes e exceções das regras de Task/Subtask.
- **Web layer (MockMvc):** retornos 204/404/422 nos PATCH e 400 de validação em POST.
- Rodar: `mvn test`

---

## 🧱 Tecnologias

- **Java 21**, **Spring Boot 3.3**
- **Spring Web**, **Spring Data MongoDB**
- **Bean Validation (Jakarta Validation)**
- **Lombok**
- **OpenAPI/Swagger UI** 
- **JUnit 5**, **Mockito**, **MockMvc**
- **Docker** e **Docker Compose**
- **Maven**

---

## 🔧 Configuração

`src/main/resources/application.properties`
```properties
spring.application.name=taskflow-api
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI:mongodb://localhost:27017/taskflow-api}
springdoc.swagger-ui.path=/swagger-ui.html
spring.data.mongodb.auto-index-creation=true
```
- Índices:  
  - `Task.statusEnum`, `Task.usuarioId` (`@Indexed`)  
  - `User.email` (`@Indexed(unique=true)`)

---

## 📎 Exemplos de cURL

```bash
# Criar usuário
curl -X POST http://localhost:8080/v1/users \
  -H "Content-Type: application/json" \
  -d '{ "name": "Alice", "email": "alice@email.com" }'

# Criar task
curl -X POST http://localhost:8080/v1/tasks \
  -H "Content-Type: application/json" \
  -d '{ "titulo": "Impl autenticação", "descricao":"JWT", "statusEnum":"PENDENTE", "usuarioId":"66f..." }'

# Listar tasks com filtros
curl "http://localhost:8080/v1/tasks?status=EM_ANDAMENTO&usuarioId=66f...&page=0&size=10"

# Concluir task
curl -X PATCH "http://localhost:8080/v1/tasks/{idTask}/status?status=CONCLUIDA"

# Criar subtask
curl -X POST http://localhost:8080/v1/tasks/{taskId}/subtasks \
  -H "Content-Type: application/json" \
  -d '{ "titulo": "Criar endpoint", "descricao": "POST /login", "statusEnum":"PENDENTE" }'

# Concluir subtask
curl -X PATCH "http://localhost:8080/v1/subtasks/{idSubTask}/status?status=CONCLUIDA"
```
