package ru.yandex.practicum.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    protected final SignatureService signatureService;
    protected final Gson gson;

    protected BaseHttpHandler(SignatureService signatureService, Gson gson) {
        this.signatureService = signatureService;
        this.gson = gson;
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), CHARSET);
        }
    }

    protected void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] responseBytes = gson.toJson(body).getBytes(CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }

    protected void sendText(HttpExchange exchange, int statusCode, String text) throws IOException {
        byte[] responseBytes = text.getBytes(CHARSET);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(responseBytes);
        }
    }
}