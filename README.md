# Portfolio Manager

Sistema de gerenciamento de portfólio de projetos desenvolvido com Java 21 + Spring Boot 3.

## Tecnologias

- Java 21
- Spring Boot 3.3
- Spring Security (Basic Auth)
- Spring Data JPA + Hibernate
- PostgreSQL 16
- Flyway (migrations)
- MapStruct
- Lombok
- Springdoc OpenAPI (Swagger)
- JUnit 5 + Mockito

## Como executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker + Docker Compose

### Passos

```bash
# Subir banco de dados
docker compose up -d

# Executar a aplicação
mvn spring-boot:run

# Acessar Swagger
http://localhost:8080/swagger-ui.html

# Credenciais
usuário: admin
senha: admin123
```

## Endpoints principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | /api/v1/members | Criar membro (via API mockada) |
| GET | /api/v1/members | Listar membros |
| GET | /api/v1/members/{id} | Buscar membro por id |
| POST | /api/v1/projects | Criar projeto |
| GET | /api/v1/projects | Listar projetos (paginado + filtros) |
| GET | /api/v1/projects/{id} | Buscar projeto por id |
| PUT | /api/v1/projects/{id} | Atualizar projeto |
| DELETE | /api/v1/projects/{id} | Excluir projeto |
| PATCH | /api/v1/projects/{id}/status | Atualizar status |
| POST | /api/v1/projects/{id}/membros/{membroId} | Adicionar membro |
| DELETE | /api/v1/projects/{id}/membros/{membroId} | Remover membro |
| GET | /api/v1/portfolio/report | Relatório do portfólio |

## Regras de negócio principais

- Status de projetos seguem sequência fixa: `EM_ANALISE → ANALISE_REALIZADA → ANALISE_APROVADA → INICIADO → PLANEJADO → EM_ANDAMENTO → ENCERRADO`. Não é possível pular etapas ou voltar.
- `CANCELADO` pode ser aplicado a partir de qualquer status.
- Projetos com status `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO` não podem ser excluídos (403).
- Apenas membros com atribuição `"funcionario"` podem ser alocados em projetos.
- Um membro pode estar em no máximo 3 projetos ativos simultaneamente.
- Um projeto comporta no máximo 10 membros; deve manter ao menos 1 membro alocado.
- Risco calculado dinamicamente por orçamento e prazo:
  - **BAIXO**: orçamento ≤ R$ 100.000 E prazo ≤ 3 meses
  - **MÉDIO**: orçamento entre R$ 100.001–500.000 OU prazo de 3–6 meses
  - **ALTO**: orçamento > R$ 500.000 OU prazo > 6 meses

## Executar testes

```bash
mvn clean test
mvn jacoco:report
# Relatório em: target/site/jacoco/index.html
```

Cobertura mínima exigida: 70% de linhas nos pacotes `service` e `domain`.
