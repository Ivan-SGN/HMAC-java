package ru.yandex.practicum.exceptions;

public class InvalidSignatureFormatException extends RuntimeException {
    public InvalidSignatureFormatException(String message) { super(message);}

    public InvalidSignatureFormatException(String message, Throwable cause) {
        super(message, cause);
    }

}
