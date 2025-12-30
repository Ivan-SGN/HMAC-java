package ru.yandex.practicum.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.api.handlers.SignatureHandler;
import ru.yandex.practicum.api.handlers.VerifyHandler;
import ru.yandex.practicum.api.logging.AppLogger;
import ru.yandex.practicum.util.GsonConfig;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.config.ConfigLoader;
import ru.yandex.practicum.service.HmacSignatureService;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class ServerHMAC {
    private final Path CONFIG_PATH = Path.of("config.json");
    private final HttpServer httpServer;
    private final AppLogger log;

    public ServerHMAC() throws IOException {
        Gson gson = GsonConfig.createGson();
        ConfigLoader configLoader = new ConfigLoader(gson);
        AppConfig config = configLoader.load(CONFIG_PATH);
        SignatureService signatureService = new HmacSignatureService(
                config.getHmacAlg(),
                config.getSecretKey()
        );
        this.log = AppLogger.forClass(getClass());
        this.httpServer = HttpServer.create(new InetSocketAddress(config.getListenPort()), 0);
        httpServer.createContext("/sign", new SignatureHandler(signatureService, gson, config));
        httpServer.createContext("/verify", new VerifyHandler(signatureService, gson, config));
    }

    public void start() {
        httpServer.start();
        log.logServerStarted(httpServer.getAddress());
    }

    public void stop() {
        httpServer.stop(0);
        log.logServerStopped();
    }

    public static void main(String[] args) {
        AppLogger log = AppLogger.forClass(ServerHMAC.class);
        try {
            ServerHMAC serverHMAC = new ServerHMAC();
            serverHMAC.start();
        } catch (Exception exception) {
            log.logServerStartFailed(exception);
        }
    }
}