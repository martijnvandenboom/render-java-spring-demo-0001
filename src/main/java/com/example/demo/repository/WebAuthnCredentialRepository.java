package com.example.demo.repository;

import com.example.demo.entity.WebAuthnCredential;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebAuthnCredentialRepository extends JpaRepository<WebAuthnCredential, Long> {
    Optional<WebAuthnCredential> findByCredentialId(String credentialId);
    List<WebAuthnCredential> findByUser(User user);
    
    @Modifying
    @Query("DELETE FROM WebAuthnCredential wc WHERE wc.credentialId = :credentialId")
    int deleteByCredentialId(String credentialId);
}
