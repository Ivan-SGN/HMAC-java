package ru.yandex.practicum.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.api.handlers.SignatureHandler;
import ru.yandex.practicum.api.handlers.VerifyHandler;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.service.HmacSignatureService;
import ru.yandex.practicum.service.SignatureService;
import ru.yandex.practicum.util.GsonConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

public abstract class ServerHMACTest {

    protected static final String HMAC_ALG = "HmacSHA256";
    protected static final String SECRET_TEXT = "test-secret-1234";
    protected static final int LISTEN_PORT = 8080;
    protected static final int MAX_BODY_SIZE_BYTES = 1048576;

    protected HttpServer httpServer;
    protected HttpClient httpClient;
    protected Gson gson;
    protected String baseUrl;
    protected AppConfig config;
    protected SignatureService signatureService;

    @BeforeEach
    void setUp() throws IOException {
        gson = GsonConfig.createGson();

        byte[] secretKey = SECRET_TEXT.getBytes(StandardCharsets.UTF_8);
        config = new AppConfig(HMAC_ALG, secretKey, LISTEN_PORT, MAX_BODY_SIZE_BYTES);
        signatureService = new HmacSignatureService(config.getHmacAlg(), config.getSecretKey());

        httpServer = HttpServer.create(new InetSocketAddress(0), 0);
        httpServer.createContext("/sign", new SignatureHandler(signatureService, gson, config));
        httpServer.createContext("/verify", new VerifyHandler(signatureService, gson, config));
        httpServer.start();

        int port = httpServer.getAddress().getPort();
        baseUrl = "http://localhost:" + port;

        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
