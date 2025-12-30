package ru.yandex.practicum.service;

import ru.yandex.practicum.util.Base64Codec;
import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
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
            throw new IllegalStateException("unable to initialize Mac", exception);
        }
    }

    public byte[] signBytes(String message) {
        Mac macInstance = createMac();
        return macInstance.doFinal(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String sign(String message) {
        try {
            byte[] signatureBytes = signBytes(message);
            return Base64Codec.encodeBase64Url(signatureBytes);
        } catch (IllegalArgumentException exception) {
            throw new InvalidSignatureFormatException(exception);
        }
    }

    @Override
    public boolean verify(String message, String signature) {
        byte[] providedSignatureBytes;
        byte[] expectedSignatureBytes;
        try {
            providedSignatureBytes = Base64Codec.decodeBase64Url(signature);
            expectedSignatureBytes = signBytes(message);
        } catch (IllegalArgumentException exception) {
            throw new InvalidSignatureFormatException(exception);
        }
        return MessageDigest.isEqual(expectedSignatureBytes, providedSignatureBytes);
    }
}
