package ru.yandex.practicum.exceptions;

public class InvalidSignatureFormatException extends RuntimeException {

    public InvalidSignatureFormatException() {
        super();
    }

    public InvalidSignatureFormatException(Throwable cause) {
        super(cause);
    }
}