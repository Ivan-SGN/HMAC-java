package ru.yandex.practicum.exceptions;

import ru.yandex.practicum.api.dto.ApiErrorCodes;

public class UnsupportedMediaTypeException extends ApiRequestException {

    public UnsupportedMediaTypeException() {
        super(415, ApiErrorCodes.UNSUPPORTED_MEDIA_TYPE);
    }
}