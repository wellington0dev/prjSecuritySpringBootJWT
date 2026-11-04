# prjSecuritySpringBootJWT

Sistema REST em Spring Boot com autenticação JWT para gerenciamento de departamentos, empregados, projetos e tarefas de projeto.

## Tecnologias

- Java 17
- Spring Boot 3.3.2
- Spring Security + JWT (JJWT)
- Spring Data JPA
- H2 Database
- Springdoc OpenAPI
- Frontend estático (HTML, CSS e JavaScript)

## Como executar

1. Clone o repositório:
   ```bash
   git clone https://github.com/wellington0dev/prjSecuritySpringBootJWT.git
   ```

2. Crie o arquivo de configuração local:
   ```bash
   cp application.properties.example application.properties
   ```

3. Ajuste o `jwt.secret` em `application.properties`.

4. Importe o projeto como um projeto Maven na IDE e execute a classe:
   ```
   controller.CtrlPrograma
   ```

5. A aplicação estará disponível em:
   ```
   http://localhost:8081
   ```

> O arquivo `application.properties`, o banco local (`data/`) e a pasta `target/` não são versionados.

## Autenticação

O login é feito em:

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "conta": "usuario",
  "senha": "senha"
}
```

A resposta contém o token JWT:

```json
{
  "meuJwt": "eyJhbGciOiJIUzUxMiJ9..."
}
```

Nas demais requisições, envie:

```http
Authorization: Bearer <token>
```

## Principais endpoints

### Departamentos

- `GET /depto/listar`
- `GET /depto/listar/{id}`
- `POST /depto/incluir`
- `PUT /depto/alterar/{id}`
- `DELETE /depto/remover/{id}`

### Empregados

- `GET /empregado/listar`
- `GET /empregado/listar/{id}`
- `POST /empregado/incluir`
- `PUT /empregado/alterar/{id}`
- `DELETE /empregado/remover/{id}`

### Projetos

- `GET /proj/listar`
- `GET /proj/listar/{id}`
- `POST /proj/incluir`
- `PUT /proj/alterar/{id}`
- `DELETE /proj/remover/{id}`

### Tarefas do projeto

- `GET /proj/{idProjeto}/tarefas/listar`
- `GET /proj/{idProjeto}/tarefas/listar/{numOrdem}`
- `POST /proj/{idProjeto}/tarefas/incluir`
- `PUT /proj/{idProjeto}/tarefas/alterar/{numOrdem}`
- `DELETE /proj/{idProjeto}/tarefas/remover/{numOrdem}`

Exemplo de tarefa:

```json
{
  "numOrdem": 1,
  "descricao": "Levantar requisitos do projeto",
  "empregado": {
    "id": 1
  }
}
```

## Frontend

Após o login, as páginas estão disponíveis em:

- `/departamentos.html`
- `/empregados.html`
- `/projetos.html`
- `/tarefas.html`
