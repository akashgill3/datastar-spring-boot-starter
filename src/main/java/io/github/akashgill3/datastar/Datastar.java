package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

/**
 * Manages server-sent event (SSE) emitters.
 *
 * <p>This class is automatically configured via Spring Boot autoconfiguration when
 * datastar-spring-boot-starter is on the classpath. Configuration properties are bound from {@link
 * DatastarProperties} using the {@code datastar} prefix.
 *
 * <p>The primary responsibility is to create {@link DatastarSseEmitter} instances and provide
 * helper methods such as {@link #readSignals(HttpServletRequest, Class)}.
 *
 * <p>Typical usage:
 *
 * <pre>{@code
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
 * }</pre>
 *
 * @author Akash Gill
 */
public class Datastar {
  private static final Logger log = LoggerFactory.getLogger(Datastar.class);

  /** Configuration properties for Datastar functionality. */
  private final DatastarProperties properties;

  /** ObjectMapper for JSON (un)marshalling. */
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Constructs a new Datastar instance with the specified configuration.
   *
   * @param properties the configuration properties for Datastar
   */
  public Datastar(DatastarProperties properties) {
    this.properties = properties;
  }

  /**
   * Creates a new {@link DatastarSseEmitter} instance.
   *
   * @return a new {@link DatastarSseEmitter} instance ready for use
   */
  public DatastarSseEmitter createEmitter() {
    DatastarSseEmitter emitter = new DatastarSseEmitter(properties);

    if (properties.enableLogging() && log.isDebugEnabled()) {
      log.debug("Created new SSE emitter");
    }

    return emitter;
  }

  /**
   * Parses incoming signals from the HTTP request into the specified target object.
   *
   * <p>For {@code GET} requests, signals are extracted from the {@code datastar} query parameter
   * (URL-encoded JSON). For other request methods, signals are read directly from the request body
   * (JSON).
   *
   * @param request the current HTTP request
   * @param target the class of the object to unmarshal signals into
   * @param <T> the type of the target object
   * @return the unmarshalled signal object
   * @throws IOException if signals cannot be read or parsed
   */
  public <T> T readSignals(HttpServletRequest request, Class<T> target) throws IOException {
    if (objectMapper == null) {
      throw new UnsupportedOperationException(
          "readSignals() requires ObjectMapper. Add jackson-databind to classpath to enable this feature.");
    }

    if ("GET".equalsIgnoreCase(request.getMethod())) {
      String datastarParam = request.getParameter(Consts.DATASTAR_KEY);
      if (datastarParam == null || datastarParam.isBlank()) {
        return objectMapper.readValue("{}", target);
      }
      String decodedJson = URLDecoder.decode(datastarParam, StandardCharsets.UTF_8);
      return objectMapper.readValue(decodedJson, target);
    } else {
      return objectMapper.readValue(request.getInputStream(), target);
    }
  }
}
