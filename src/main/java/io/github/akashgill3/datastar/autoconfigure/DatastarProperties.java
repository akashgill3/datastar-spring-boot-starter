package io.github.akashgill3.datastar.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;


/**
 * Configuration properties for Datastar server-sent event (SSE) functionality.
 * <p>
 * This record holds all configurable properties for Datastar behavior, bound from
 * application configuration using the {@code datastar} prefix. Properties can be
 * configured in {@code application.properties} or {@code application.yml}.
 * <p>
 * Example configuration:
 * <pre>
 * datastar.max-concurrent-connections=1000
 * datastar.debug-logging=false
 * </pre>
 *
 * @param maxConcurrentConnections the maximum number of concurrent SSE connections allowed (default: 1000)
 * @param debugLogging             whether to enable debug logging for SSE connection lifecycle events (default: false)
 * @author Akash Gill
 */
@Validated
@ConfigurationProperties(prefix = "datastar")
public record DatastarProperties(
        @DefaultValue(value = "1000")
        Integer maxConcurrentConnections,

        @DefaultValue(value = "false")
        boolean debugLogging
) {
}
