package ru.yandex.practicum.api.dto;

public class VerifyRequest {

    private String msg;
    private String signature;

    public String msg() {
        return msg;
    }

    public String signature() {
        return signature;
    }
}