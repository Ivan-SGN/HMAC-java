package ru.yandex.practicum;

import ru.yandex.practicum.service.HmacSignatureService;
import ru.yandex.practicum.service.SignatureService;

import java.util.Base64;

public class ServerHMAC {
    public static void main(String[] args) {
        byte[] key = Base64.getDecoder().decode("c2VjcmV0LWtleS0xMjM0");

        SignatureService signatureService =
                new HmacSignatureService("HmacSHA256", key);

        String message = "hello";

        String signature = signatureService.sign(message);
        System.out.println("Signature: " + signature);

        boolean valid = signatureService.verify(message, signature);
        System.out.println("Valid (expected true): " + valid);

        boolean invalidMessage = signatureService.verify("hello!", signature);
        System.out.println("Valid with modified message (expected false): " + invalidMessage);

        boolean invalidSignature = signatureService.verify(message, signature + "A");
        System.out.println("Valid with modified signature (expected false): " + invalidSignature);
    }
}
