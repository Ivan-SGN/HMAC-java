package ru.yandex.practicum.exceptions;

public class MethodNotAllowedException extends ApiRequestException {

    public MethodNotAllowedException() {
        super(405, "method_not_allowed", "method not allowed");
    }
}