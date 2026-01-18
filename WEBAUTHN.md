# WebAuthn Authentication Setup - User Guide

This Spring Boot application now includes **WebAuthn (Web Authentication) support** for passwordless, background authentication similar to SSH keys.

## How It Works

### Initial Setup (First Time)
1. **Register Account**: Create a new account with username, email, and password
2. **Login**: Use your credentials to log in normally
3. **Setup WebAuthn**: On your dashboard, click "Setup WebAuthn" to register your device
4. **Authenticate Device**: Follow your browser's prompts to authenticate using:
   - Biometrics (fingerprint, face recognition)
   - Security key (hardware authentication device)
   - Platform authenticator (Windows Hello, Touch ID, etc.)

### Subsequent Logins (After WebAuthn Setup)
Once WebAuthn is configured, you can:
- **Option 1**: Use your device automatically (background authentication) - similar to SSH keys
- **Option 2**: Log in with username/password as usual
- The system remembers your device and authenticates transparently

## Architecture

### Components

**Database Entities:**
- `User`: Stores user account information with WebAuthn status
- `WebAuthnCredential`: Stores registered WebAuthn credentials per user

**Backend Services:**
- `WebAuthnService`: Handles registration and authentication flows
- `AuthController`: REST endpoints for auth operations
- `SecurityConfig`: Spring Security configuration

**Frontend Pages:**
- `/login` - Login form
- `/register` - Registration form
- `/dashboard` - User dashboard with account info
- `/setup-webauthn` - WebAuthn registration interface

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.9.6+

### Build
```bash
./mvnw clean package
```

### Run
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Points
- **Home**: `http://localhost:8080/` → redirects to login or dashboard
- **Register**: `http://localhost:8080/register`
- **Login**: `http://localhost:8080/login`
- **Dashboard**: `http://localhost:8080/dashboard` (authenticated only)
- **H2 Console**: `http://localhost:8080/h2-console` (for database inspection)

## API Endpoints

### Authentication
- `POST /api/auth/register/begin` - Start WebAuthn registration
- `POST /api/auth/register/finish` - Complete WebAuthn registration
- `POST /api/auth/authenticate/begin` - Start WebAuthn authentication
- `POST /api/auth/authenticate/finish` - Complete WebAuthn authentication
- `POST /api/auth/credential/remove` - Remove a registered credential

## Key Features

✅ **Traditional Auth**: Username/password login still available  
✅ **WebAuthn Support**: FIDO2/W3C WebAuthn standard  
✅ **Biometric Auth**: Fingerprint, face recognition, etc.  
✅ **Security Keys**: Support for hardware security keys  
✅ **Multiple Devices**: Register multiple credentials per user  
✅ **Background Authentication**: Similar to SSH key behavior  
✅ **Session Management**: Secure session handling  
✅ **Bootstrap UI**: Modern, responsive design  

## Security Notes

- Passwords are hashed with BCrypt
- CSRF protection enabled
- Session-based authentication
- Database-backed user storage (H2 for development)
- All credentials are encrypted and securely stored

## Dependencies

- **Spring Boot 3.5.10**: Web framework
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: Database access
- **Yubico WebAuthn Server**: WebAuthn protocol implementation
- **H2 Database**: In-memory database (development)
- **Thymeleaf**: Template engine
- **Bootstrap 5**: UI framework
- **Gson**: JSON processing

## Docker Support

The project includes a Dockerfile for containerization:

```bash
docker build -t demo-app .
docker run -p 8080:8080 demo-app
```

## Testing the WebAuthn Flow

1. **Create Account**:
   - Go to `http://localhost:8080/register`
   - Enter username, email, and password
   - Click "Create Account"

2. **Login**:
   - Go to `http://localhost:8080/login`
   - Use your credentials
   - Click "Sign In"

3. **Setup WebAuthn**:
   - Go to `/dashboard`
   - Click "Setup WebAuthn"
   - Enter a device name (e.g., "My MacBook")
   - Click "Register Device"
   - Authenticate using your device's biometric or security key

4. **Verify Registration**:
   - Your credential should appear in the "Your Credentials" section
   - You can register multiple credentials

5. **Test Background Auth** (Future):
   - After setting up WebAuthn, subsequent authentications can occur transparently
   - Similar to how SSH keys work without prompting for passwords

## Troubleshooting

**WebAuthn not working?**
- Check browser compatibility (Chrome, Firefox, Safari, Edge all support it)
- Ensure HTTPS is used (or localhost for development)
- Check browser console for error messages

**H2 Console**:
- Access at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave blank)

## Future Enhancements

- Persistent database (PostgreSQL)
- Email verification
- Two-factor authentication
- Remember device feature
- Credential backup codes
- Admin panel
- User roles and permissions

---

Built with ❤️ using Spring Boot and WebAuthn
