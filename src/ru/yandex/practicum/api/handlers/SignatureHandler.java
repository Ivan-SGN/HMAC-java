package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.api.dto.SignRequest;
import ru.yandex.practicum.api.dto.SignResponse;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.exceptions.ApiRequestException;
import ru.yandex.practicum.exceptions.InvalidMsgException;
import ru.yandex.practicum.exceptions.InvalidSignatureFormatException;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;

public class SignatureHandler extends BaseHttpHandler {

    public SignatureHandler(SignatureService signatureService, Gson gson, AppConfig config) {
        super(signatureService, gson, config);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            handleSign(exchange);
        } catch (InvalidSignatureFormatException exception) {
            sendInvalidSignatureFormatException(exchange);
        } catch (ApiRequestException exception) {
            sendError(exchange, exception.getStatusCode(), exception.getErrorCode());
        } catch (Exception exception) {
            sendInternal(exchange);
        }
    }

    private void handleSign(HttpExchange exchange) throws IOException {
        validatePostJson(exchange);
        String body = readRequestBody(exchange);
        SignRequest request = parseJson(body, SignRequest.class);
        validateRequireRequestNotBlank(request.msg());
        validateFieldSize(request.msg());
        String signature = signatureService.sign(request.msg());
        sendSuccess(exchange, new SignResponse(signature));
    }
}