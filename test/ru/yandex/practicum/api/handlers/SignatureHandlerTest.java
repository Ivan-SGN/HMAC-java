package ru.yandex.practicum.api.handlers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.api.dto.ApiErrorCodes;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class SignatureHandlerTest extends ApiHandlerTestBase {

    private static final String SIGN_PATH = "/sign";

    @Test
    void testSignReturnsSignature() throws Exception {
        HttpResponse<String> response = postJson(SIGN_PATH, "{\"msg\":\"hello\"}");

        assertEquals(200, response.statusCode(), "status should be 200");

        JsonObject json = parseJsonObject(response.body());
        assertTrue(json.has("signature"), "response should contain signature");
        assertFalse(json.get("signature").getAsString().isBlank(), "signature should not be blank");
    }

    @Test
    void testSignEmptyMsgReturns400() throws Exception {
        HttpResponse<String> response = postJson(SIGN_PATH, "{\"msg\":\"\"}");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_MSG);
    }

    @Test
    void testSignInvalidJsonReturns400() throws Exception {
        HttpResponse<String> response = postJson(SIGN_PATH, "{\"msg\":");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_JSON);
    }

    @Test
    void testSignUnsupportedMediaTypeReturns415() throws Exception {
        HttpResponse<String> response = postRaw(SIGN_PATH, "{\"msg\":\"hello\"}", "text/plain");

        assertEquals(415, response.statusCode(), "status should be 415");
        assertError(response.body(), ApiErrorCodes.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void testSignMethodNotAllowedReturns405() throws Exception {
        HttpResponse<String> response = get(SIGN_PATH);

        assertEquals(405, response.statusCode(), "status should be 405");
        assertError(response.body(), ApiErrorCodes.METHOD_NOT_ALLOWED);
    }

    @Test
    void testSignTooLargePayloadReturns413() throws Exception {
        String largeBody = "a".repeat(MAX_BODY_SIZE_BYTES + 1);
        HttpResponse<String> response = postJson(SIGN_PATH, largeBody);

        assertEquals(413, response.statusCode(), "status should be 413");
        assertError(response.body(), ApiErrorCodes.PAYLOAD_TOO_LARGE);
    }

    @Test
    void testSignIsDeterministicForSameMessage() throws Exception {
        HttpResponse<String> firstResponse = postJson(SIGN_PATH, "{\"msg\":\"hello\"}");
        HttpResponse<String> secondResponse = postJson(SIGN_PATH, "{\"msg\":\"hello\"}");

        assertEquals(200, firstResponse.statusCode(), "first status should be 200");
        assertEquals(200, secondResponse.statusCode(), "second status should be 200");

        JsonObject firstJson = parseJsonObject(firstResponse.body());
        JsonObject secondJson = parseJsonObject(secondResponse.body());

        assertTrue(firstJson.has("signature"), "first response should contain signature");
        assertTrue(secondJson.has("signature"), "second response should contain signature");

        String firstSignature = firstJson.get("signature").getAsString();
        String secondSignature = secondJson.get("signature").getAsString();

        assertEquals(firstSignature, secondSignature, "signatures should be identical for the same message");
    }
    @Test
    void testSignTooLargeMsgReturns413() throws Exception {
        String tooLargeMsg = "a".repeat(MAX_BODY_SIZE_BYTES + 1);
        String body = "{\"msg\":\"" + tooLargeMsg + "\"}";

        HttpResponse<String> response = postJson(SIGN_PATH, body);

        assertEquals(413, response.statusCode(), "status should be 413");
        assertError(response.body(), ApiErrorCodes.PAYLOAD_TOO_LARGE);
    }
}