package ru.yandex.practicum.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacSignatureService implements SignatureService {

    private final String algorithm;
    private final byte[] key;

    public HmacSignatureService(String algorithm, byte[] key) {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm is blank");
        }
        if (key == null || key.length == 0) {
            throw new IllegalArgumentException("key is empty");
        }
        this.algorithm = algorithm;
        this.key = key.clone();
    }

    private Mac createMac() {
        try {
            Mac macInstance = Mac.getInstance(algorithm);
            macInstance.init(new SecretKeySpec(key, algorithm));
            return macInstance;
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new IllegalStateException("Unable to initialize Mac", exception);
        }
    }

    public byte[] signBytes(String message) {
        Mac macInstance = createMac();
        return macInstance.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    public String sign(String message) {
        byte[] signatureBytes = signBytes(message);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    public boolean verify(String message, String signature) {
        byte[] expectedSignatureBytes = signBytes(message);
        byte[] providedSignatureBytes;

        try {
            providedSignatureBytes = Base64.getUrlDecoder().decode(signature);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        return MessageDigest.isEqual(expectedSignatureBytes, providedSignatureBytes);
    }
}
