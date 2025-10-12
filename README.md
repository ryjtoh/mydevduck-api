# MyDevDuck API

Spring Boot 3.2.x REST API for MyDevDuck application.

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
