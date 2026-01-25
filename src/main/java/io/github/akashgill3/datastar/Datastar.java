package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Datastar {

    private static final Logger log = LoggerFactory.getLogger(Datastar.class);

    private final DatastarProperties properties;

    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public Datastar(DatastarProperties properties) {
        this.properties = properties;
    }

    public DatastarSseEmitter createEmitter() {
        int current = activeConnections.incrementAndGet();
        if (current > properties.maxConcurrentConnections()) {
            activeConnections.decrementAndGet();
            throw new IllegalStateException(
                    "Maximum concurrent connections reached: " + properties.maxConcurrentConnections()
            );
        }

        DatastarSseEmitter emitter = new DatastarSseEmitter(properties);

        emitter.onCompletion(() -> {
            activeConnections.decrementAndGet();
            if (properties.debugLogging()) {
                log.debug("SSE connection completed. Active: {}", activeConnections.get());
            }
        });

        emitter.onTimeout(() -> {
            activeConnections.decrementAndGet();
            log.warn("SSE connection timeout. Active: {}", activeConnections.get());
        });

        emitter.onError(throwable -> {
            activeConnections.decrementAndGet();
            log.error("SSE connection error. Active: {}", activeConnections.get(), throwable);
        });

        if (properties.debugLogging()) {
            log.debug("Created new SSE emitter. Active connections: {}", activeConnections.get());
        }

        return emitter;
    }
}
