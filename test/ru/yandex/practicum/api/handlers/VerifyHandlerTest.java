package ru.yandex.practicum.api.handlers;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.api.dto.ApiErrorCodes;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class VerifyHandlerTest extends ApiHandlerTestBase {

    private static final String VERIFY_PATH = "/verify";

    @Test
    void testVerifyReturnsOkTrueForValidSignature() throws Exception {
        String signature = signatureService.sign("hello");
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"hello\",\"signature\":\"" + signature + "\"}");

        assertEquals(200, response.statusCode(), "status should be 200");

        JsonObject json = parseJsonObject(response.body());
        assertTrue(json.has("ok"), "response should contain ok");
        assertTrue(json.get("ok").getAsBoolean(), "ok should be true");
    }

    @Test
    void testVerifyReturnsOkFalseForChangedMessage() throws Exception {
        String signature = signatureService.sign("hello");
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"hello!\",\"signature\":\"" + signature + "\"}");

        assertEquals(200, response.statusCode(), "status should be 200");

        JsonObject json = parseJsonObject(response.body());
        assertTrue(json.has("ok"), "response should contain ok");
        assertFalse(json.get("ok").getAsBoolean(), "ok should be false");
    }

    @Test
    void testVerifyReturnsOkFalseForChangedSignature() throws Exception {
        String signature = signatureService.sign("hello");
        String changedSignature = changeBase64UrlLastChar(signature);

        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"hello\",\"signature\":\"" + changedSignature + "\"}");

        assertEquals(200, response.statusCode(), "status should be 200");

        JsonObject json = parseJsonObject(response.body());
        assertTrue(json.has("ok"), "response should contain ok");
        assertFalse(json.get("ok").getAsBoolean(), "ok should be false");
    }

    @Test
    void testVerifyInvalidSignatureFormatReturns400() throws Exception {
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"hello\",\"signature\":\"@@@\"}");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_SIGNATURE_FORMAT);
    }

    @Test
    void testVerifyEmptyMsgReturns400() throws Exception {
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"\",\"signature\":\"abc\"}");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_MSG);
    }

    @Test
    void testVerifyEmptySignatureReturns400() throws Exception {
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":\"hello\",\"signature\":\"\"}");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_MSG);
    }

    @Test
    void testVerifyInvalidJsonReturns400() throws Exception {
        HttpResponse<String> response = postJson(VERIFY_PATH, "{\"msg\":");

        assertEquals(400, response.statusCode(), "status should be 400");
        assertError(response.body(), ApiErrorCodes.INVALID_JSON);
    }

    @Test
    void testVerifyUnsupportedMediaTypeReturns415() throws Exception {
        HttpResponse<String> response = postRaw(VERIFY_PATH, "{\"msg\":\"hello\",\"signature\":\"abc\"}", "text/plain");

        assertEquals(415, response.statusCode(), "status should be 415");
        assertError(response.body(), ApiErrorCodes.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void testVerifyMethodNotAllowedReturns405() throws Exception {
        HttpResponse<String> response = get(VERIFY_PATH);

        assertEquals(405, response.statusCode(), "status should be 405");
        assertError(response.body(), ApiErrorCodes.METHOD_NOT_ALLOWED);
    }

    @Test
    void testVerifyTooLargeMsgReturns413() throws Exception {
        String tooLargeMsg = "a".repeat(MAX_BODY_SIZE_BYTES + 1);
        String signature = signatureService.sign("hello");
        String body = "{\"msg\":\"" + tooLargeMsg + "\",\"signature\":\"" + signature + "\"}";
        HttpResponse<String> response = postJson(VERIFY_PATH, body);

        assertEquals(413, response.statusCode(), "status should be 413");
        assertError(response.body(), ApiErrorCodes.PAYLOAD_TOO_LARGE);
    }

    @Test
    void testVerifyTooLargePayloadReturns413() throws Exception {
        String largeBody = "a".repeat(MAX_BODY_SIZE_BYTES + 1);
        HttpResponse<String> response = postJson(VERIFY_PATH, largeBody);

        assertEquals(413, response.statusCode(), "status should be 413");
        assertError(response.body(), ApiErrorCodes.PAYLOAD_TOO_LARGE);
    }

    private String changeBase64UrlLastChar(String base64Url) {
        if (base64Url == null || base64Url.isEmpty()) {
            return base64Url;
        }
        char last = base64Url.charAt(base64Url.length() - 1);
        char replacement = last == 'A' ? 'B' : 'A';
        return base64Url.substring(0, base64Url.length() - 1) + replacement;
    }
}