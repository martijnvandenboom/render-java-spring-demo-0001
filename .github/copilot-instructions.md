# AI Coding Agent Instructions

## Project Overview

**WebAuthn Authentication System** - A Spring Boot application with integrated FIDO2/WebAuthn passwordless authentication. Users register devices once for biometric login via cryptographic verification. Production-ready with H2 in-memory DB and Thymeleaf templating.

## Architecture & Key Components

### Three-Tier Structure
- **Frontend**: Thymeleaf HTML templates + Bootstrap 5 (under `src/main/resources/templates/` and `static/`)
- **Application Layer**: Spring Boot controllers, services, and security config (under `src/main/java/com/example/demo/`)
- **Data Layer**: H2 in-memory database with JPA entities (`User`, `Post`, `WebAuthnCredential`)

### Core Components
- **`AuthController`** - Handles registration, login, WebAuthn enrollment (`/register`, `/login`, `/api/auth/*`)
- **`WebAuthnService`** - Manages cryptographic registration/authentication flows using Yubico WebAuthn library
- **`SecurityConfig`** - Spring Security configuration: BCrypt password encoding, route authorization, form login
- **`UserRepository`, `WebAuthnCredentialRepository`** - JPA data access for users and credentials
- **`CustomUserDetailsService`** - Spring Security integration for user lookup

### Data Flow: User Registration → Login → WebAuthn Setup
1. User registers with username/password → saved with BCrypt hashing
2. Login via traditional auth → session created
3. User opts into WebAuthn → cryptographic challenge issued
4. Device responds with attestation → credential stored in DB
5. Next login: challenge → device verifies → automatic auth (no password needed)

## Build & Deployment Workflow

### Development Build & Run
```bash
# Clean build + skip tests (fastest iteration)
./mvnw clean package -DskipTests

# Run locally
./mvnw spring-boot:run                    # or
java -jar target/demo-0.0.1-SNAPSHOT.jar
```
- **Server starts on**: `http://localhost:8080`
- **H2 console**: `http://localhost:8080/h2-console` (user: `sa`, no password)
- **Java version**: 17+ required

### Key Build Config
- Maven 3.9.6+ (included via `./mvnw` wrapper)
- Spring Boot 3.5.10-SNAPSHOT with Spring Security 6.x
- H2 in-memory DB (auto-resets per session; use `spring.jpa.hibernate.ddl-auto=update`)

## Critical Patterns & Conventions

### Security Patterns
1. **Password Encoding**: Always use `@Autowired PasswordEncoder passwordEncoder` (BCrypt) when handling user passwords
2. **Authentication Context**: Use `SecurityContextHolder.getContext().setAuthentication()` to manually set auth after registration
3. **Route Authorization**: `SecurityConfig.securityFilterChain()` defines which paths require authentication vs public access
   - Public: `/register`, `/login`, `/api/**`, `/css/**`, `/blog/**`
   - Protected: Everything else requires authentication

### Service & Repository Layer
- **`@Service`** classes (WebAuthnService, BlogService) handle business logic
- **`@Repository`** interfaces (UserRepository, WebAuthnCredentialRepository) extend `JpaRepository<Entity, ID>`
- Use `@Autowired` for dependency injection; follow Spring 6.x best practices (avoid circular dependencies)

### WebAuthn Integration (Yubico Library)
- **Registration**: `WebAuthnService.startRegistration()` creates challenge; client sends attestation → `finishRegistration()` stores credential
- **Authentication**: Challenge-response flow stored in session; credential lookup via `WebAuthnCredentialRepository.findByUser()`
- **RP ID Configuration**: Set in `WebAuthnService.rpId = "localhost"` (change to domain for production)

### Controller & Template Patterns
- **Controller Response**: Mix of Thymeleaf template returns (Model + view name) and `@ResponseEntity` JSON for API endpoints (`/api/**`)
- **Session Management**: Store challenges/data in `HttpSession` during WebAuthn flow (e.g., challenge storage in `AuthController`)
- **Frontend-Backend Communication**: AJAX calls to `/api/auth/*` endpoints return JSON; form submissions go to `/register`, `/login`

## File Organization & Key Locations

| Component | Path |
|-----------|------|
| Entities | `src/main/java/com/example/demo/entity/{User.java, WebAuthnCredential.java, Post.java}` |
| Controllers | `src/main/java/com/example/demo/controller/{AuthController.java, BlogController.java}` |
| Services | `src/main/java/com/example/demo/service/{WebAuthnService.java, CustomUserDetailsService.java}` |
| Repositories | `src/main/java/com/example/demo/repository/{UserRepository.java, WebAuthnCredentialRepository.java}` |
| Security Config | `src/main/java/com/example/demo/config/SecurityConfig.java` |
| Templates | `src/main/resources/templates/{login.html, register.html, dashboard.html, setup-webauthn.html}` |
| Static Assets | `src/main/resources/static/{css/styles.css, js/scripts.js}` |
| Config | `src/main/resources/application.properties` |

## External Dependencies & Integration Points

- **Spring Security 6.x** - Authentication, authorization, password encoding
- **Yubico WebAuthn 2.5.0** - WebAuthn protocol implementation (challenge generation, attestation verification)
- **Gson** - JSON serialization for WebAuthn data exchange
- **H2 Database** - In-memory relational DB; console at `/h2-console`
- **Thymeleaf** - Server-side template rendering with Spring integration

## Testing & Debugging Commands

```bash
# Run tests
./mvnw test

# Check for errors
./mvnw clean package -DskipTests -X   # verbose/debug output

# Database inspection
# Navigate to http://localhost:8080/h2-console and execute:
# SELECT * FROM USERS;
# SELECT * FROM WEBAUTHN_CREDENTIALS;
```

## Common Tasks & Implementation Patterns

### Adding a New Endpoint
1. Create method in controller (e.g., `AuthController`) with `@GetMapping` or `@PostMapping`
2. Return Thymeleaf template (`return "template-name"`) or `ResponseEntity` (for API)
3. Update `SecurityConfig.securityFilterChain()` if new public route needed
4. Add template in `src/main/resources/templates/` if rendering view

### Modifying User Model
1. Update `User.java` entity with new fields and JPA annotations
2. `spring.jpa.hibernate.ddl-auto=update` auto-creates/alters table schema
3. Update `UserRepository` if new query methods needed
4. Update `CustomUserDetailsService` if affecting auth flow

### WebAuthn Feature Changes
1. Modify challenge logic in `WebAuthnService.startRegistration()` (ES256 or RS256 algorithms)
2. Update credential storage in `finishRegistration()` if new fields needed
3. Update `setup-webauthn.html` frontend for new UI/UX
4. Test with real WebAuthn device; test endpoints via AJAX in browser console

## Tips for Productivity

- **Database Reset**: H2 is in-memory; restarts clear all data—useful for testing but not persistence
- **Session Debugging**: Use browser DevTools → Storage → Cookies/Session to inspect session ID
- **CSRF Disabled**: `csrf(csrf -> csrf.disable())` in `SecurityConfig` for API convenience (not recommended for production)
- **Thymeleaf Hot Reload**: `spring.thymeleaf.cache=false` enables live template changes without rebuild
- **Port Configuration**: Change `server.port=${PORT:8080}` in `application.properties` (defaults to 8080)
