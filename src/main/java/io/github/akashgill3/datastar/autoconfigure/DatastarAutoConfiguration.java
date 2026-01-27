package io.github.akashgill3.datastar.autoconfigure;

import io.github.akashgill3.datastar.Datastar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Datastar.
 *
 * <p>This configuration automatically creates a {@link Datastar} bean when Spring Boot detects the
 * Datastar library on the classpath. The bean is configured using properties defined in {@link
 * DatastarProperties}.
 *
 * <p>The configuration can be customized via application properties with the prefix {@code
 * datastar}, including max concurrent connections, and logging flag.
 * <p>
 * To disable this autoconfiguration, exclude it in your Spring Boot application:
 * <pre>
 * {@code @SpringBootApplication(exclude = DatastarAutoConfiguration.class)}
 * </pre>
 *
 * @author Akash Gill
 */
@AutoConfiguration
@EnableConfigurationProperties(DatastarProperties.class)
public class DatastarAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(DatastarAutoConfiguration.class);

  /**
   * Creates the {@link Datastar} bean with the provided configuration properties.
   * <p>
   * This bean is only created if no other {@link Datastar} bean is already defined
   * in the application context.
   *
   * @param properties the Datastar configuration properties
   * @return configured Datastar instance
   */
  @Bean
  @ConditionalOnMissingBean
  public Datastar datastar(DatastarProperties properties) {
    log.info("Configuring Datastar with maxConcurrentConnections: {}, enableLogging: {}",
        properties.maxConcurrentConnections(), properties.enableLogging());
    return new Datastar(properties);
  }
}
