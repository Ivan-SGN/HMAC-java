package ru.yandex.practicum.api.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.yandex.practicum.api.ServerHMACTest;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

abstract class ApiHandlerTestBase extends ServerHMACTest {

    protected HttpResponse<String> postJson(String path, String body) throws Exception {
        return postRaw(path, body, "application/json");
    }

    protected HttpResponse<String> postRaw(String path, String body, String contentType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    protected JsonObject parseJsonObject(String body) {
        return JsonParser.parseString(body).getAsJsonObject();
    }

    protected void assertError(String body, String expectedErrorCode) {
        JsonObject json = parseJsonObject(body);
        assertTrue(json.has("error"), "response should contain error");
        assertEquals(expectedErrorCode, json.get("error").getAsString(), "error code should match");
    }
}
