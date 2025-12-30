package ru.yandex.practicum.exceptions;

import ru.yandex.practicum.api.dto.ApiErrorCodes;

public class MethodNotAllowedException extends ApiRequestException {

    public MethodNotAllowedException() {
        super(405, ApiErrorCodes.METHOD_NOT_ALLOWED);
    }
}