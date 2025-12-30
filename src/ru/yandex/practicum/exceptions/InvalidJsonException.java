package ru.yandex.practicum.exceptions;

import ru.yandex.practicum.api.dto.ApiErrorCodes;

public class InvalidJsonException extends ApiRequestException {

    public InvalidJsonException() {
        super(400, ApiErrorCodes.INVALID_JSON);
    }
}