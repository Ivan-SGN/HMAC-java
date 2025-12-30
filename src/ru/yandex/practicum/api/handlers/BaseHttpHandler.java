package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.api.dto.ApiErrorCodes;
import ru.yandex.practicum.api.dto.ErrorResponse;
import ru.yandex.practicum.api.logging.AppLogger;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.exceptions.InvalidJsonException;
import ru.yandex.practicum.exceptions.InvalidMsgException;
import ru.yandex.practicum.exceptions.MethodNotAllowedException;
import ru.yandex.practicum.exceptions.TooLargePayloadException;
import ru.yandex.practicum.exceptions.UnsupportedMediaTypeException;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String RESPONSE_CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

    protected final SignatureService signatureService;
    protected final Gson gson;
    protected final AppLogger log;
    protected final int maxBodySizeBytes;

    protected BaseHttpHandler(SignatureService signatureService, Gson gson, AppConfig appConfig) {
        this.signatureService = signatureService;
        this.gson = gson;
        this.maxBodySizeBytes = appConfig.getMaxMsgSizeBytes();
        this.log = AppLogger.forClass(getClass());
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        byte[] bodyBytes = readAllBytesLimited(exchange.getRequestBody(), maxBodySizeBytes);
        return new String(bodyBytes, CHARSET);
    }

    private static byte[] readAllBytesLimited(InputStream inputStream, int limitBytes) throws IOException {
        try (InputStream source = inputStream) {
            byte[] bodyBytes = source.readNBytes(limitBytes + 1);
            if (bodyBytes.length > limitBytes) {
                throw new TooLargePayloadException();
            }
            return bodyBytes;
        }
    }

    protected void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] responseBytes = gson.toJson(body).getBytes(CHARSET);
        exchange.getResponseHeaders().set(HEADER_CONTENT_TYPE, RESPONSE_CONTENT_TYPE_JSON_UTF8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }

    protected void validatePostJson(HttpExchange exchange) {
        if (!isPost(exchange)) {
            throw new MethodNotAllowedException();
        }
        if (!isJsonContentType(exchange)) {
            throw new UnsupportedMediaTypeException();
        }
    }

    protected boolean isPost(HttpExchange exchange) {
        return "POST".equalsIgnoreCase(exchange.getRequestMethod());
    }

    protected boolean isJsonContentType(HttpExchange exchange) {
        String contentType = exchange.getRequestHeaders().getFirst(HEADER_CONTENT_TYPE);
        if (contentType == null) {
            return false;
        }
        return contentType.trim().toLowerCase().startsWith(APPLICATION_JSON);
    }

    protected <T> T parseJson(String body, Class<T> targetClass) {
        if (body == null || body.isBlank()) {
            throw new InvalidJsonException();
        }
        try {
            return gson.fromJson(body, targetClass);
        } catch (JsonParseException exception) {
            throw new InvalidJsonException();
        }
    }

    protected void validateRequireRequestNotBlank(String request) {
        if (request == null || request.isBlank()) {
            throw new InvalidMsgException();
        }
    }

    protected void validateFieldSize(String value) {
        if (value == null) {
            return;
        }
        if (value.getBytes(CHARSET).length > maxBodySizeBytes) {
            throw new TooLargePayloadException();
        }
    }

    protected void sendSuccess(HttpExchange exchange, Object body) throws IOException {
        sendJson(exchange, 200, body);
        log.logResponse(exchange, 200);
    }

    protected void sendError(HttpExchange exchange, int statusCode, String errorCode) throws IOException {
        sendJson(exchange, statusCode, new ErrorResponse(errorCode));
        log.logError(exchange, statusCode, errorCode);
    }

    protected void sendInternal(HttpExchange exchange) throws IOException {
        sendError(exchange, 500, ApiErrorCodes.INTERNAL);
    }

    protected void sendInvalidSignatureFormatException(HttpExchange exchange) throws IOException {
        sendError(exchange, 400, ApiErrorCodes.INVALID_SIGNATURE_FORMAT);
    }
}