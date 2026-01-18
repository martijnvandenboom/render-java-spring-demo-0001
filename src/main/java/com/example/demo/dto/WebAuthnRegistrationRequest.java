package com.example.demo.dto;

public class WebAuthnRegistrationRequest {
    private String credentialName;
    private String credentialId;
    private String transports;
    private String attestationObject;
    private String clientDataJSON;
    
    // Debug fields from frontend
    private String debugRawValue;
    private String debugType;
    private String debugConstructorName;
    private String debugIsArrayBuffer;
    private String debugByteLength;
    private String debugToString;
    private String debugAttestationResponseId;
    private String debugBase64UrlId;
    private Integer debugBase64UrlIdLength;
    private Boolean debugBase64UrlIdIsEmpty;
    private String debugAttestationResponseType;
    private String debugTransports;

    public WebAuthnRegistrationRequest() {}

    public String getCredentialName() {
        return credentialName;
    }

    public void setCredentialName(String credentialName) {
        this.credentialName = credentialName;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }

    public String getAttestationObject() {
        return attestationObject;
    }

    public void setAttestationObject(String attestationObject) {
        this.attestationObject = attestationObject;
    }

    public String getClientDataJSON() {
        return clientDataJSON;
    }

    public void setClientDataJSON(String clientDataJSON) {
        this.clientDataJSON = clientDataJSON;
    }

    // Debug field getters/setters
    public String getDebugAttestationResponseId() {
        return debugAttestationResponseId;
    }

    public void setDebugAttestationResponseId(String debugAttestationResponseId) {
        this.debugAttestationResponseId = debugAttestationResponseId;
    }

    public String getDebugBase64UrlId() {
        return debugBase64UrlId;
    }

    public void setDebugBase64UrlId(String debugBase64UrlId) {
        this.debugBase64UrlId = debugBase64UrlId;
    }

    public Integer getDebugBase64UrlIdLength() {
        return debugBase64UrlIdLength;
    }

    public void setDebugBase64UrlIdLength(Integer debugBase64UrlIdLength) {
        this.debugBase64UrlIdLength = debugBase64UrlIdLength;
    }

    public Boolean getDebugBase64UrlIdIsEmpty() {
        return debugBase64UrlIdIsEmpty;
    }

    public void setDebugBase64UrlIdIsEmpty(Boolean debugBase64UrlIdIsEmpty) {
        this.debugBase64UrlIdIsEmpty = debugBase64UrlIdIsEmpty;
    }

    public String getDebugAttestationResponseType() {
        return debugAttestationResponseType;
    }

    public void setDebugAttestationResponseType(String debugAttestationResponseType) {
        this.debugAttestationResponseType = debugAttestationResponseType;
    }

    public String getDebugTransports() {
        return debugTransports;
    }

    public void setDebugTransports(String debugTransports) {
        this.debugTransports = debugTransports;
    }

    // New debug field getters/setters
    public String getDebugRawValue() {
        return debugRawValue;
    }

    public void setDebugRawValue(String debugRawValue) {
        this.debugRawValue = debugRawValue;
    }

    public String getDebugType() {
        return debugType;
    }

    public void setDebugType(String debugType) {
        this.debugType = debugType;
    }

    public String getDebugConstructorName() {
        return debugConstructorName;
    }

    public void setDebugConstructorName(String debugConstructorName) {
        this.debugConstructorName = debugConstructorName;
    }

    public String getDebugIsArrayBuffer() {
        return debugIsArrayBuffer;
    }

    public void setDebugIsArrayBuffer(String debugIsArrayBuffer) {
        this.debugIsArrayBuffer = debugIsArrayBuffer;
    }

    public String getDebugByteLength() {
        return debugByteLength;
    }

    public void setDebugByteLength(String debugByteLength) {
        this.debugByteLength = debugByteLength;
    }

    public String getDebugToString() {
        return debugToString;
    }

    public void setDebugToString(String debugToString) {
        this.debugToString = debugToString;
    }
}
