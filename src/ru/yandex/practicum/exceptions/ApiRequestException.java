package ru.yandex.practicum.exceptions;

public abstract class ApiRequestException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    protected ApiRequestException(int statusCode, String errorCode) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    protected ApiRequestException(int statusCode, String errorCode, Throwable cause) {
        super(cause);
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