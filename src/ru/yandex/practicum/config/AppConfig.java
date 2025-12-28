package ru.yandex.practicum.config;

public class AppConfig {

    private final String hmacAlg;
    private final byte[] secretKey;
    private final int listenPort;
    private final int maxMsgSizeBytes;

    public AppConfig(String hmacAlg, byte[] secretKey, int listenPort, int maxMsgSizeBytes) {
        if (hmacAlg == null || hmacAlg.isBlank()) {
            throw new IllegalArgumentException("hmacAlg is blank");
        }
        if (secretKey == null || secretKey.length == 0) {
            throw new IllegalArgumentException("secretKey is empty");
        }
        if (listenPort <= 0 || listenPort > 65535) {
            throw new IllegalArgumentException("listenPort is invalid");
        }
        if (maxMsgSizeBytes <= 0) {
            throw new IllegalArgumentException("maxMsgSizeBytes is invalid");
        }

        this.hmacAlg = hmacAlg;
        this.secretKey = secretKey.clone();
        this.listenPort = listenPort;
        this.maxMsgSizeBytes = maxMsgSizeBytes;
    }

    public String getHmacAlg() {
        return hmacAlg;
    }

    public byte[] getSecretKey() {
        return secretKey.clone();
    }

    public int getListenPort() {
        return listenPort;
    }

    public int getMaxMsgSizeBytes() {
        return maxMsgSizeBytes;
    }
}