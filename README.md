# Let's Play Backend

Spring Boot REST API for user authentication and product management. It uses MongoDB for persistence and stateless JWT authentication.

## Requirements

- Java 21
- Docker and Docker Compose for MongoDB
- Maven is optional; the repository includes the Maven Wrapper

## Configuration

Copy `.env.example` to `.env`, replace every placeholder, and export the variables before starting the application. Spring Boot does not automatically load `.env` files. For local development, `application.properties` includes defaults matching the bundled Compose setup; environment variables take precedence.

```bash
set -a
. ./.env
set +a
```

Required variables:

- `MONGO_INITDB_ROOT_USERNAME` and `MONGO_INITDB_ROOT_PASSWORD`: MongoDB root credentials
- `MONGODB_URI`: application connection string, including `authSource=admin`
- `JWT_SECRET`: base64-encoded HMAC secret of at least 256 bits
- `JWT_EXPIRATION`: token lifetime in milliseconds
- `SSL_KEY_STORE_PASSWORD`: password for `src/main/resources/keystore.p12`

Never commit real credentials, JWT keys, or keystore passwords. Rotate any credentials that have previously been committed.

## Run Locally

Start MongoDB:

```bash
docker compose up -d mongodb
```

Load the environment variables, then start the API:

```bash
./mvnw spring-boot:run
```

The API listens on `https://localhost:8443`. The bundled keystore is intended for local development; use a managed certificate or deployment-specific keystore in production.

Stop MongoDB with:

```bash
docker compose down
```

## API Overview

All endpoints are under `/api/v1`.

### Authentication

`POST /auth/register` creates a normal user and returns a JWT. The role is assigned server-side and cannot be supplied by the client.

```json
{
  "name": "Alex",
  "email": "alex@example.com",
  "password": "correct-horse-battery-staple"
}
```

`POST /auth/login` accepts the same email and password and returns a JWT.

Send the token on protected requests:

```text
Authorization: Bearer <token>
```

### Products

- `GET /products` and `GET /products/{id}` are public.
- `POST /products` requires authentication.
- `PUT /products/{id}` and `DELETE /products/{id}` require authentication and are restricted to the product owner or an administrator.

Product creation requires `name`, `description`, and a positive `price`. Product updates accept any subset of `name`, `description`, `price`, and `category`.

### Users

- `GET /users` requires `ROLE_ADMIN`.

## Testing

Tests use Testcontainers and require Docker:

```bash
./mvnw test
```

Test configuration uses an isolated MongoDB connection and test-only JWT values. It does not use production secrets.

## API Documentation

When the application is running:

- Swagger UI: `https://localhost:8443/swagger-ui/index.html`
- OpenAPI JSON: `https://localhost:8443/v3/api-docs`
