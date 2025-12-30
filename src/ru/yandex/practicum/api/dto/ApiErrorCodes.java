package ru.yandex.practicum.api.dto;

public final class ApiErrorCodes {

    public static final String INVALID_JSON = "invalid_json";
    public static final String INVALID_MSG = "invalid_msg";
    public static final String INVALID_SIGNATURE_FORMAT = "invalid_signature_format";
    public static final String METHOD_NOT_ALLOWED = "method_not_allowed";
    public static final String UNSUPPORTED_MEDIA_TYPE = "unsupported_media_type";
    public static final String PAYLOAD_TOO_LARGE = "payload_too_large";
    public static final String INTERNAL = "internal";

    private ApiErrorCodes() {
    }
}