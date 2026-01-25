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
 * datastar.enable-logging=false
 * </pre>
 *
 * @param maxConcurrentConnections the maximum number of concurrent SSE connections allowed (default: 1000)
 * @param enableLogging            whether to enable logging (default: false)
 * @author Akash Gill
 */
@Validated
@ConfigurationProperties(prefix = "datastar")
public record DatastarProperties(
        @DefaultValue("1000")
        int maxConcurrentConnections,

        @DefaultValue("false")
        boolean enableLogging
) {
}
