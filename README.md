# MyDevDuck API

A Spring Boot REST API for a virtual pet application with gamification features. Users can create and manage virtual pets, interact with them through feeding and playing, and track their progress through an XP and leveling system.

## Features

- **User Authentication**: JWT-based authentication with access and refresh tokens
- **Pet Management**: Create, read, update, and delete virtual pets (max 5 per user)
- **Pet Interactions**:
  - Feed pets to increase hunger levels and earn XP
  - Play with pets to increase happiness and earn XP
  - View detailed pet statistics including health status and time since last interaction
- **Leveling System**: Pets gain XP through interactions and level up (100 XP per level)
- **Pet Status Tracking**: Automatic status calculation (HEALTHY, HUNGRY, SAD, DYING)
- **Brute Force Protection**: Account lockout after failed login attempts

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
- Redis 6+ (optional, using simple cache by default)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/mydevduck.git
cd mydevduck
```

### 2. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE mydevduck_dev;
```

### 3. Environment Configuration

Copy the example environment file and fill in your values:
```bash
cp .env.dev.example .env.dev
```

Edit `.env.dev` and set your configuration:
- `DB_USERNAME`, `DB_PASSWORD`, `DB_NAME`: Your PostgreSQL credentials
- `JWT_SECRET`: Generate with `openssl rand -base64 32` (must be at least 32 characters)

### 4. Load Environment Variables

```bash
# On Linux/Mac
export $(cat .env.dev | xargs)

# On Windows (PowerShell)
Get-Content .env.dev | ForEach-Object { $var = $_.Split('='); [Environment]::SetEnvironmentVariable($var[0], $var[1]) }
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Health Check
```
GET /api/v1/health
```

### Authentication
```
POST /api/v1/auth/register    # Register new user
POST /api/v1/auth/login       # Login
POST /api/v1/auth/refresh     # Refresh access token
GET  /api/v1/auth/me          # Get current user info
```

### Pet Management
```
POST   /api/v1/pets/create    # Create new pet
GET    /api/v1/pets/{id}      # Get pet by ID
PUT    /api/v1/pets/{id}      # Update pet name
DELETE /api/v1/pets/{id}      # Delete pet
```

### Pet Interactions
```
POST /api/v1/pets/{id}/feed   # Feed pet (+20 hunger, +5 XP)
POST /api/v1/pets/{id}/play   # Play with pet (+15 happiness, +3 XP)
GET  /api/v1/pets/{id}/stats  # Get detailed pet statistics
```

## Production Deployment

```bash
mvn clean package
java -jar -Dspring.profiles.active=prod target/mydevduck-api-0.0.1-SNAPSHOT.jar
```

Make sure to set production environment variables and use a secure JWT secret.

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

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Future Enhancements

See [TODO.md](TODO.md) for planned features and improvements.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
