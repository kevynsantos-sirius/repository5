package com.totaldocs.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TemporalCryptoIdUtil {

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int GCM_TAG_LENGTH = 128;

    private static final int IV_LENGTH = 12;

    private final String secretKey;

    private final SecureRandom secureRandom = new SecureRandom();

    public TemporalCryptoIdUtil(
            @Value("${secret.key.identifier.model}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    /**
     * Gera chave AES
     */
    private SecretKeySpec getKey() {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);

        byte[] normalizedKey = new byte[32];

        System.arraycopy(
                keyBytes,
                0,
                normalizedKey,
                0,
                Math.min(keyBytes.length, normalizedKey.length)
        );

        return new SecretKeySpec(normalizedKey, ALGORITHM);
    }

    /**
     * Verifica UUID puro
     */
    public boolean isUUID(String value) {

        try {

            UUID.fromString(value);

            return true;

        } catch (IllegalArgumentException e) {

            return false;
        }
    }

    /**
     * Gera token protegido pela sessão atual
     */
    public String generateToken(
            Integer recordId,
            Pair<String,Integer> controle
    ) {

        String payload =
                recordId +
                "|" +
                controle.getFirst() +
                "|" +
                controle.getSecond();

        return encrypt(payload);
    }
    
    public String generateTokenLong(
            Long recordId,
            Pair<String,Integer> controle
    ) {

        String payload =
                recordId +
                "|" +
                controle.getFirst() +
                "|" +
                controle.getSecond();

        return encrypt(payload);
    }

    /**
     * Extrai ID validando sessão e usuário
     */
    public Integer extractId(
            String token,
            Pair<String,Integer> controle
    ) {

        if (isUUID(token)) {
            return null;
        }

        try {

            String decrypted = decrypt(token);

            String[] parts = decrypted.split("\\|");

            if (parts.length != 3) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token inválido"
                );
            }

            Integer recordId = Integer.parseInt(parts[0]);

            String tokenSessionId = parts[1];

            Integer tokenUserId = Integer.parseInt(parts[2]);

            /**
             * Valida sessão
             */
            if (!tokenSessionId.equals(controle.getFirst())) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Sessão inválida"
                );
            }

            /**
             * Valida usuário
             */
            if (!tokenUserId.equals(controle.getSecond())) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário inválido"
                );
            }

            return recordId;

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token inválido"
            );
        }
    }
    
    public Long extractIdLong(
            String token,
            Pair<String,Integer> controle
    ) {

        if (isUUID(token)) {
            return null;
        }

        try {

            String decrypted = decrypt(token);

            String[] parts = decrypted.split("\\|");

            if (parts.length != 3) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Token inválido"
                );
            }

            Long recordId = Long.parseLong(parts[0]);

            String tokenSessionId = parts[1];

            Long tokenUserId = Long.parseLong(parts[2]);

            /**
             * Valida sessão
             */
            if (!tokenSessionId.equals(controle.getFirst())) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Sessão inválida"
                );
            }

            /**
             * Valida usuário
             */
            Long userIdBySession = Long.parseLong(String.valueOf(controle.getSecond()));
            if (!tokenUserId.equals(userIdBySession)) {

                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário inválido"
                );
            }

            return recordId;

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token inválido"
            );
        }
    }

    /**
     * Criptografa usando AES GCM
     */
    private String encrypt(String value) {

        try {

            byte[] iv = new byte[IV_LENGTH];

            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(
                    TRANSFORMATION
            );

            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    );

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getKey(),
                    spec
            );

            byte[] encrypted =
                    cipher.doFinal(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            ByteBuffer byteBuffer =
                    ByteBuffer.allocate(
                            iv.length + encrypted.length
                    );

            byteBuffer.put(iv);

            byteBuffer.put(encrypted);

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(
                            byteBuffer.array()
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao criptografar token",
                    e
            );
        }
    }

    /**
     * Descriptografa AES GCM
     */
    private String decrypt(String encryptedValue) {

        try {

            byte[] decoded =
                    Base64.getUrlDecoder()
                            .decode(encryptedValue);

            ByteBuffer byteBuffer =
                    ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];

            byteBuffer.get(iv);

            byte[] encrypted =
                    new byte[byteBuffer.remaining()];

            byteBuffer.get(encrypted);

            Cipher cipher =
                    Cipher.getInstance(
                            TRANSFORMATION
                    );

            GCMParameterSpec spec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getKey(),
                    spec
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao descriptografar token",
                    e
            );
        }
    }
}