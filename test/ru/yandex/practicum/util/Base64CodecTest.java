package ru.yandex.practicum.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class Base64CodecTest {

    private final byte[] textBytes = "hello".getBytes(StandardCharsets.UTF_8);
    private final byte[] secretBytes = "secret-key-1234".getBytes(StandardCharsets.UTF_8);

    @Test
    void testBase64UrlEncodeDecodeRoundTrip() {
        String encoded = Base64Codec.encodeBase64Url(textBytes);
        byte[] decoded = Base64Codec.decodeBase64Url(encoded);

        assertArrayEquals(textBytes, decoded, "decoded bytes should match original input");
    }

    @Test
    void testBase64UrlDoesNotContainPadding() {
        String encoded = Base64Codec.encodeBase64Url(textBytes);

        assertFalse(encoded.contains("="), "base64url output should not contain padding");
    }

    @Test
    void testBase64EncodeDecodeRoundTrip() {
        String encoded = Base64Codec.encodeBase64(secretBytes);
        byte[] decoded = Base64Codec.decodeBase64(encoded);

        assertArrayEquals(secretBytes, decoded, "decoded bytes should match original input");
    }
}