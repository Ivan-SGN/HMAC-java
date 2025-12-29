package ru.yandex.practicum.exceptions;

public class UnsupportedMediaTypeException extends ApiRequestException {

    public UnsupportedMediaTypeException() {
        super(415, "unsupported_media_type", "unsupported media type");
    }
}