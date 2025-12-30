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
    private static final Path DEFAULT_CONFIG_PATH = Path.of("config.json");
    private final HttpServer httpServer;
    private final AppLogger log;

    public ServerHMAC() throws IOException {
        this(DEFAULT_CONFIG_PATH);
    }

    public ServerHMAC(Path configPath) throws IOException {
        Gson gson = GsonConfig.createGson();
        ConfigLoader configLoader = new ConfigLoader(gson);
        AppConfig config = configLoader.load(configPath);
        SignatureService signatureService = new HmacSignatureService(
                config.getHmacAlg(),
                config.getSecretKey()
        );
        this.log = AppLogger.forClass(getClass());
        this.httpServer = HttpServer.create(new InetSocketAddress(config.getListenPort()), 0);
        httpServer.createContext("/sign", new SignatureHandler(signatureService, gson, config));
        httpServer.createContext("/verify", new VerifyHandler(signatureService, gson, config));
    }

    public static void main(String[] args) {
        AppLogger log = AppLogger.forClass(ServerHMAC.class);
        Path configPath = resolveConfigPath(args);
        try {
            ServerHMAC serverHMAC = new ServerHMAC(configPath);
            serverHMAC.start();
        } catch (Exception exception) {
            log.logServerStartFailed(exception);
        }
    }

    public void start() {
        httpServer.start();
        log.logServerStarted(httpServer.getAddress());
    }

    public void stop() {
        httpServer.stop(0);
        log.logServerStopped();
    }

    private static Path resolveConfigPath(String[] args) {
        if (args == null || args.length == 0) {
            return DEFAULT_CONFIG_PATH;
        }
        String value = args[0];
        if (value == null) {
            return DEFAULT_CONFIG_PATH;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return DEFAULT_CONFIG_PATH;
        }
        return Path.of(value);
    }
}