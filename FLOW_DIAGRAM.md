# User Authentication Flow Diagram

## Registration Flow

```
┌─────────────┐
│ New User    │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│ Visit /register     │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ Fill Registration Form          │
│ - Username                      │
│ - Email                         │
│ - Password                      │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ POST /register                  │
│ - Validate input                │
│ - Check username/email taken    │
│ - Hash password (BCrypt)        │
│ - Save to database              │
└──────┬──────────────────────────┘
       │
       ▼
   ✅ Success
       │
       ▼
┌─────────────────────┐
│ Redirect to login   │
└─────────────────────┘
```

## Traditional Login Flow

```
┌─────────────┐
│ Registered  │
│ User        │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│ Visit /login        │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ Enter Credentials               │
│ - Username                      │
│ - Password                      │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ POST /login                     │
│ (Spring Security Form Login)    │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│ DaoAuthenticationProvider       │
│ - Load user by username         │
│ - Compare password with hash    │
└──────┬──────────────────────────┘
       │
       ▼
   ✅ Match?  ❌ No
       │        │
       │        ▼
       │    Error: Invalid credentials
       │        │
       │        ▼
       │    Redirect to login
       │
       ▼
┌─────────────────────────────────┐
│ Create Session                  │
│ - Generate session ID           │
│ - Store in cookie (HTTPOnly)    │
│ - Authenticate SecurityContext  │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Redirect to /       │
│ (dashboard)         │
└─────────────────────┘
```

## WebAuthn Registration Flow

```
┌──────────────────┐
│ Logged-In User   │
└────────┬─────────┘
         │
         ▼
┌───────────────────────────────┐
│ Visit /setup-webauthn         │
└────────┬────────────────────┬─┘
         │                    │
         ▼                    ▼
    Show Form         (Optional: List existing)
         │
         ▼
┌──────────────────────────────┐
│ User enters device name      │
│ (e.g., "My MacBook")         │
└────────┬─────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│ Click: "📱 Register Device"                  │
│                                              │
│ → POST /api/auth/register/begin              │
│   - Generate challenge (32 bytes)            │
│   - Create PublicKeyCredentialCreationOptions│
│   - Return to frontend                       │
└────────┬─────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│ Browser: navigator.credentials.create()      │
│                                              │
│ User sees platform-specific prompt:          │
│ - Windows Hello                              │
│ - Touch ID                                   │
│ - Fingerprint Reader                         │
│ - Security Key                               │
└────────┬─────────────────────────────────────┘
         │
         ▼
   ✅ User Authenticates
         │
         ▼
┌──────────────────────────────────────────────┐
│ Browser returns AttestationResponse:         │
│ - Credential ID                              │
│ - Public Key                                 │
│ - Attestation Object                         │
│ - Client Data JSON                           │
└────────┬─────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│ POST /api/auth/register/finish               │
│ - Credential ID                              │
│ - Public Key (base64)                        │
│ - Device Name                                │
└────────┬─────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────┐
│ WebAuthnService.finishRegistration()         │
│                                              │
│ - Create WebAuthnCredential entity           │
│ - Save to database                           │
│ - Enable WebAuthn on user                    │
│ - Set sign_count = 0                         │
└────────┬─────────────────────────────────────┘
         │
         ▼
   ✅ Success
         │
         ▼
┌────────────────────────────────┐
│ Redirect to /dashboard         │
│                                │
│ ✓ Device appears in           │
│   "Your Credentials" section   │
└────────────────────────────────┘
```

## WebAuthn Authentication Flow (Future)

