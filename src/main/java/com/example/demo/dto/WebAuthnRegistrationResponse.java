package com.example.demo.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import java.util.Base64;

public class WebAuthnRegistrationResponse {
    public String challenge;
    public JsonObject options;

    public WebAuthnRegistrationResponse(byte[] challengeBytes, PublicKeyCredentialCreationOptions opts) {
        // Encode challenge as Base64
        this.challenge = Base64.getEncoder().encodeToString(challengeBytes);
        
        // Serialize options to JSON
        this.options = new JsonObject();
        
        // Add RP info
        JsonObject rp = new JsonObject();
        rp.addProperty("id", opts.getRp().getId());
        rp.addProperty("name", opts.getRp().getName());
        options.add("rp", rp);
        
        // Add user info with Base64-encoded ID
        JsonObject user = new JsonObject();
        user.addProperty("id", Base64.getEncoder().encodeToString(opts.getUser().getId().getBytes()));
        user.addProperty("name", opts.getUser().getName());
        user.addProperty("displayName", opts.getUser().getDisplayName());
        options.add("user", user);
        
        // Add challenge as Base64
        options.addProperty("challenge", this.challenge);
        
        // Add pubKeyCredParams
        JsonArray pubKeyCredParams = new JsonArray();
        for (var param : opts.getPubKeyCredParams()) {
            JsonObject paramObj = new JsonObject();
            paramObj.addProperty("type", "public-key");
            paramObj.addProperty("alg", param.getAlg().getId());
            pubKeyCredParams.add(paramObj);
        }
        options.add("pubKeyCredParams", pubKeyCredParams);
        
        // Add timeout (default 300000 if not set)
        options.addProperty("timeout", 300000L);
        
        // Add attestation
        options.addProperty("attestation", opts.getAttestation().toString());
    }
}
