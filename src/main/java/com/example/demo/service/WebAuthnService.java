package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.WebAuthnCredential;
import com.example.demo.repository.WebAuthnCredentialRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.data.exception.Base64UrlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

@Service
public class WebAuthnService {

    @Autowired
    private WebAuthnCredentialRepository credentialRepository;

    //private final String rpId = "localhost";
    private final String rpId = "render-java-spring-demo-0001.onrender.com";
    private final String rpName = "Demo App";

    public RegistrationRequest startRegistration(User user) throws IOException, Base64UrlException {
        byte[] challenge = new byte[32];
        new SecureRandom().nextBytes(challenge);
        
        // Use Base64URL encoder directly (no need to convert to string)
        byte[] userId = user.getId().toString().getBytes();

        UserIdentity userIdentity = UserIdentity.builder()
                .name(user.getUsername())
                .displayName(user.getUsername())
                .id(new ByteArray(userId))
                .build();

        PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.builder()
                .rp(RelyingPartyIdentity.builder()
                        .id(rpId)
                        .name(rpName)
                        .build())
                .user(userIdentity)
                .challenge(new ByteArray(challenge))
                .pubKeyCredParams(List.of(
                        PublicKeyCredentialParameters.builder()
                                .alg(COSEAlgorithmIdentifier.ES256)
                                .type(PublicKeyCredentialType.PUBLIC_KEY)
                                .build(),
                        PublicKeyCredentialParameters.builder()
                                .alg(COSEAlgorithmIdentifier.RS256)
                                .type(PublicKeyCredentialType.PUBLIC_KEY)
                                .build()
                ))
                .authenticatorSelection(AuthenticatorSelectionCriteria.builder()
                        .authenticatorAttachment(AuthenticatorAttachment.PLATFORM)
                        .residentKey(ResidentKeyRequirement.PREFERRED)
                        .userVerification(UserVerificationRequirement.DISCOURAGED)
                        .build())
                .timeout(300000)
                .attestation(AttestationConveyancePreference.DIRECT)
                .build();

        return new RegistrationRequest(challenge, options);
    }

    public boolean finishRegistration(User user, String credentialId, String transports, String attestationObject, String clientDataJSON, String credentialName) {
        try {
            // Store credential with the actual ID from the authenticator
            WebAuthnCredential credential = new WebAuthnCredential(
                    user,
                    credentialId,
                    attestationObject,
                    credentialName
            );
            // Set transports if provided
            if (transports != null && !transports.isEmpty()) {
                credential.setTransports(transports);
            }
            credentialRepository.save(credential);
            user.setWebauthnEnabled(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AuthenticationRequest startAuthentication(User user) throws IOException, Base64UrlException {
        try {
            byte[] challenge = new byte[32];
            new SecureRandom().nextBytes(challenge);

            List<PublicKeyCredentialDescriptor> allowedCredentials = new ArrayList<>();
            for (WebAuthnCredential cred : user.getCredentials()) {
                PublicKeyCredentialDescriptor.PublicKeyCredentialDescriptorBuilder builder = 
                        PublicKeyCredentialDescriptor.builder()
                                .id(ByteArray.fromBase64Url(cred.getCredentialId()))
                                .type(PublicKeyCredentialType.PUBLIC_KEY);
                
                // Only set transports if they exist, otherwise leave null to allow all authenticators
                Set<AuthenticatorTransport> transports = parseTransports(cred.getTransports());
                if (!transports.isEmpty()) {
                    builder.transports(transports);
                }
                
                allowedCredentials.add(builder.build());
            }

            PublicKeyCredentialRequestOptions options = PublicKeyCredentialRequestOptions.builder()
                    .challenge(new ByteArray(challenge))
                    .allowCredentials(allowedCredentials)
                    .userVerification(UserVerificationRequirement.PREFERRED)
                    .timeout(300000)
                    .build();

            return new AuthenticationRequest(challenge, options);
        } catch (Exception e) {
            throw new IOException("Failed to start authentication: " + e.getMessage(), e);
        }
    }

    public boolean finishAuthentication(User user, String authenticatorData, String clientDataJSON) {
        try {
            // In a real implementation, verify the signature
            // For now, we'll accept it as valid if the user has credentials
            return !user.getCredentials().isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeCredential(String credentialId) {
        credentialRepository.deleteByCredentialId(credentialId);
    }

    private Set<AuthenticatorTransport> parseTransports(String transports) {
        if (transports == null) {
            return new HashSet<>();
        }
        Set<AuthenticatorTransport> result = new HashSet<>();
        for (String transport : transports.split(",")) {
            try {
                result.add(AuthenticatorTransport.valueOf(transport.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore unknown transports
            }
        }
        return result;
    }

    public static class RegistrationRequest {
        public byte[] challenge;
        public PublicKeyCredentialCreationOptions options;

        public RegistrationRequest(byte[] challenge, PublicKeyCredentialCreationOptions options) {
            this.challenge = challenge;
            this.options = options;
        }
    }

    public static class AuthenticationRequest {
        public byte[] challenge;
        public PublicKeyCredentialRequestOptions options;

        public AuthenticationRequest(byte[] challenge, PublicKeyCredentialRequestOptions options) {
            this.challenge = challenge;
            this.options = options;
        }
    }
}
