package ru.yandex.practicum.api.logging;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;

public final class AppLogger {

    private final Logger log;
    private final String prefix;

    private AppLogger(Class<?> handlerClass) {
        this.log = LoggerFactory.getLogger(handlerClass);
        this.prefix = "[" + handlerClass.getSimpleName() + "]";
    }

    private enum Level {
        INFO, WARN, ERROR;

        private static Level byStatus(int statusCode) {
            if (statusCode >= 500) {
                return ERROR;
            }
            if (statusCode >= 400) {
                return WARN;
            }
            return INFO;
        }
    }

    public static AppLogger forClass(Class<?> ownerClass) {
        return new AppLogger(ownerClass);
    }

    public void logResponse(HttpExchange exchange, int statusCode) {
        logResponseInternal(exchange, statusCode, null);
    }

    public void logError(HttpExchange exchange, int statusCode, String errorCode) {
        logResponseInternal(exchange, statusCode, errorCode);
    }

    private void logResponseInternal(HttpExchange exchange, int statusCode, String errorCode) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        Level level = Level.byStatus(statusCode);

        switch (level) {
            case INFO -> log.info("{} {} {} -> {}", prefix, method, path, statusCode);
            case WARN -> log.warn("{} {} {} -> {} ({})", prefix, method, path, statusCode, errorCode);
            case ERROR -> log.error("{} {} {} -> {}", prefix, method, path, statusCode);
        }
    }

    public void logServerStarted(InetSocketAddress address) {
        String host = address == null ? "-" : address.getHostString();
        int port = address == null ? -1 : address.getPort();
        log.info("{} server started host={} port={}", prefix, host, port);
    }

    public void logServerStopped() {
        log.info("{} server stopped", prefix);
    }

    public void logServerStartFailed(Throwable exception) {
        log.error("{} server start failed: {}", prefix, safeMessage(exception), exception);
    }

    private String safeMessage(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return throwable.getClass().getSimpleName();
        }
        return throwable.getClass().getSimpleName() + ": " + message;
    }
}