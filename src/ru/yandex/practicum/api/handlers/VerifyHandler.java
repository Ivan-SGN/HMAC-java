package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.api.dto.VerifyRequest;
import ru.yandex.practicum.api.dto.VerifyResponse;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.exceptions.ApiRequestException;
import ru.yandex.practicum.exceptions.InvalidMsgException;
import ru.yandex.practicum.service.SignatureService;
import ru.yandex.practicum.util.Base64Codec;

import java.io.IOException;

public class VerifyHandler extends BaseHttpHandler {

    public VerifyHandler(SignatureService signatureService, Gson gson, AppConfig config) {
        super(signatureService, gson, config);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handleVerify(exchange);
        } catch (ApiRequestException exception) {
            sendError(exchange, exception.getStatusCode(), exception.getErrorCode());
        } catch (Exception exception) {
            sendInternal(exchange);
        }
    }

    private void handleVerify(HttpExchange exchange) throws IOException {
        validatePostJson(exchange);
        String body = readRequestBody(exchange);
        VerifyRequest request = parseJson(body, VerifyRequest.class);
        validateRequireRequestNotBlank(request.msg());
        if (request.signature() == null || request.signature().isBlank()) {
            throw new InvalidMsgException();
        }
        Base64Codec.decodeBase64Url(request.signature());
        boolean ok = signatureService.verify(request.msg(), request.signature());
        sendSuccess(exchange, new VerifyResponse(ok));
    }
}