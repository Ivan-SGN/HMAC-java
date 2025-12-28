package ru.yandex.practicum.service;

public interface SignatureService {

    String sign(String message);

    boolean verify(String message, String signature);
}
