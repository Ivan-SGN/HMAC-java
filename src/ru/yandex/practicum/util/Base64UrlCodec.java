package ru.yandex.practicum.util;

import java.util.Base64;

public final class Base64UrlCodec {

    private Base64UrlCodec() {
    }

    public static String encode(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static byte[] decode(String base64Url) {
        if (base64Url == null || base64Url.isBlank()) {
            throw new IllegalArgumentException("base64Url is blank");
        }
        return Base64.getUrlDecoder().decode(base64Url);
    }
}