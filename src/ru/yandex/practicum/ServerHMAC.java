package ru.yandex.practicum;

import com.google.gson.Gson;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.config.ConfigLoader;
import ru.yandex.practicum.service.HmacSignatureService;
import ru.yandex.practicum.service.SignatureService;

import java.nio.file.Path;

public class ServerHMAC {
    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader(new Gson());
        AppConfig config = configLoader.load(Path.of("config.json"));

        SignatureService signatureService = new HmacSignatureService(
                config.getHmacAlg(),
                config.getSecretKey()
        );

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
