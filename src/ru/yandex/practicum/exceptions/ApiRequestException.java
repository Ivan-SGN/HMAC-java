package ru.yandex.practicum.exceptions;

public abstract class ApiRequestException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    protected ApiRequestException(int statusCode, String errorCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}