```
┌─────────────────┐
│ User visits app │
└────────┬────────┘
         │
         ▼
┌────────────────────────┐
│ Browser checks for     │
│ stored credentials     │
└────────┬───────────────┘
         │
         ▼
  ✅ Found credential?
    │
    ├─ Yes ─────────────────────────────┐
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ Start background auth:     │
    │                        │ POST /api/auth/auth/begin  │
    │                        │ - Generate challenge       │
    │                        │ - Send credential options  │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ Browser auto-triggers      │
    │                        │ user verification          │
    │                        │ (similar to SSH key)       │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ User authenticates         │
    │                        │ (Fingerprint/Face/Key)     │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ POST /api/auth/auth/finish │
    │                        │ - Assertion response       │
    │                        │ - Verified signature       │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ Server verifies assertion  │
    │                        │ - Check signature          │
    │                        │ - Increment sign_count     │
    │                        │ - Validate counter         │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ Create session             │
    │                        │ (NO password needed!)      │
    │                        └──────────┬─────────────────┘
    │                                    │
    │                                    ▼
    │                        ┌────────────────────────────┐
    │                        │ ✅ Authenticated!          │
    │                        │ User on dashboard          │
    │                        └────────────────────────────┘
    │
    └─ No ──────────────────────────────┐
                                        │
                                        ▼
                         ┌──────────────────────────┐
                         │ Show login page          │
                         │ Traditional auth option  │
                         └──────────────────────────┘
```

## Database State Transitions

### User Creation
```
BEFORE:
users table: (empty)

AFTER register:
users table:
  id  | username | password_hash      | email            | webauthn_enabled
  1   | john     | $2a$10$xxx...     | john@example.com | false

webauthn_credentials: (empty)
```

### WebAuthn Registration
```
BEFORE:
users: {id: 1, webauthn_enabled: false}
webauthn_credentials: (empty)

AFTER setup-webauthn:
users: {id: 1, webauthn_enabled: true}
webauthn_credentials:
  id  | user_id | credential_id | public_key | sign_count | created_at | credential_name
  1   | 1       | "abc123..."   | "xyz..."   | 0          | 1673000000 | "My MacBook"
  2   | 1       | "def456..."   | "uvw..."   | 0          | 1673001000 | "Work iPhone"
```

### WebAuthn Authentication
```
Each successful authentication:
- sign_count increments
- last_used timestamp updates
- Prevents replay attacks

webauthn_credentials:
  id | sign_count | updated_at
  1  | 1          | 1673100000
  1  | 2          | 1673101000
  1  | 3          | 1673102000
```

## Session Management

```
User Login → Authentication Success → Session Created

Session contains:
├─ session_id: "f47ac10b58cc4372a5670e4cn5d87ab1"
├─ user_id: 1
├─ username: "john"
├─ authenticated: true
├─ created_at: 1673000000
├─ last_accessed: 1673100000
└─ expires_at: 1673103600

Cookie sent to browser:
SESSIONID=f47ac10b58cc4372a5670e4cn5d87ab1; Path=/; HttpOnly; Secure; SameSite=Strict
```

## Security Measures in Flow

```
Registration:
  Password → BCrypt Hashing → Hash Storage
  (Plaintext never stored in database)

Login:
  Input Password + Stored Hash → BCrypt Compare
  (Secure comparison prevents timing attacks)

WebAuthn:
  Challenge → User Device → Cryptographic Signature
  → Server Verification (prevents replay/tampering)

Session:
  Random ID → HTTPOnly Cookie → Server-side session store
  (Protects against XSS and CSRF)
```

## User States

```
┌──────────────┐
│ Anonymous    │
│ (Not Logged) │
└──────┬───────┘
       │
       ├─ Can access: /login, /register, /
       │
       ▼
┌──────────────────────┐
│ Authenticated        │
│ (Logged In)          │
└────────┬─────────────┘
         │
         ├─ Can access: /dashboard, /setup-webauthn, /logout
         │
         ├─ WebAuthn NOT enabled yet
         │ └─ Can only log in with password
         │
         └─ WebAuthn enabled
            ├─ Can log in with password (backup)
            └─ Can log in with biometric/key (preferred)
```

This architecture provides:
- 🔒 Secure password handling
- 🔐 Passwordless authentication
- 🛡️ Replay attack prevention
- ⚡ Background authentication
- 📱 Multi-device support
- 🔄 Graceful fallbacks
