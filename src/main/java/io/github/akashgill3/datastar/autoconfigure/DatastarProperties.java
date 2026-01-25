package io.github.akashgill3.datastar.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "datastar")
public record DatastarProperties(
        @DefaultValue(value = "300")
        Integer timeout,

        @DefaultValue(value = "1000")
        Integer maxConcurrentConnections,

        @DefaultValue(value = "false")
        boolean debugLogging
) {
}
