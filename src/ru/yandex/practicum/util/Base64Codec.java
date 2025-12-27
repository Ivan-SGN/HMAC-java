package ru.yandex.practicum.util;

import java.util.Base64;

public final class Base64Codec {

    public static String encodeBase64Url(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static byte[] decodeBase64Url(String base64Url) {
        if (base64Url == null || base64Url.isBlank()) {
            throw new IllegalArgumentException("base64Url is blank");
        }
        return Base64.getUrlDecoder().decode(base64Url);
    }

    public static String encodeBase64(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            throw new IllegalArgumentException("base64 is blank");
        }
        return Base64.getDecoder().decode(base64);
    }
}