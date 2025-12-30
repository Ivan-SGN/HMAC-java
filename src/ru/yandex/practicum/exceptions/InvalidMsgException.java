package ru.yandex.practicum.exceptions;

import ru.yandex.practicum.api.dto.ApiErrorCodes;

public class InvalidMsgException extends ApiRequestException {

    public InvalidMsgException() {
        super(400, ApiErrorCodes.INVALID_MSG);
    }
}