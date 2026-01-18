# Quick Start Guide

## Running the Application

### Option 1: Maven (Recommended)
```bash
# Build and run
./mvnw clean spring-boot:run
```

### Option 2: Pre-built JAR
```bash
# Build JAR
./mvnw clean package

# Run JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Option 3: Docker
```bash
# Build image
docker build -t demo-app .

# Run container
docker run -p 8080:8080 demo-app
```

## First Steps

1. **Open Browser**: Navigate to `http://localhost:8080`
   - You'll be redirected to the login page

2. **Create Account**: Click "Create one here"
   - Username: (choose any)
   - Email: (any valid email)
   - Password: (create a password)

3. **Login**: Use your credentials
   - You'll be taken to your dashboard

4. **Setup WebAuthn**: Click "🔧 Setup WebAuthn"
   - Enter a device name (e.g., "My MacBook")
   - Click "📱 Register Device"
   - Follow your browser's authentication prompt
   - Your device will be registered for passwordless auth

5. **Done!** 🎉
   - Your device is now registered for WebAuthn
   - You can remove the registered credential anytime
   - Register multiple devices for different machines

## Architecture Overview

```
┌─────────────────────────────────────────┐
│      Spring Boot Application             │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  Security Configuration         │   │
│  │  - Authentication Manager       │   │
│  │  - Password Encoder (BCrypt)    │   │
│  │  - HTTP Security                │   │
│  └─────────────────────────────────┘   │
│                  │                      │
│  ┌─────────────────────────────────┐   │
│  │  Auth Controller                │   │
│  │  - POST /register               │   │
│  │  - POST /login                  │   │
│  │  - POST /logout                 │   │
│  │  - POST /api/auth/register/*    │   │
│  │  - POST /api/auth/authenticate/*│   │
│  └─────────────────────────────────┘   │
│                  │                      │
│  ┌─────────────────────────────────┐   │
│  │  Services                       │   │
│  │  - WebAuthnService              │   │
│  │  - CustomUserDetailsService     │   │
│  └─────────────────────────────────┘   │
│                  │                      │
│  ┌─────────────────────────────────┐   │
│  │  Data Access                    │   │
│  │  - UserRepository               │   │
│  │  - WebAuthnCredentialRepository │   │
│  └─────────────────────────────────┘   │
│                  │                      │
│  ┌─────────────────────────────────┐   │
│  │  H2 Database                    │   │
│  │  - users table                  │   │
│  │  - webauthn_credentials table   │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
        ↓
    Frontend (Thymeleaf + Bootstrap 5)
    ├─ login.html
    ├─ register.html
    ├─ dashboard.html
    └─ setup-webauthn.html
```

## File Structure

```
src/main/java/com/example/demo/
├── DemoApplication.java              # Main entry point
├── HelloController.java              # Home page routing
├── config/
│   └── SecurityConfig.java          # Spring Security configuration
├── controller/
│   └── AuthController.java          # Authentication endpoints
├── entity/
│   ├── User.java                    # User entity
│   └── WebAuthnCredential.java      # WebAuthn credential entity
├── repository/
│   ├── UserRepository.java          # User data access
│   └── WebAuthnCredentialRepository.java  # Credential data access
├── service/
│   ├── WebAuthnService.java         # WebAuthn logic
│   └── CustomUserDetailsService.java # User details service
└── dto/
    ├── RegisterRequest.java         # Registration DTO
    └── WebAuthnRegistrationRequest.java # WebAuthn DTO

src/main/resources/
├── application.properties            # Spring configuration
├── templates/
│   ├── login.html                   # Login page
│   ├── register.html                # Registration page
│   ├── dashboard.html               # User dashboard
│   └── setup-webauthn.html          # WebAuthn setup page
└── static/                          # Static assets (CSS, JS, images)
```

## Default Ports

- **HTTP**: 8080
- **H2 Console**: 8080/h2-console
- **Configurable via**: `server.port` in `application.properties`

## Database Access

### H2 Console
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

### Tables Created Automatically
- `users` - User accounts
- `webauthn_credentials` - Registered WebAuthn devices

## Useful Resources

- [WebAuthn MDN Documentation](https://developer.mozilla.org/en-US/docs/Web/API/Web_Authentication_API)
- [FIDO Alliance](https://fidoalliance.org/)
- [Yubico WebAuthn Server](https://github.com/Yubico/java-webauthn-server)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Boot](https://spring.io/projects/spring-boot)

## Issues & Support

If you encounter any issues:

1. Check the console output for error messages
2. Check the browser's Developer Console (F12)
3. Verify Java 17+ is installed: `java -version`
4. Verify Maven 3.9.6+ is installed: `mvn -version`
5. Check H2 Console at `http://localhost:8080/h2-console` to inspect database

## Next Steps

After setup, consider:
- ✅ Test multiple device registrations
- ✅ Test logout and re-authentication
- ✅ Check browser compatibility for WebAuthn
- ✅ Deploy to Render, Heroku, or your favorite PaaS
- ✅ Switch to PostgreSQL for production
- ✅ Add HTTPS configuration
- ✅ Configure email verification
- ✅ Add remember-me functionality

Enjoy passwordless authentication! 🔐
