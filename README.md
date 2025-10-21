# MyDevDuck API

Spring Boot 3.2.x REST API for MyDevDuck application.

## Future Enhancements / TODOs

### High Priority
- **Integration Tests for AuthService** (STRONGLY RECOMMENDED)
  - Test successful registration, login, token refresh
  - Test duplicate email registration (409 error)
  - Test invalid credentials (401 error)
  - Test expired/invalid refresh tokens
  - Test brute force protection lockout
  - Use `@SpringBootTest`, `TestRestTemplate`, and test database (H2)
  - Create test fixtures for User entities

- **Refresh Token Database Storage** (RECOMMENDED)
  - Create `RefreshToken` entity with: id, userId, token, expiresAt, createdAt
  - Save refresh token to database on login
  - On logout: delete refresh token from database
  - On refresh: verify token exists in database before issuing new access token
  - Add scheduled job to clean up expired tokens
  - Enables token revocation for security

### Medium Priority
- **Add Swagger/OpenAPI Documentation** (RECOMMENDED)
  - Add `@Tag(name = "Authentication", description = "Authentication endpoints")` to controllers
  - Add `@Operation`, `@ApiResponse` annotations to each endpoint
  - Document request/response schemas with examples
  - Configure Swagger UI in application.yml

- **Repository Tests**
  - Add unit tests for repository layer

### Low Priority
- **Custom Exception Handling** (OPTIONAL)
  - Use the existing `EmailAlreadyExistsException` in AuthService
  - Add `@ControllerAdvice` to handle exceptions globally
  - Replace `ResponseStatusException` with custom exceptions throughout codebase

- **Add validation annotations in controllers**
  - Ensure all `@RequestBody` parameters use `@Valid`
  - Add appropriate constraint annotations to DTOs

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.5
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security + JWT
- **Build Tool**: Maven

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 14+
- Redis 6+

## Configuration

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE mydevduck_dev;
```

2. Update `src/main/resources/application.yml` with your database credentials:
```yaml
spring:
  datasource:
    username: your_username
    password: your_password
```

### Environment Variables

Set the following environment variables:

- `DB_USERNAME`: Database username (default: postgres)
- `DB_PASSWORD`: Database password (default: postgres)
- `REDIS_PASSWORD`: Redis password (if required)
- `JWT_SECRET`: JWT secret key (must be at least 256 bits)

### JWT Secret

Generate a secure JWT secret:
```bash
openssl rand -base64 32
```

Set it as an environment variable or in application.yml.

## Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

### Production Mode

```bash
mvn clean package
java -jar -Dspring.profiles.active=prod target/mydevduck-api-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Health Check

```
GET /api/health
```

### Authentication

```
POST /api/auth/login
POST /api/auth/register
```

## Project Structure

```
com.mydevduck
├── controller       # REST endpoints
├── service          # Business logic
├── repository       # Data access layer
├── model            # Entity classes
├── dto              # Request/Response objects
├── config           # Configuration classes
├── exception        # Custom exceptions and handlers
├── security         # Authentication and authorization
└── util             # Helper utilities
```

## Development

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow Spring Boot best practices
- Write unit tests for services
- Use validation annotations on DTOs

### Database Migrations

Consider using Flyway or Liquibase for production database migrations (set `ddl-auto: validate`).

## License

Proprietary
