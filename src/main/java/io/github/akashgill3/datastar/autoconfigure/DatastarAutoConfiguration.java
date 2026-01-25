package io.github.akashgill3.datastar.autoconfigure;

import io.github.akashgill3.datastar.Datastar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(DatastarProperties.class)
public class DatastarAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DatastarAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public Datastar datastar(DatastarProperties properties) {
        log.info("Configuring Datastar with timeout: {}, maxConcurrentConnections: {}, debugLogging: {}",
                properties.timeout(), properties.maxConcurrentConnections(), properties.debugLogging());
        return new Datastar(properties);
    }

}
