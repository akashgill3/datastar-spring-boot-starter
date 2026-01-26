package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages server-sent event (SSE) emitters and enforces concurrent connection limits.
 * <p>
 * This class is automatically configured via Spring Boot autoconfiguration when
 * datastar-spring-boot-starter is on the classpath. Configuration properties are
 * bound from {@link DatastarProperties} using the {@code datastar} prefix.
 * <p>
 * The primary responsibility is to safely create {@link DatastarSseEmitter} instances
 * while enforcing the maximum concurrent connection limit defined in configuration. When
 * the limit is exceeded, an {@link IllegalStateException} is thrown.
 * <p>
 * Each emitter is automatically registered with lifecycle callbacks to track active
 * connections and log events (if debug logging is enabled).
 * <p>
 * Typical usage:
 * <pre>
 * {@code
 * @RestController
 * public class MyController {
 *     private final Datastar datastar;
 *     private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
 *
 *     public MyController(Datastar datastar) {
 *         this.datastar = datastar;
 *     }
 *
 *     @GetMapping("/sse")
 *     public DatastarSseEmitter handle() {
 *         DatastarSseEmitter sseEmitter = datastar.createEmitter();
 *
 *         // Usually ran using executor for async processing
 *         executor.execute(() -> {
 *             try {
 *                 sseEmitter.patchElements("<div>Hello</div>");
 *                 sseEmitter.complete();
 *             } catch (Exception e) {
 *                 sseEmitter.completeWithError(e);
 *             }
 *         });
 *
 *         return sseEmitter;
 *     }
 * }
 * }
 * </pre>
 *
 * @author Akash Gill
 */
public class Datastar {

    private static final Logger log = LoggerFactory.getLogger(Datastar.class);

    /**
     * Configuration properties for Datastar functionality.
     */
    private final DatastarProperties properties;

    /**
     * Thread-safe counter tracking the number of currently active SSE connections.
     */
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    /**
     * Constructs a new Datastar instance with the specified configuration properties.
     *
     * @param properties the configuration properties for Datastar
     */
    public Datastar(DatastarProperties properties) {
        this.properties = properties;
    }

    /**
     * Creates a new {@link DatastarSseEmitter} instance with connection tracking and lifecycle callbacks.
     * <p>
     * This method atomically increments the active connection count and validates it against
     * the maximum concurrent connection limit specified in the configuration. If the limit
     * is exceeded, the connection count is decremented and an {@link IllegalStateException}
     * is thrown.
     * <p>
     * The returned emitter is automatically configured with:
     * <ul>
     *   <li>Completion callback - decrements active connections and logs if debug logging is enabled</li>
     *   <li>Timeout callback - decrements active connections and logs a warning</li>
     *   <li>Error callback - decrements active connections and logs the error with stack trace</li>
     * </ul>
     * <p>
     * Thread-safe for concurrent invocations.
     *
     * @return a new {@link DatastarSseEmitter} instance ready for use
     * @throws IllegalStateException if the maximum concurrent connection limit has been reached
     */
    public DatastarSseEmitter createEmitter() {
        int current = activeConnections.incrementAndGet();
        if (current > properties.maxConcurrentConnections()) {
            activeConnections.decrementAndGet();
            throw new IllegalStateException(
                    "Maximum concurrent connections reached: " + properties.maxConcurrentConnections()
            );
        }

        DatastarSseEmitter emitter = new DatastarSseEmitter(properties);

        AtomicBoolean decremented = new AtomicBoolean(false);
        Runnable decrementOnce = () -> {
            if (decremented.compareAndSet(false, true)) {
                activeConnections.decrementAndGet();
            }
        };

        emitter.onCompletion(() -> {
            decrementOnce.run();
            if (properties.enableLogging() && log.isDebugEnabled()) {
                log.debug("SSE connection completed. Active connections: {}", activeConnections.get());
            }
        });

        emitter.onTimeout(() -> {
            decrementOnce.run();
            if (properties.enableLogging() && log.isDebugEnabled()) {
                log.debug("SSE connection timed out. Active connections: {} (If unexpected, review server/proxy idle timeouts or send keep-alives.)", activeConnections.get());
            }
        });

        emitter.onError(throwable -> {
            decrementOnce.run();
            if (properties.enableLogging() && log.isDebugEnabled()) {
                log.debug("SSE connection error. Active: {}", activeConnections.get(), throwable);
            }
        });

        if (properties.enableLogging() && log.isDebugEnabled()) {
            log.debug("Created new SSE emitter. Active connections: {}", activeConnections.get());
        }

        return emitter;
    }
}