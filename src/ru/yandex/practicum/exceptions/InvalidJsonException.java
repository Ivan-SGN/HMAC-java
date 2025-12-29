package ru.yandex.practicum.exceptions;

public class InvalidJsonException extends ApiRequestException {

    public InvalidJsonException() {
        super(400, "invalid_json", "invalid json");
    }
}