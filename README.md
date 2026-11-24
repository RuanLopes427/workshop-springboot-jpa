# Workshop Spring Boot + JPA

API REST de um sistema simples de pedidos (usuários, produtos, categorias, pedidos e pagamentos), construída com **Spring Boot**, **JPA/Hibernate** e banco relacional.

![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-7-59666C?logo=hibernate&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-in--memory-1F6FEB)
![Status](https://img.shields.io/badge/status-projeto%20de%20estudo-yellow)

---

## Sobre o projeto

Este é o meu **primeiro projeto com Spring Boot**. Ele foi desenvolvido acompanhando o workshop de Spring Boot + JPA do curso **Java COMPLETO**, do professor **Nelio Alves** (DevSuperior).

O curso foi gravado com versões mais antigas do Spring. Eu fiz o projeto com **Spring Boot 4 e Java 25**, então várias coisas não funcionaram como na aula e precisei investigar e adaptar (veja [Desafios que resolvi](#desafios-que-resolvi)).

**O que o projeto pratica:**

- Arquitetura em camadas: **Resource → Service → Repository**
- Injeção de dependência com o Spring
- Mapeamento objeto-relacional com JPA (1:N, N:N, N:N com atributos e 1:1)
- CRUD com os verbos HTTP e os status corretos (`200`, `201`, `204`, `404`, `400`)
- Tratamento de exceções centralizado com `@RestControllerAdvice`
- Perfis de execução: **test** (H2 em memória) e **dev** (PostgreSQL)
- Carga inicial de dados (*seeding*) para testes

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 25 |
| Framework | Spring Boot 4 (Spring Web MVC, Spring Data JPA) |
| ORM | Hibernate 7 / Jakarta Persistence |
| Banco (teste) | H2 em memória, com console web |
| Banco (dev) | PostgreSQL |
| Build | Maven (wrapper incluso, não precisa instalar) |
| Testes manuais | Postman |

---

## Modelo de domínio

```mermaid
classDiagram
    direction LR
    class User {
        Long id
        String name
        String email
        String phone
        String password
    }
    class Order {
        Long id
        Instant moment
        OrderStatus orderStatus
        +getTotal() Double
    }
    class OrderItem {
        Integer quantity
        Double price
        +getSubTotal() Double
    }
    class Product {
        Long id
        String name
        String description
        Double price
        String imgUrl
    }
    class Category {
        Long id
        String name
    }
    class Payment {
        Long id
        Instant moment
    }
    class OrderStatus {
        <<enumeration>>
        WAITING_PAYMENT
        PAID
        SHIPPED
        DELIVERED
        CANCELED
    }

    User "1" --> "*" Order : client
    Order "1" --> "*" OrderItem : items
    OrderItem "*" --> "1" Product
    Product "*" --> "*" Category
    Order "1" --> "0..1" Payment
    Order --> OrderStatus
```

### Como cada relação foi mapeada

| Relação | Exemplo | Mapeamento JPA |
|---|---|---|
| Um para muitos | `User` → `Order` | `@ManyToOne` + `@OneToMany(mappedBy)` |
| Muitos para muitos | `Product` ↔ `Category` | `@ManyToMany` + `@JoinTable` (`tb_product_category`) |
| Muitos para muitos **com atributos** | `Order` ↔ `Product` (via `OrderItem`) | Classe de associação com chave composta `@EmbeddedId` (`OrderItemPk`), guardando quantidade e preço no momento da compra |
| Um para um | `Order` → `Payment` | `@OneToOne` + `@MapsId` (o pagamento usa o mesmo id do pedido) |

O `OrderStatus` é gravado no banco como **código numérico** (1 a 5), em vez do texto do enum. Assim, reordenar o enum no código não corrompe os dados.

---

## Arquitetura

```
 Cliente HTTP (Postman, front-end...)
        │
        ▼
 ┌──────────────┐     ┌──────────────┐     ┌─────────────────────┐     ┌────────┐
 │   Resource   │ ──▶ │   Service    │ ──▶ │     Repository      │ ──▶ │ Banco  │
 │ (controller) │     │ (regras de   │     │ (Spring Data JPA)   │     │        │
 │              │     │  negócio)    │     │                     │     │        │
 └──────────────┘     └──────────────┘     └─────────────────────┘     └────────┘
        ▲
        │ exceções viram respostas HTTP padronizadas
 ┌──────────────────────────┐
 │ ResourceExceptionHandler │  (@RestControllerAdvice)
 └──────────────────────────┘
```

```
src/main/java/com/educandoweb/course
├── config/          # TestConfig: popula o banco no perfil "test"
├── entities/        # entidades JPA (+ enums/ e pk/)
├── repositories/    # interfaces JpaRepository
├── services/        # regras de negócio (+ exceptions/)
└── resources/       # controllers REST (+ exceptions/ com o handler global)
```

---

## Endpoints

Base: `http://localhost:8080`

| Recurso | Método | Rota | Descrição |
|---|---|---|---|
| Usuários | `GET` | `/users` | Lista todos |
| | `GET` | `/users/{id}` | Busca por id |
| | `POST` | `/users` | Cria (retorna `201` + header `Location`) |
| | `PUT` | `/users/{id}` | Atualiza nome, e-mail e telefone |
| | `DELETE` | `/users/{id}` | Remove |
| Produtos | `GET` | `/products` · `/products/{id}` | Lista / busca |
| | `POST` · `PUT` · `DELETE` | `/products` · `/products/{id}` | Cria, atualiza, remove |
| Categorias | `GET` | `/categories` · `/categories/{id}` | Lista / busca |
| | `POST` · `PUT` · `DELETE` | `/categories` · `/categories/{id}` | Cria, atualiza, remove |
| Pedidos | `GET` | `/orders` · `/orders/{id}` | Lista / busca (com itens, pagamento e total) |
| | `POST` · `PUT` · `DELETE` | `/orders` · `/orders/{id}` | Cria, atualiza, remove |

### Exemplo: `GET /orders/1`

```json
{
  "id": 1,
  "moment": "2019-06-20T19:53:07Z",
  "orderStatus": "PAID",
  "client": { "id": 1, "name": "Ruan Lopes", "...": "..." },
  "items": [
    { "product": { "id": 1, "name": "The Lord of the Rings", "...": "..." }, "quantity": 2, "price": 90.5, "subTotal": 181.0 },
    { "product": { "id": 3, "name": "Macbook Pro", "...": "..." }, "quantity": 1, "price": 1250.0, "subTotal": 1250.0 }
  ],
  "payment": { "id": 1, "moment": "2019-06-20T21:53:07Z" },
  "total": 1431.0
}
```

As datas seguem o padrão **ISO 8601 em UTC** (`Instant`). O `subTotal` e o `total` são calculados pela própria entidade, não ficam gravados no banco.

---

## Tratamento de erros

Todas as exceções de negócio passam por um handler global e voltam num formato único:

```json
{
  "timestamp": "2026-11-24T19:07:09.351Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Resource not found. Id: 99",
  "path": "/users/99"
}
```

| Situação | Exceção | Status |
|---|---|---|
| Buscar, atualizar ou excluir um id inexistente | `ResourceNotFoundException` / `EntityNotFoundException` | `404 Not Found` |
| Excluir um registro que outros usam (ex.: usuário com pedidos) | `DataIntegrityViolationException` → `DatabaseException` | `400 Bad Request` |

---

## Como executar

**Pré-requisito:** JDK 25 ou superior. O Maven já vem no projeto (`mvnw`).

```bash
git clone https://github.com/RuanLopes427/workshop-springboot-jpa.git
cd workshop-springboot-jpa
```

### Perfil `test` (recomendado para testar): H2 em memória com dados de exemplo

```bash
# Linux / macOS / Git Bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# Windows (PowerShell)
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=test"
```

A API sobe em `http://localhost:8080` já com usuários, produtos, categorias e pedidos cadastrados.

**Console do H2:** acesse `http://localhost:8080/h2-console` e use:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `sa` |
| Password | *(vazio)* |

> O banco fica só na memória: ele é recriado a cada execução e some quando a aplicação para.

### Perfil `dev`: PostgreSQL local

1. Crie um banco chamado `springboot_workshop` no PostgreSQL.
2. Configure a conexão em `src/main/resources/application-dev.properties`.
3. Rode `./mvnw spring-boot:run`. O perfil `dev` é o padrão em `application.properties`.

O Hibernate cria e atualiza as tabelas sozinho (`ddl-auto=update`).

---

## Desafios que resolvi

Por usar versões mais novas que as do curso, alguns comportamentos mudaram, e esses foram os pontos em que mais aprendi:

- **`deleteById` não lança mais exceção para id inexistente.** No Spring Data 3+, ele simplesmente não faz nada e a API devolvia `204`. Passei a verificar com `existsById` antes de excluir, para responder `404`.
- **Atualizar um id inexistente gerava `500`.** O `getReferenceById` lança `EntityNotFoundException` só quando o objeto é usado. Tratei essa exceção no handler global para responder `404`.
- **Excluir um usuário com pedidos gerava `500`.** O banco bloqueia por integridade referencial (chave estrangeira). Capturei a `DataIntegrityViolationException` e devolvo `400` com uma mensagem clara.
- **Recursão infinita no JSON.** Usuário → pedidos → cliente → pedidos... Quebrei o ciclo com `@JsonIgnore` no lado certo de cada associação.
- **Perfis do Spring.** O nome do perfil ativo, o sufixo do arquivo `application-{perfil}.properties` e o `@Profile` precisam ser idênticos. Se não forem, o Spring ignora a configuração sem mostrar erro.

---

## Próximos passos

- [ ] Usar **DTOs** para separar a entidade do que a API expõe
- [ ] Validação de entrada com **Bean Validation** (`@NotBlank`, `@Email`...)
- [ ] `BigDecimal` para valores monetários
- [ ] Testes automatizados com **JUnit** e **MockMvc**
- [ ] Documentação interativa com **Swagger / OpenAPI**
- [ ] **Docker Compose** com PostgreSQL

---

## Créditos

Projeto de estudo baseado no workshop do curso **Java COMPLETO**, do professor **Nelio Alves** ([DevSuperior](https://devsuperior.com.br)).

## Autor

**Ruan Lopes**, estudante de Análise e Desenvolvimento de Sistemas.

[![GitHub](https://img.shields.io/badge/GitHub-RuanLopes427-181717?logo=github)](https://github.com/RuanLopes427)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Ruan%20Lopes-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ruan-lopes-8556573b0)
