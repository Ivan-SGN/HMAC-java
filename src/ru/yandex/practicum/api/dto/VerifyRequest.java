package ru.yandex.practicum.api.dto;

public record VerifyRequest(String msg, String signature) { }