package ru.yandex.practicum.config;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import ru.yandex.practicum.exceptions.ConfigException;
import ru.yandex.practicum.util.Base64Codec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {

    private static class RawConfig {
        private String hmacAlg;
        private String secret;
        private int listenPort;
        private int maxMsgSizeBytes;
    }

    private final Gson gson;

    public ConfigLoader(Gson gson) {
        this.gson = gson;
    }

    public AppConfig load(Path configPath) {
        try {
            String json = Files.readString(configPath, StandardCharsets.UTF_8);
            RawConfig rawConfig = gson.fromJson(json, RawConfig.class);
            validate(rawConfig);
            byte[] secretKey = Base64Codec.decodeBase64(rawConfig.secret);
            return new AppConfig(
                    rawConfig.hmacAlg,
                    secretKey,
                    rawConfig.listenPort,
                    rawConfig.maxMsgSizeBytes
            );
        } catch (JsonParseException exception) {
            throw new ConfigException("invalid json", exception);
        } catch (IOException exception) {
            throw new ConfigException("unable to read config file", exception);
        } catch (IllegalArgumentException exception) {
            throw new ConfigException(exception.getMessage(), exception);
        }
    }
    private void validate(RawConfig rawConfig) {
        if (rawConfig == null) {
            throw new ConfigException("config is empty");
        }
        if (rawConfig.secret == null || rawConfig.secret.isBlank()) {
            throw new ConfigException("secret is blank");
        }
    }
}