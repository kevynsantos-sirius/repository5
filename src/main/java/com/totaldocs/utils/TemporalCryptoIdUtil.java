package com.totaldocs.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
public class TemporalCryptoIdUtil {

    private final String secretKey;

    public TemporalCryptoIdUtil(
        @Value("${secret.key.identifier.model}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    private static final String ALGORITHM = "AES";
    private static final long DEFAULT_EXPIRATION_SECONDS = 600;

    private SecretKeySpec getKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    private boolean isUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String generateToken(Integer recordId) {
        long expiresAt = Instant.now()
                .plusSeconds(DEFAULT_EXPIRATION_SECONDS)
                .toEpochMilli();

        String payload = recordId + "|" + expiresAt;
        return encrypt(payload);
    }

    public Integer extractId(String token) {
    	
    	if(isUUID(token))
    	{
    		return null;
    	}
        String decrypted = decrypt(token);

        String[] parts = decrypted.split("\\|");
        if (parts.length != 2) {
            throw new RuntimeException("Token inválido");
        }

        Integer recordId = Integer.parseInt(parts[0]);
        Long expiresAt = Long.parseLong(parts[1]);

        if (Instant.now().toEpochMilli() > expiresAt) {
            throw new RuntimeException("Token expirado");
        }

        return recordId;
    }

    private String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar token", e);
        }
    }

    private String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());

            byte[] decoded = Base64.getUrlDecoder().decode(encryptedValue);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar token", e);
        }
    }
}
