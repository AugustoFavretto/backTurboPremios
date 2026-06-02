# Turbo Prêmios — Backend API

Backend REST para a plataforma de rifas Turbo Prêmios. Desenvolvido com Kotlin, Spring Boot 3 e PostgreSQL (Supabase).

## Stack

- **Kotlin** + **Spring Boot 3.2**
- **Spring Security** + JWT (JJWT)
- **Spring Data JPA** + PostgreSQL (Supabase)
- **Flyway** para migrações
- **OpenAPI 3 / Swagger UI**
- **JUnit 5** + MockK para testes
- **Docker / Docker Compose**

## Executando localmente

### Pré-requisitos

- JDK 17+
- Gradle 8+ (ou use o wrapper `./gradlew`)
- PostgreSQL (ou Supabase)

### Configuração

Copie o arquivo de exemplo e preencha as variáveis:

```bash
cp .env.example .env
```

Variáveis necessárias:

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | JDBC URL do PostgreSQL (Supabase) |
| `DATABASE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave secreta JWT (mínimo 256 bits) |

### Rodando com Gradle

```bash
./gradlew bootRun
```

A API estará disponível em: `http://localhost:8080/v1`

Swagger UI: `http://localhost:8080/v1/swagger-ui/index.html`

### Rodando com Docker

```bash
docker-compose up --build
```

## Estrutura do Projeto

```
src/main/kotlin/com/turbopremios/
├── config/          # AppProperties, OpenAPI
├── security/        # JWT, SecurityConfig, Filter
├── common/          # ApiResponse, PaginatedResponse
├── exceptions/      # AppExceptions, GlobalExceptionHandler
├── auth/            # Login, Register, JWT auth
├── campaigns/       # Campanhas de rifas
├── purchases/       # Compras + webhook PIX
├── tickets/         # Bilhetes + geração única
├── affiliate/       # Painel de afiliados
├── dashboard/       # Stats e atividades
├── winners/         # Ganhadores
└── integrations/    # Gateway PIX desacoplado
```

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Não | Login |
| POST | `/auth/register` | Não | Cadastro |
| GET | `/auth/me` | Sim | Usuário atual |
| GET | `/campaigns` | Não | Listar campanhas |
| GET | `/campaigns/featured` | Não | Destaques |
| GET | `/campaigns/:id` | Não | Detalhe |
| POST | `/purchases` | Não | Criar compra (gera PIX) |
| GET | `/purchases` | Sim | Minhas compras |
| GET | `/tickets` | Sim | Meus bilhetes |
| GET | `/tickets/phone/:phone` | Não | Bilhetes anônimos |
| GET | `/affiliate/profile` | Sim | Perfil afiliado |
| GET | `/affiliate/stats` | Sim | Estatísticas |
| POST | `/affiliate/withdraw` | Sim | Solicitar saque |
| GET | `/dashboard/stats` | Sim | Dashboard stats |
| GET | `/winners` | Não | Ganhadores |
| POST | `/payments/webhook/pix` | Não | Webhook PIX |

## Integração PIX

O gateway de PIX é abstraído via interface `PaymentGateway`. Em produção, troque `MockPaymentGateway` por uma implementação real:

```bash
app.pix.gateway=real-gateway-name
```

## Testes

```bash
./gradlew test
./gradlew jacocoTestReport
```

## Segurança

- BCrypt com 12 rounds para senhas
- JWT stateless com expiração configurável (padrão 24h)
- Spring Security com roles: `USER`, `AFFILIATE`, `ADMIN`
- CORS configurado para aceitar todas origens (configurável em produção)
