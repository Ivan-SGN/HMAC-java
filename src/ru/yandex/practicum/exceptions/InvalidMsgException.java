package ru.yandex.practicum.exceptions;

public class InvalidMsgException extends ApiRequestException {

    public InvalidMsgException() {
        super(400, "invalid_msg", "invalid_msg");
    }
}