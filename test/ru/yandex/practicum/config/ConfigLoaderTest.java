package ru.yandex.practicum.config;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.ConfigException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    @TempDir
    Path tempDir;

    private final Gson gson = new Gson();
    private final ConfigLoader loader = new ConfigLoader(gson);

    @Test
    void testLoadsValidConfig() throws IOException {
        Path configPath = writeConfig("""
                {
                  "hmacAlg": "HmacSHA256",
                  "secret": "c2VjcmV0LWtleS0xMjM0",
                  "listenPort": 8080,
                  "maxMsgSizeBytes": 1048576
                }
                """);

        AppConfig config = loader.load(configPath);

        assertEquals("HmacSHA256", config.getHmacAlg(), "hmac algorithm should be loaded from config");
        assertEquals(8080, config.getListenPort(), "listen port should be loaded from config");
        assertEquals(1048576, config.getMaxMsgSizeBytes(), "max message size should be loaded from config");
        assertNotNull(config.getSecretKey(), "secret key should be decoded and not null");
        assertTrue(config.getSecretKey().length > 0, "secret key should not be empty");
    }

    @Test
    void testThrowsInvalidJson() throws IOException {
        Path configPath = writeConfig("{");

        ConfigException exception = assertThrows(ConfigException.class, () -> loader.load(configPath),
                "invalid json should cause config loader to throw config exception");

        assertEquals("invalid json", exception.getMessage(), "invalid json should produce a clear config error");
    }

    @Test
    void testThrowsUnableToReadConfigFile() {
        Path configPath = tempDir.resolve("missing.json");

        ConfigException exception = assertThrows(ConfigException.class, () -> loader.load(configPath),
                "missing file should cause config loader to throw config exception");

        assertEquals("unable to read config file", exception.getMessage(), "missing file should produce a clear config error");
    }

    @Test
    void testThrowsSecretIsBlank() throws IOException {
        Path configPath = writeConfig("""
                {
                  "hmacAlg": "HmacSHA256",
                  "secret": "",
                  "listenPort": 8080,
                  "maxMsgSizeBytes": 1048576
                }
                """);

        ConfigException exception = assertThrows(ConfigException.class, () -> loader.load(configPath),
                "blank secret should cause config loader to throw config exception");

        assertEquals("secret is blank", exception.getMessage(), "blank secret should be rejected");
    }

    @Test
    void testThrowsConfigIsEmpty() throws IOException {
        Path configPath = writeConfig("null");

        ConfigException exception = assertThrows(ConfigException.class, () -> loader.load(configPath),
                "null json should cause config loader to throw config exception");

        assertEquals("config is empty", exception.getMessage(), "null json should be treated as empty config");
    }

    private Path writeConfig(String content) throws IOException {
        Path path = tempDir.resolve("config.json");
        Files.writeString(path, content, StandardCharsets.UTF_8);
        return path;
    }
}