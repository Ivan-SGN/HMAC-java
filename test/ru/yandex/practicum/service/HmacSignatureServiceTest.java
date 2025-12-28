package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.util.Base64Codec;
import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;

import static org.junit.jupiter.api.Assertions.*;

class HmacSignatureServiceTest {

    private final String algorithm = "HmacSHA256";
    private final byte[] key = Base64Codec.decodeBase64("c2VjcmV0LWtleS0xMjM0");
    private SignatureService service;
    private final String message = "hello";

    @BeforeEach
    void setUp(){
        service = new HmacSignatureService(algorithm, key);
    }

    @Test
    void testSignIsDeterministic() {
        String first = service.sign(message);
        String second = service.sign(message);

        assertEquals(first, second, "signature should be deterministic for the same input and key");
    }

    @Test
    void testVerifyReturnsTrueForValidSignature() {
        String signature = service.sign(message);

        assertTrue(service.verify(message, signature), "verify should return true for a valid signature");
    }

    @Test
    void testVerifyReturnsFalseForModifiedMessage() {
        String signature = service.sign(message);

        assertFalse(service.verify("hello!", signature), "verify should return false for a modified message");
    }

    @Test
    void testVerifyThrowsExceptionForInvalidSignatureFormat() {
        assertThrows(
                InvalidSignatureFormatException.class,
                () -> service.verify(message, "@@@"),
                "invalid signature format should cause exception"
        );
    }
}