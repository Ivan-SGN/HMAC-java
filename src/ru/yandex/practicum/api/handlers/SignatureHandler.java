package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;

public class SignatureHandler extends BaseHttpHandler {
    public SignatureHandler(SignatureService signatureService, Gson gson) {
        super(signatureService, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
