package ru.yandex.practicum.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.api.handlers.SignatureHandler;
import ru.yandex.practicum.api.handlers.VerifyHandler;
import ru.yandex.practicum.util.GsonConfig;
import ru.yandex.practicum.config.AppConfig;
import ru.yandex.practicum.config.ConfigLoader;
import ru.yandex.practicum.service.HmacSignatureService;
import ru.yandex.practicum.service.SignatureService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

public class ServerHMAC {

    private final AppConfig config;
    private final Gson gson;
    private final SignatureService signatureService;
    private final HttpServer httpServer;

    public ServerHMAC() throws IOException {
        this.gson = GsonConfig.createGson();
        ConfigLoader configLoader = new ConfigLoader(gson);
        this.config = configLoader.load(Path.of("config.json"));

        this.signatureService = new HmacSignatureService(
                config.getHmacAlg(),
                config.getSecretKey()
        );

        this.httpServer = HttpServer.create(new InetSocketAddress(config.getListenPort()), 0);
        httpServer.createContext("/sign", new SignatureHandler(signatureService, gson));
        httpServer.createContext("/verify", new VerifyHandler(signatureService, gson));
    }

    public void start() {
        httpServer.start();
        System.out.println("server starts "
                + "ip: " + httpServer.getAddress().getAddress() + " "
                + "port: " + httpServer.getAddress().getPort());
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static void main(String[] args) {
        try {
            ServerHMAC serverHMAC = new ServerHMAC();
            serverHMAC.start();
        } catch (IOException exception) {
            System.out.println("failed to start http server: " + exception.getMessage());
        }
    }
}