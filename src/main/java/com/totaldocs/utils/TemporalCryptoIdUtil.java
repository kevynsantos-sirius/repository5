package com.totaldocs.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class TemporalCryptoIdUtil {

    private static final String ALGORITHM = "AES";

    // 16, 24 ou 32 bytes
    private static final String SECRET_KEY = "c3f55ad5e5671088077b3a0169002510";

    // Tempo padrão do token (ex: 10 minutos)
    private static final long DEFAULT_EXPIRATION_SECONDS = 600;

    private static SecretKeySpec getKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * Gera um token criptografado com expiração
     */
    public static String generateToken(Long recordId) {
        long expiresAt = Instant.now()
                .plusSeconds(DEFAULT_EXPIRATION_SECONDS)
                .toEpochMilli();

        String payload = recordId + "|" + expiresAt;

        return encrypt(payload);
    }
    
    public static String getIntToGenerateToken(int idInt)
    {
    	long id = idInt;
		String uuid = TemporalCryptoIdUtil.generateToken(id);
		
		return uuid;
    }

    /**
     * Recupera o ID do token e valida expiração
     */
    public static Long extractId(String token) {
        String decrypted = decrypt(token);

        String[] parts = decrypted.split("\\|");
        if (parts.length != 2) {
            throw new RuntimeException("Token inválido");
        }

        Long recordId = Long.parseLong(parts[0]);
        long expiresAt = Long.parseLong(parts[1]);

        if (Instant.now().toEpochMilli() > expiresAt) {
            throw new RuntimeException("Token expirado");
        }

        return recordId;
    }

    private static String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar token", e);
        }
    }

    private static String decrypt(String encryptedValue) {
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
