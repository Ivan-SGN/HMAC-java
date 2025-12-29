package ru.yandex.practicum.exceptions;

public class TooLargePayloadException extends ApiRequestException {

    public TooLargePayloadException(int maxSizeBytes) {
        super(413, "payload_too_large", "payload exceeds max size: " + maxSizeBytes);
    }
}