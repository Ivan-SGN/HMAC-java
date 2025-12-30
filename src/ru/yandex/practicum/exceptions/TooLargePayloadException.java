package ru.yandex.practicum.exceptions;

import ru.yandex.practicum.api.dto.ApiErrorCodes;

public class TooLargePayloadException extends ApiRequestException {

    public TooLargePayloadException() {
        super(413, ApiErrorCodes.PAYLOAD_TOO_LARGE);
    }
}