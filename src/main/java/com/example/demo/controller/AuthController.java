package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.WebAuthnRegistrationRequest;
import com.example.demo.dto.WebAuthnRegistrationResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.WebAuthnCredential;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WebAuthnCredentialRepository;
import com.example.demo.service.WebAuthnService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebAuthnCredentialRepository webAuthnCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private WebAuthnService webAuthnService;

    private final Gson gson = new Gson();

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        if (userRepository.existsByUsername(request.getUsername())) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail()
        );
        userRepository.save(user);

        model.addAttribute("success", "Registration successful! Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        // Always fetch fresh user data from database
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("webauthnEnabled", user.isWebauthnEnabled());
        
        // Fetch fresh credentials from database for this user
        List<WebAuthnCredential> creds = webAuthnCredentialRepository.findByUser(user);
        if (creds != null) {
            model.addAttribute("credentials", creds);
            model.addAttribute("credentialCount", creds.size());
        } else {
            model.addAttribute("credentials", new ArrayList<>());
            model.addAttribute("credentialCount", 0);
        }

        return "dashboard";
    }

    @GetMapping("/setup-webauthn")
    public String setupWebAuthn(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("webauthnEnabled", user.isWebauthnEnabled());
        }

        return "setup-webauthn";
    }

    @PostMapping("/api/auth/register/begin")
    @ResponseBody
    public String beginRegistration(Authentication authentication) throws Exception {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return gson.toJson(new ErrorResponse("User not found"));
        }

        WebAuthnService.RegistrationRequest request = webAuthnService.startRegistration(user);
        WebAuthnRegistrationResponse response = new WebAuthnRegistrationResponse(request.challenge, request.options);
        
        String jsonResponse = gson.toJson(response);
        System.out.println("\n===== REGISTRATION BEGIN OPTIONS =====");
        System.out.println(jsonResponse);
        System.out.println("=======================================\n");
        
        return jsonResponse;
    }

    @PostMapping("/api/auth/register/finish")
    @ResponseBody
    public String finishRegistration(
            Authentication authentication,
            @RequestBody WebAuthnRegistrationRequest request
    ) {
        String username = authentication.getName();
        System.out.println("\n========== REGISTRATION FINISH ==========");
        System.out.println("Username: " + username);
        
        // Log frontend debug info
        System.out.println("\n=== FRONTEND DEBUG INFO (attestationResponse.id analysis) ===");
        System.out.println("debugRawValue: " + request.getDebugRawValue());
        System.out.println("debugType: " + request.getDebugType());
        System.out.println("debugConstructorName: " + request.getDebugConstructorName());
        System.out.println("debugIsArrayBuffer: " + request.getDebugIsArrayBuffer());
        System.out.println("debugByteLength: " + request.getDebugByteLength());
        System.out.println("debugToString: " + request.getDebugToString());
        System.out.println("debugAttestationResponseId: " + request.getDebugAttestationResponseId());
        System.out.println("debugBase64UrlId: '" + request.getDebugBase64UrlId() + "'");
        System.out.println("debugBase64UrlIdLength: " + request.getDebugBase64UrlIdLength());
        System.out.println("debugBase64UrlIdIsEmpty: " + request.getDebugBase64UrlIdIsEmpty());
        System.out.println("debugAttestationResponseType: " + request.getDebugAttestationResponseType());
        System.out.println("debugTransports: " + request.getDebugTransports());
        System.out.println("==========================================================");
        
        System.out.println("\n=== RECEIVED CREDENTIAL DATA ===");
        System.out.println("Received credentialId:");
        System.out.println("  - Value: '" + request.getCredentialId() + "'");
        System.out.println("  - Length: " + (request.getCredentialId() != null ? request.getCredentialId().length() : "NULL"));
        System.out.println("  - Is empty string: " + (request.getCredentialId() != null && request.getCredentialId().isEmpty()));
        System.out.println("  - Is null: " + (request.getCredentialId() == null));
        System.out.println("  - First 50 chars: '" + (request.getCredentialId() != null && request.getCredentialId().length() > 0 ? request.getCredentialId().substring(0, Math.min(50, request.getCredentialId().length())) : "N/A") + "'");
        System.out.println("Received credentialName: '" + request.getCredentialName() + "'");
        System.out.println("Received transports: '" + request.getTransports() + "'");
        System.out.println("Received attestationObject length: " + (request.getAttestationObject() != null ? request.getAttestationObject().length() : "NULL"));
        System.out.println("Received clientDataJSON length: " + (request.getClientDataJSON() != null ? request.getClientDataJSON().length() : "NULL"));
        System.out.println("================================");
        
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            System.out.println("ERROR: User not found");
            System.out.println("=========================================\n");
            return gson.toJson(new ErrorResponse("User not found"));
        }

        boolean success = webAuthnService.finishRegistration(
                user,
                request.getCredentialId(),
                request.getTransports(),
                request.getAttestationObject(),
                request.getClientDataJSON(),
                request.getCredentialName()
        );

        userRepository.save(user);
        
        System.out.println("\n=== REGISTRATION RESULT ===");
        System.out.println("Registration success: " + success);
        System.out.println("User WebAuthn enabled: " + user.isWebauthnEnabled());
        System.out.println("User credentials count: " + user.getCredentials().size());
        if (!user.getCredentials().isEmpty()) {
            user.getCredentials().forEach(cred -> {
                System.out.println("Stored Credential ID:");
                System.out.println("  - Value: '" + cred.getCredentialId() + "'");
                System.out.println("  - Length: " + cred.getCredentialId().length());
                System.out.println("  - Is empty: " + cred.getCredentialId().isEmpty());
                System.out.println("  - First 50 chars: '" + cred.getCredentialId().substring(0, Math.min(50, cred.getCredentialId().length())) + "'");
            });
        }
        System.out.println("===========================");
        System.out.println("=========================================\n");

        if (success) {
            return gson.toJson(new SuccessResponse("WebAuthn credential registered successfully"));
        } else {
            return gson.toJson(new ErrorResponse("Failed to register credential"));
        }
    }

    @PostMapping("/api/auth/credential/remove")
    @ResponseBody
    @Transactional
    public String removeCredential(@RequestParam String credentialId, Authentication authentication) {
        // Verify user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return gson.toJson(new ErrorResponse("User not authenticated"));
        }
        
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            return gson.toJson(new ErrorResponse("User not found"));
        }
        
        // Verify credential belongs to this user and exists in database
        Optional<WebAuthnCredential> credentialOpt = webAuthnCredentialRepository.findByCredentialId(credentialId);
        
        if (credentialOpt.isEmpty() || !credentialOpt.get().getUser().getId().equals(user.getId())) {
            System.out.println("Credential not found or does not belong to user: " + credentialId);
            return gson.toJson(new ErrorResponse("Credential not found or does not belong to this user"));
        }
        
        try {
            System.out.println("[REMOVE] Starting credential removal for: " + credentialId);
            System.out.println("[REMOVE] User: " + username);
            
            // Count before deletion
            List<WebAuthnCredential> beforeDeletion = webAuthnCredentialRepository.findByUser(user);
            System.out.println("[REMOVE] Credentials before deletion: " + beforeDeletion.size());
            
            // Delete the credential from database using repository (returns count of deleted records)
            int deletedCount = webAuthnCredentialRepository.deleteByCredentialId(credentialId);
            System.out.println("[REMOVE] Deleted " + deletedCount + " records from database");
            
            // Count after deletion
            List<WebAuthnCredential> afterDeletion = webAuthnCredentialRepository.findByUser(user);
            System.out.println("[REMOVE] Credentials after deletion: " + afterDeletion.size());
            
            // If no credentials left, disable WebAuthn
            if (afterDeletion.isEmpty()) {
                user.setWebauthnEnabled(false);
                User savedUser = userRepository.save(user);
                System.out.println("[REMOVE] WebAuthn disabled for user: " + username);
                System.out.println("[REMOVE] Updated user webauthnEnabled: " + savedUser.isWebauthnEnabled());
            } else {
                System.out.println("[REMOVE] User still has " + afterDeletion.size() + " credentials remaining");
            }
            
            System.out.println("[REMOVE] Credential removal completed successfully");
            return gson.toJson(new SuccessResponse("Credential removed successfully"));
        } catch (Exception e) {
            System.err.println("[REMOVE] Error removing credential: " + e.getMessage());
            e.printStackTrace();
            return gson.toJson(new ErrorResponse("Error removing credential: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auth/authenticate/begin")
    @ResponseBody
    public ResponseEntity<?> beginWebAuthnAuthentication(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            System.out.println("=== WebAuthn Authentication Begin ===");
            System.out.println("Username: " + username);
            
            User user = userRepository.findByUsername(username).orElse(null);

            if (user == null || !user.isWebauthnEnabled() || user.getCredentials().isEmpty()) {
                System.out.println("ERROR: User not found OR WebAuthn not enabled OR no credentials");
                if (user != null) {
                    System.out.println("  - WebAuthn enabled: " + user.isWebauthnEnabled());
                    System.out.println("  - Credentials count: " + user.getCredentials().size());
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("User not found or WebAuthn not enabled"));
            }
            
            System.out.println("User found with " + user.getCredentials().size() + " credentials");
            user.getCredentials().forEach(cred -> {
                System.out.println("  - Credential: " + cred.getCredentialId().substring(0, Math.min(30, cred.getCredentialId().length())) + "...");
            });

            WebAuthnService.AuthenticationRequest authRequest = webAuthnService.startAuthentication(user);
            
            // Build a clean response with only what we need
            Map<String, Object> response = new HashMap<>();
            response.put("challenge", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(authRequest.challenge));
            
            // Build options manually to avoid serialization issues
            Map<String, Object> options = new HashMap<>();
            options.put("challenge", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(authRequest.challenge));
            options.put("timeout", 60000);
            options.put("userVerification", "discouraged");
            
            // Add allowed credentials WITHOUT transports to allow all authenticator types
            List<Map<String, Object>> allowedCredentials = new ArrayList<>();
            for (WebAuthnCredential cred : user.getCredentials()) {
                Map<String, Object> credMap = new HashMap<>();
                credMap.put("id", cred.getCredentialId());
                credMap.put("type", "public-key");
                allowedCredentials.add(credMap);
            }
            options.put("allowCredentials", allowedCredentials);
            
            response.put("options", options);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auth/authenticate/finish")
    @ResponseBody
    public String finishWebAuthnAuthentication(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String username = request.get("username");
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return gson.toJson(new ErrorResponse("User not found"));
        }

        try {
            String authenticatorDataB64U = request.get("authenticatorData");
            String clientDataJSONB64U = request.get("clientDataJSON");
            
            if (authenticatorDataB64U == null || clientDataJSONB64U == null) {
                return gson.toJson(new ErrorResponse("Missing authentication data"));
            }

            // Convert Base64Url back to standard Base64 for safe handling
            String authenticatorDataB64 = base64UrlToBase64(authenticatorDataB64U);
            String clientDataJSONB64 = base64UrlToBase64(clientDataJSONB64U);

            boolean success = webAuthnService.finishAuthentication(user, authenticatorDataB64, clientDataJSONB64);

            if (success) {
                // 1. Create the Authentication object
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), 
                        null, 
                        List.of(() -> "ROLE_USER")
                );

                // 2. Create and set the SecurityContext
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

                // 3. Manually save the context to the HttpSession so Spring recognizes it on the next request
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

                return gson.toJson(new HashMap<String, Object>() {{
                    put("success", true);
                    put("message", "Authentication successful");
                }});
            } else {
                return gson.toJson(new ErrorResponse("Authentication failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return gson.toJson(new ErrorResponse("Authentication error: " + e.getMessage()));
        }
    }

    private String base64UrlToBase64(String base64Url) {
        if (base64Url == null) return null;
        String base64 = base64Url.replace('-', '+').replace('_', '/');
        // Add padding if needed
        int mod = base64.length() % 4;
        if (mod > 0) {
            base64 += "=".repeat(4 - mod);
        }
        return base64;
    }

    static class SuccessResponse {
        public String message;

        SuccessResponse(String message) {
            this.message = message;
        }
    }

    static class ErrorResponse {
        public String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }
}