package io.github.akashgill3.datastar.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.akashgill3.datastar.Datastar;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 *
 * <p>A single bean factory method is provided:
 *
 * <ul>
 *   <li>{@link #datastar(DatastarProperties, ObjectMapper)} - creates Datastar with ObjectMapper
 * </ul>
 *
 * <p>The {@link ObjectMapper} dependency is optional. If not available on the classpath, a warning
 * is logged and {@link Datastar#readSignals(HttpServletRequest, Class)}} will throw {@link
 * UnsupportedOperationException}. Add {@code jackson-databind} to enable signal reading.
 *
 * <p>To disable this autoconfiguration, exclude it in your Spring Boot application:
 *
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
   * Creates the {@link Datastar} bean with optional ObjectMapper support.
   *
   * <p>This bean is only created if no other {@link Datastar} bean is already defined in the
   * application context. The {@link ObjectMapper} is optional and may be null if not available on
   * the classpath.
   *
   * @param properties the Datastar configuration properties
   * @param objectMapper the ObjectMapper for JSON operations, nullable
   * @return configured Datastar instance
   */
  @Bean
  @ConditionalOnMissingBean
  public Datastar datastar(
      DatastarProperties properties, @Autowired(required = false) ObjectMapper objectMapper) {

    if (objectMapper == null) {
      log.warn(
          "ObjectMapper not available on classpath. Datastar.readSignals() will throw UnsupportedOperationException. "
              + "Add jackson-databind to enable signal reading.");
    }

    log.info(
        "Configuring Datastar with Object with maxConcurrentConnections: {}, enableLogging: {}",
        properties.maxConcurrentConnections(),
        properties.enableLogging());
    return new Datastar(properties, objectMapper);
  }
}
