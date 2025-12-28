package ru.yandex.practicum.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    private final String algorithm = "HmacSHA256";
    private final byte[] secretKey = new byte[] {1, 2, 3};
    private final int listenPort = 8080;
    private final int maxMsgSizeBytes = 1024;

    @Test
    void testCreatesValidConfig() {
        AppConfig config = new AppConfig(
                algorithm,
                secretKey,
                listenPort,
                maxMsgSizeBytes
        );

        assertEquals(algorithm, config.getHmacAlg(), "hmac algorithm should match input value");
        assertEquals(listenPort, config.getListenPort(), "listen port should match input value");
        assertEquals(maxMsgSizeBytes, config.getMaxMsgSizeBytes(), "max message size should match input value");
        assertArrayEquals(secretKey, config.getSecretKey(), "secret key bytes should match input value");
    }

    @Test
    void testSecretKeyIsClonedOnGet() {
        AppConfig config = new AppConfig(
                algorithm,
                secretKey,
                listenPort,
                maxMsgSizeBytes
        );

        byte[] fromConfig = config.getSecretKey();
        fromConfig[0] = 9;

        assertNotEquals(fromConfig[0], config.getSecretKey()[0], "secret key should be cloned and protected from modification");
    }
}