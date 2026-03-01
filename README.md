# Second Hand UZ

A second-hand marketplace for Uzbekistan. Sellers list items; buyers browse, purchase, and pay via ATMOS. After delivery, buyers leave reviews.

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Backend     | Java 21, Spring Boot 3.4.3, Maven   |
| Database    | PostgreSQL 17, Liquibase migrations |
| Auth        | JWT (jjwt 0.12.6), Spring Security  |
| Payment     | ATMOS (Uzbekistan)                  |
| API docs    | SpringDoc OpenAPI 3                 |
| Frontend    | React 18, Vite, React Query, Axios  |
| Docker      | Multi-stage Dockerfile, Compose     |

## Prerequisites

- Java 21+
- Maven 3.9+ (or use `./mvnw`)
- PostgreSQL 17 (or Docker)
- Node.js 20+

## Quick Start (Docker)

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET, ATMOS_* credentials

docker compose up -d
# Backend:  http://localhost:8080
# Swagger:  http://localhost:8080/swagger-ui.html
```

## Local Development

### Backend

```bash
# Start PostgreSQL
docker compose up postgres -d

# Copy and configure env
cp .env.example .env.local
# Set env vars (export or .env tooling)

./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# http://localhost:5173
```

## Maven Commands

```bash
./mvnw clean package               # Build JAR
./mvnw test                        # Run all tests
./mvnw verify                      # Tests + static analysis
./mvnw spotbugs:check              # SpotBugs
./mvnw pmd:check                   # PMD
./mvnw checkstyle:check            # Checkstyle
./mvnw spring-boot:run             # Run in default profile
```

## API Overview

Base URL: `http://localhost:8080/api/v1`

| Method | Endpoint                     | Auth | Description             |
|--------|------------------------------|------|-------------------------|
| POST   | /auth/register               | —    | Register user           |
| POST   | /auth/login                  | —    | Login → JWT             |
| GET    | /users/me                    | JWT  | Own profile             |
| GET    | /users/{id}                  | —    | Public profile          |
| GET    | /listings                    | —    | Browse / search         |
| GET    | /listings/{id}               | —    | Listing detail          |
| POST   | /listings                    | JWT  | Create listing          |
| PUT    | /listings/{id}               | JWT  | Update listing          |
| DELETE | /listings/{id}               | JWT  | Delete listing          |
| POST   | /listings/{id}/images        | JWT  | Upload image            |
| POST   | /orders                      | JWT  | Create order            |
| GET    | /orders                      | JWT  | My orders               |
| GET    | /orders/{id}                 | JWT  | Order detail            |
| POST   | /orders/{id}/cancel          | JWT  | Cancel order            |
| POST   | /payments/session/{orderId}  | JWT  | Create ATMOS session    |
| POST   | /payments/webhook            | —    | ATMOS webhook           |
| POST   | /reviews                     | JWT  | Leave review            |
| GET    | /reviews/seller/{sellerId}   | —    | Seller reviews          |

Full interactive docs: `http://localhost:8080/swagger-ui.html`

## Environment Variables

See `.env.example` for all required variables.

| Variable              | Description                          |
|-----------------------|--------------------------------------|
| DB_URL                | JDBC URL for PostgreSQL              |
| DB_USERNAME           | Database username                    |
| DB_PASSWORD           | Database password                    |
| JWT_SECRET            | ≥32-char secret for signing JWTs    |
| JWT_EXPIRATION_MS     | Token TTL in milliseconds            |
| ATMOS_STORE_ID        | ATMOS partner store ID               |
| ATMOS_CONSUMER_KEY    | ATMOS OAuth consumer key             |
| ATMOS_CONSUMER_SECRET | ATMOS OAuth consumer secret          |
| ATMOS_WEBHOOK_SECRET  | ATMOS webhook HMAC-SHA256 secret     |
| CORS_ALLOWED_ORIGINS  | Comma-separated allowed origins      |
| UPLOAD_PATH           | Directory for uploaded images        |

## Project Structure

```
second-hand-uz/
├── src/main/java/.../
│   ├── common/          # Security, exceptions, logging, config
│   ├── user/            # Auth + user profiles
│   ├── listing/         # Listing CRUD + image upload
│   ├── order/           # Order lifecycle
│   ├── payment/         # ATMOS integration
│   └── review/          # Buyer reviews
├── src/main/resources/
│   ├── application.yml  # Base config
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/changelog/    # Liquibase migrations
├── src/test/            # Unit + integration tests
├── frontend/            # React + Vite SPA
├── config/              # Checkstyle + PMD rules
├── Dockerfile
└── docker-compose.yml
```

## Architecture

Hexagonal (Ports & Adapters):

- **domain**: entities, value objects, enums
- **application/port/in**: use-case interfaces (driving ports)
- **application/port/out**: repository interfaces (driven ports)
- **application/service**: use-case implementations
- **infrastructure/persistence**: JPA repositories (driven adapters)
- **web**: REST controllers, DTOs (driving adapters)
