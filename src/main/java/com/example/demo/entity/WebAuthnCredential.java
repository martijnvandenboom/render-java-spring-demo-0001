package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "webauthn_credentials")
public class WebAuthnCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "credential_id", nullable = false, unique = true, columnDefinition = "LONGTEXT")
    private String credentialId;

    @Column(name = "public_key", nullable = false, columnDefinition = "LONGTEXT")
    private String publicKey;

    @Column(name = "sign_count")
    private long signCount = 0;

    @Column(name = "transports", columnDefinition = "LONGTEXT")
    private String transports;

    @Column(name = "created_at")
    private long createdAt;

    @Column(name = "credential_name")
    private String credentialName;

    public WebAuthnCredential() {}

    public WebAuthnCredential(User user, String credentialId, String publicKey, String credentialName) {
        this.user = user;
        this.credentialId = credentialId;
        this.publicKey = publicKey;
        this.credentialName = credentialName;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getSignCount() {
        return signCount;
    }

    public void setSignCount(long signCount) {
        this.signCount = signCount;
    }

    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }
}
