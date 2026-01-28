package io.github.akashgill3.datastar;

import static org.springframework.http.MediaType.TEXT_PLAIN;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import io.github.akashgill3.datastar.events.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * SSE emitter for sending Datastar events to the browser.
 *
 * <p>Supports the following Datastar event types:
 *
 * <ul>
 *   <li>{@link #patchElements(String)} - Patch DOM elements
 *   <li>{@link #patchSignals(String)} - Patch signal store
 * </ul>
 *
 * <p>Also provides convenience methods:
 *
 * <ul>
 *   <li>{@link #executeScript(String)} - Execute JavaScript in the browser
 *   <li>{@link #consoleLog(String)} - Log to browser console
 *   <li>{@link #consoleError(String)} - Log error to browser console
 *   <li>{@link #redirect(String)} - Redirect browser
 *   <li>{@link #replaceUrl(String)} - Replace url in the browser
 * </ul>
 *
 * @author Akash Gill
 */
public class DatastarSseEmitter extends ResponseBodyEmitter {

  private static final Logger log = LoggerFactory.getLogger(DatastarSseEmitter.class);

  private final DatastarProperties properties;

  public DatastarSseEmitter(DatastarProperties properties) {
    super();
    this.properties = properties;
  }

  // ========================================================================
  // Core Datastar Events
  // ========================================================================

  /**
   * Send a patch elements event to update the DOM.
   *
   * @param elements the HTML elements to patch
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter patchElements(String elements) throws IOException {
    return patchElements(elements, options -> {});
  }

  /**
   * Send a patch elements event to update the DOM.
   *
   * @param elements the HTML elements to patch
   * @param options the patch options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter patchElements(String elements, Consumer<PatchElementOptions> options)
      throws IOException {
    PatchElementConfig opts = PatchElementConfig.from(options);
    super.send(formatPatchElementsEvent(elements, opts), TEXT_PLAIN);
    return this;
  }

  /**
   * Send a patch signals event to update the signal store.
   *
   * @param signals the JSON signals to patch
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter patchSignals(String signals) throws IOException {
    return patchSignals(signals, options -> {});
  }

  /**
   * Send a patch signals event to update the signal store.
   *
   * @param signals the JSON signals to patch
   * @param config the patch options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter patchSignals(String signals, Consumer<PatchSignalOptions> config)
      throws IOException {
    PatchSignalConfig opts = PatchSignalConfig.from(config);
    super.send(formatPatchSignalsEvent(signals, opts), TEXT_PLAIN);
    return this;
  }

  // ========================================================================
  // Execute Script - Convenience Methods
  // ========================================================================

  /**
   * Execute a JavaScript script in the browser with default options.
   *
   * <p>This is a convenience method that internally creates a {@code <script>} tag and sends it via
   * {@link #patchElements(String)}. It is not a separate Datastar event type.
   *
   * @param script the JavaScript code to execute
   * @return this emitter
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter executeScript(String script) throws IOException {
    return executeScript(script, options -> {});
  }

  /**
   * Execute a JavaScript script in the browser with custom options.
   *
   * @param script the JavaScript code to execute
   * @param options configuration for script execution
   * @return this emitter
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter executeScript(String script, Consumer<ExecuteScriptOptions> options)
      throws IOException {
    ExecuteScriptConfig opts = ExecuteScriptConfig.from(options);
    String element = buildScriptElement(script, opts.autoRemove(), opts.attributes());

    Consumer<PatchElementOptions> patchElementOptionsConsumer =
        patchElementOptions -> {
          patchElementOptions.selector("body").mode(ElementPatchMode.Append);
          if (opts.eventId() != null && !opts.eventId().isEmpty()) {
            patchElementOptions.eventId(opts.eventId());
          }
          if (opts.retryDuration() != null
              && opts.retryDuration() != Consts.DEFAULT_SSE_RETRY_DURATION_MS) {
            patchElementOptions.retryDuration(opts.retryDuration());
          }
        };

    return patchElements(element, patchElementOptionsConsumer);
  }

  // ========================================================================
  // Console Logging - Convenience Methods
  // ========================================================================

  /**
   * Log a message to the browser console.
   *
   * <p>Equivalent to calling {@code console.log(message)} in the browser.
   *
   * @param message the message to log
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter consoleLog(String message) throws IOException {
    return consoleLog(message, options -> {});
  }

  /**
   * Log a message to the browser console with custom options.
   *
   * @param message the message to log
   * @param options execution options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter consoleLog(String message, Consumer<ExecuteScriptOptions> options)
      throws IOException {
    String script = "console.log(" + toJsString(message) + ")";
    return executeScript(script, options);
  }

  /**
   * Log an error to the browser console.
   *
   * <p>Equivalent to calling {@code console.error(message)} in the browser.
   *
   * @param message the error message to log
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter consoleError(String message) throws IOException {
    return consoleError(message, options -> {});
  }

  /**
   * Log an error to the browser console with custom options.
   *
   * @param message the error message to log
   * @param options execution options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter consoleError(String message, Consumer<ExecuteScriptOptions> options)
      throws IOException {
    String script = "console.error(" + toJsString(message) + ")";
    return executeScript(script, options);
  }

  // ========================================================================
  // Browser Navigation - Convenience Methods
  // ========================================================================

  /**
   * Redirect the browser to a new URL.
   *
   * <p>Uses {@code setTimeout} to allow current event processing to complete before redirecting.
   *
   * @param url the URL to redirect to
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter redirect(String url) throws IOException {
    return redirect(url, options -> {});
  }

  /**
   * Redirect the browser to a new URL with custom options.
   *
   * @param url the URL to redirect to
   * @param options execution options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter redirect(String url, Consumer<ExecuteScriptOptions> options)
      throws IOException {
    String script = "setTimeout(() => window.location.href = " + toJsString(url) + ")";
    return executeScript(script, options);
  }

  /**
   * Replace the current URL in the browser's history without reloading.
   *
   * <p>Uses {@code window.history.replaceState} to update the URL bar.
   *
   * @param url the new URL
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter replaceUrl(String url) throws IOException {
    return replaceUrl(url, options -> {});
  }

  /**
   * Replace the current URL in the browser's history with custom options.
   *
   * @param url the new URL
   * @param options execution options
   * @return this emitter for method chaining
   * @throws IOException if an I/O error occurs
   */
  public DatastarSseEmitter replaceUrl(String url, Consumer<ExecuteScriptOptions> options)
      throws IOException {
    String script =
        "setTimeout(() => window.history.replaceState({}, '', " + toJsString(url) + "))";
    return executeScript(script, options);
  }

  // ========================================================================
  // Response Configuration
  // ========================================================================

  @Override
  protected void extendResponse(@NonNull ServerHttpResponse outputMessage) {
    super.extendResponse(outputMessage);

    HttpHeaders headers = outputMessage.getHeaders();
    if (headers.getContentType() == null) {
      headers.setContentType(MediaType.TEXT_EVENT_STREAM);
    }

    headers.setCacheControl("no-cache");
  }

  // ========================================================================
  // Internal Records
  // ========================================================================

  private record PatchElementConfig(
      String eventId,
      Long retryDuration,
      String selector,
      ElementPatchMode mode,
      boolean useViewTransition,
      Namespace namespace) {
    static PatchElementConfig from(Consumer<PatchElementOptions> config) {
      PatchElementOptions opts = new PatchElementOptions();
      config.accept(opts);
      return new PatchElementConfig(
          opts.getEventId(),
          opts.getRetryDuration(),
          opts.getSelector(),
          opts.getMode(),
          opts.isUseViewTransition(),
          opts.getNamespace());
    }
  }

  private record PatchSignalConfig(String eventId, Long retryDuration, boolean onlyIfMissing) {
    static PatchSignalConfig from(Consumer<PatchSignalOptions> config) {
      PatchSignalOptions opts = new PatchSignalOptions();
      config.accept(opts);
      return new PatchSignalConfig(
          opts.getEventId(), opts.getRetryDuration(), opts.isOnlyIfMissing());
    }
  }

  private record ExecuteScriptConfig(
      String eventId, Long retryDuration, boolean autoRemove, List<String> attributes) {
    static ExecuteScriptConfig from(Consumer<ExecuteScriptOptions> config) {
      ExecuteScriptOptions opts = new ExecuteScriptOptions();
      config.accept(opts);
      return new ExecuteScriptConfig(
          opts.getEventId(), opts.getRetryDuration(), opts.getAutoRemove(), opts.getAttributes());
    }
  }

  // ========================================================================
  // Internal Formatting Methods
  // ========================================================================

  /**
   * Formats a {@link DatastarEventType#PATCH_ELEMENTS} event into SSE wire format.
   *
   * <p>Generates an SSE event with the following structure:
   *
   * <pre>
   * event: datastar-patch-elements
   * id: [eventId]                    (if specified)
   * retry: [retryDuration]           (if not default 1000ms)
   * data: selector [CSS selector]    (if specified)
   * data: mode [patch mode]          (if not default 'outer')
   * data: useViewTransition true     (if enabled)
   * data: namespace [namespace]      (if not default 'html')
   * data: elements [HTML line 1]
   * data: elements [HTML line 2]
   * ...
   * [blank line]
   * </pre>
   *
   * <p>The generated event follows the <a
   * href="https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events">
   * Server-Sent Events specification</a> and the Datastar protocol.
   *
   * <p>Only non-default values are included in the output to minimize wire size. Multi-line HTML is
   * split with each line sent as a separate {@code data: elements} line.
   *
   * @param elements the HTML to patch
   * @param options the patch options
   * @return the formatted SSE event
   * @see <a href="https://data-star.dev/reference/sse_events#datastar-patch-elements">Datastar
   *     Reference</a>
   */
  private String formatPatchElementsEvent(String elements, PatchElementConfig options) {
    int initialCapacity = 128 + (elements == null ? 0 : Math.min(elements.length(), 4096));
    StringBuilder sb = new StringBuilder(initialCapacity);

    appendLine(sb, "event", DatastarEventType.PATCH_ELEMENTS.value);

    if (options.eventId() != null) {
      appendLine(sb, "id", options.eventId());
    }
    if (options.retryDuration() != null
        && options.retryDuration != Consts.DEFAULT_SSE_RETRY_DURATION_MS) {
      appendLine(sb, "retry", options.retryDuration());
    }
    if (options.selector() != null && !options.selector().isEmpty()) {
      appendDataLine(sb, Consts.SELECTOR_DATALINE_LITERAL, options.selector().trim());
    }
    if (options.mode() != null && !options.mode().equals(Consts.DEFAULT_ELEMENT_PATCH_MODE)) {
      appendDataLine(sb, Consts.MODE_DATALINE_LITERAL, options.mode().value);
    }
    if (options.useViewTransition()) {
      appendDataLine(sb, Consts.USE_VIEW_TRANSITION_DATALINE_LITERAL, "true");
    }
    if (options.namespace() != null && !options.namespace().equals(Consts.DEFAULT_NAMESPACE)) {
      appendDataLine(sb, Consts.NAMESPACE_DATALINE_LITERAL, options.namespace().value);
    }

    if (elements != null && !elements.isEmpty()) {
      appendNonBlankDataLines(sb, Consts.ELEMENTS_DATALINE_LITERAL, elements);
    }
    sb.append("\n");

    if (properties.enableLogging() && log.isDebugEnabled()) {
      log.debug("Formatted 'datastar-patch-elements' event with length {}", sb.length());
    }

    return sb.toString();
  }

  /**
   * Formats a {@link DatastarEventType#PATCH_SIGNALS} event into SSE wire format.
   *
   * <p>Generates an SSE event with the following structure:
   *
   * <pre>
   * event: datastar-patch-signals
   * id: [eventId]                 (if specified)
   * retry: [retryDuration]        (if not default 1000ms)
   * data: onlyIfMissing true      (if enabled)
   * data: signals [JSON line 1]
   * data: signals [JSON line 2]
   * ...
   * [blank line]
   * </pre>
   *
   * Only non-default values are included in the output. Multi-line JSON is split with each line
   * sent as a separate {@code data: signals} line.
   *
   * @param signals the JSON signals to patch
   * @param options the patch options
   * @return the formatted SSE event
   * @see <a href="https://datatracker.ietf.org/doc/html/rfc7386">RFC 7386 JSON Merge Patch</a>
   */
  private String formatPatchSignalsEvent(String signals, PatchSignalConfig options) {
    int initialCapacity = 128 + (signals == null ? 0 : Math.min(signals.length(), 4096));
    StringBuilder sb = new StringBuilder(initialCapacity);

    appendLine(sb, "event", DatastarEventType.PATCH_SIGNALS.value);

    if (options.eventId() != null) {
      appendLine(sb, "id", options.eventId());
    }
    if (options.retryDuration() != null
        && !options.retryDuration().equals(Consts.DEFAULT_SSE_RETRY_DURATION_MS)) {
      appendLine(sb, "retry", options.retryDuration());
    }

    if (options.onlyIfMissing()) {
      appendDataLine(sb, Consts.ONLY_IF_MISSING_DATALINE_LITERAL, true);
    }

    if (signals != null && !signals.isEmpty()) {
      appendNonBlankDataLines(sb, Consts.SIGNALS_DATALINE_LITERAL, signals);
    }
    sb.append("\n");

    if (properties.enableLogging() && log.isDebugEnabled()) {
      log.debug(
          "Formatted 'datastar-patch-signals' event into SSE event, with length: {}", sb.length());
    }

    return sb.toString();
  }

  /**
   * Append an SSE field line to the output buffer.
   *
   * <p>The line is in the format: {@code field: value\n}
   *
   * <p>Used for SSE control fields like {@code event}, {@code id}, and {@code retry}. These fields
   * appear before any {@code data} lines and control SSE behavior.
   *
   * @param sb the string builder to append to
   * @param literal the field name (e.g., "event", "id", "retry")
   * @param value the field value
   * @see <a
   *     href="https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events">
   *     SSE Specification</a>
   */
  private void appendLine(StringBuilder sb, String literal, Object value) {
    sb.append(literal).append(": ").append(value).append('\n');
  }

  /**
   * Append a Datastar data line to the output buffer.
   *
   * <p>The line is in the format: {@code data: prefix value\n}
   *
   * <p>The prefix (literal) identifies the data type for Datastar's protocol:
   *
   * <ul>
   *   <li>{@code elements} - HTML element data
   *   <li>{@code signals} - JSON signal data
   *   <li>{@code selector} - CSS selector
   *   <li>{@code mode} - Patch mode
   *   <li>{@code namespace} - namespace
   *   <li>{@code useViewTransition} - View transition flag
   *   <li>{@code onlyIfMissing} - Conditional patch flag
   * </ul>
   *
   * <p>Multiple {@code data} lines with the same prefix can appear in a single event (e.g., for
   * multi-line HTML or JSON).
   *
   * @param sb the string builder to append to
   * @param literal the data type prefix (e.g., "elements", "signals")
   * @param value the data value
   */
  private void appendDataLine(StringBuilder sb, String literal, Object value) {
    sb.append("data: ").append(literal).append(' ').append(value).append('\n');
  }

  /**
   * Appends "data: {literal} {line}\n" for each non-blank line in {@code payload}, efficiently
   * splitting on newline boundaries without allocating substrings.
   *
   * <p>Handles both LF ({@code \n}) and CRLF ({@code \r\n}) line endings. Lines containing only
   * whitespace are skipped.
   *
   * <p>Each non-blank line is written as: {@code data: literal line\n}
   *
   * <p>
   *
   * @param sb the string builder to append to
   * @param literal the data type prefix (e.g., "elements", "signals")
   * @param payload the multi-line string to process
   */
  private void appendNonBlankDataLines(StringBuilder sb, String literal, String payload) {
    final int n = payload.length();
    int start = 0;

    for (int i = 0; i <= n; i++) {
      if (i == n || payload.charAt(i) == '\n') {
        int end = i;

        // Trim trailing '\r' (handles CRLF)
        if (end > start && payload.charAt(end - 1) == '\r') {
          end--;
        }

        if (!isBlankRange(payload, start, end)) {
          sb.append("data: ").append(literal).append(' ');
          sb.append(payload, start, end);
          sb.append('\n');
        }

        start = i + 1;
      }
    }
  }

  /**
   * Check if a substring range contains only whitespace characters.
   *
   * <p>Returns {@code true} if all characters in the range {@code [start, end)} are whitespace
   * according to {@link Character#isWhitespace(char)}.
   *
   * <p>Returns {@code true} for empty ranges ({@code start >= end}).
   *
   * @param s the string to check
   * @param start the start index (inclusive)
   * @param end the end index (exclusive)
   * @return {@code true} if the range contains only whitespace, {@code false} otherwise
   */
  private boolean isBlankRange(String s, int start, int end) {
    for (int i = start; i < end; i++) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Build an HTML {@code <script>} element with the specified content and attributes.
   *
   * <p>Generates a script tag in the format:
   *
   * <pre>
   * &lt;script [data-effect="el.remove()"] [custom-attrs]&gt;
   *   [script content]
   * &lt;/script&gt;
   * </pre>
   *
   * <p>The {@code data-effect="el.remove()"} attribute, when present, causes Datastar to
   * automatically remove the script tag from the DOM after execution, keeping the DOM clean.
   *
   * <p>Custom attributes can be used to specify:
   *
   * <ul>
   *   <li>{@code type="module"} - Load as ES6 module
   *   <li>{@code async} - Asynchronous execution
   *   <li>{@code defer} - Deferred execution
   *   <li>Any other valid HTML script attributes
   * </ul>
   *
   * <p><strong>Security Note:</strong> This method does not escape the script content or attribute
   * values. Callers are responsible for ensuring inputs are safe and do not contain user-controlled
   * data that could lead to XSS vulnerabilities.
   *
   * @param script the JavaScript code to execute
   * @param autoRemove if {@code true}, adds {@code data-effect="el.remove()"} to auto-remove the
   *     script after execution
   * @param attributes custom HTML attributes to add to the script tag (nullable)
   * @return an HTML string containing the complete {@code <script>} element
   * @see ExecuteScriptOptions
   */
  private String buildScriptElement(String script, Boolean autoRemove, List<String> attributes) {
    if (script == null || script.isEmpty() || script.contains("</script>")) {
      throw new IllegalArgumentException("Script cannot be null/empty or contain '</script>'");
    }

    StringBuilder el = new StringBuilder();
    el.append("<script");

    if (autoRemove != null && autoRemove) {
      el.append(" data-effect=\"el.remove()\"");
    }

    if (attributes != null && !attributes.isEmpty()) {
      for (String attr : attributes) {
        if (attr != null && !attr.isBlank()) {
          el.append(' ').append(attr);
        }
      }
    }

    el.append(">").append(script).append("</script>");
    return el.toString();
  }

  /**
   * Convert a Java string to a JavaScript string literal.
   *
   * <p>Escapes special characters and wraps in quotes.
   *
   * @param str the string to convert
   * @return a JavaScript string literal
   */
  private String toJsString(String str) {
    if (str == null) return "null";

    // Escape special JavaScript characters
    String escaped =
        str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");

    return "\"" + escaped + "\"";
  }
}
