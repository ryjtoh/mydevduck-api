# MyDevDuck API - TODOs

This file tracks future enhancements and technical debt for the MyDevDuck API.

## High Priority

### Integration Tests for AuthService (STRONGLY RECOMMENDED)
- Test successful registration, login, token refresh
- Test duplicate email registration (409 error)
- Test invalid credentials (401 error)
- Test expired/invalid refresh tokens
- Test brute force protection lockout
- Use `@SpringBootTest`, `TestRestTemplate`, and test database (H2)
- Create test fixtures for User entities

### Refresh Token Database Storage (RECOMMENDED)
- Create `RefreshToken` entity with: id, userId, token, expiresAt, createdAt
- Save refresh token to database on login
- On logout: delete refresh token from database
- On refresh: verify token exists in database before issuing new access token
- Add scheduled job to clean up expired tokens
- Enables token revocation for security

## Medium Priority

### Add Swagger/OpenAPI Documentation (RECOMMENDED)
- Add `@Tag(name = "Authentication", description = "Authentication endpoints")` to controllers
- Add `@Operation`, `@ApiResponse` annotations to each endpoint
- Document request/response schemas with examples
- Configure Swagger UI in application.yml

### Repository Tests
- Add unit tests for repository layer

### Database Migrations
- Implement Flyway or Liquibase for production database migrations
- Set `ddl-auto: validate` in production profile

## Low Priority

### Custom Exception Handling (OPTIONAL)
- Use the existing `EmailAlreadyExistsException` in AuthService
- Add `@ControllerAdvice` to handle exceptions globally
- Replace `ResponseStatusException` with custom exceptions throughout codebase

### Add validation annotations in controllers
- Ensure all `@RequestBody` parameters use `@Valid`
- Add appropriate constraint annotations to DTOs

## Security Enhancements

### Rate Limiting
- Add rate limiting to registration endpoint
- Implement IP-based rate limiting for public endpoints

### CORS Configuration
- Add proper CORS configuration when frontend is developed
- Define allowed origins explicitly (never use `*` in production)

### Password Validation
- Add password complexity requirements (uppercase, lowercase, number, special char)
- Consider using Passay library for password validation

## Code Quality

### Add @Transactional Annotations
- Add `@Transactional` to `createPet()`, `updatePet()`, `deletePet()` in PetService
- Ensures atomicity of database operations

### Standardize Authentication Patterns
- Consider using `@AuthenticationPrincipal` instead of manual token extraction
- Standardize token handling across all controllers

### Extract Magic Numbers to Constants
- Extract hardcoded values (max pets: 5, hunger thresholds: 90, 50, 20, etc.)
- Move to configuration properties or constants

## Future Features

### Pet Interaction Enhancements
- Add interaction counters to Pet entity
- Create activity log for each interaction
- Add more interaction types beyond feed/play

### Monitoring & Observability
- Add comprehensive logging
- Implement application monitoring
- Add metrics collection

## Documentation

### API Documentation
- Complete OpenAPI/Swagger documentation
- Add request/response examples
- Document error codes and responses

### Setup Documentation
- Improve setup instructions in README
- Add troubleshooting guide
- Document deployment process
