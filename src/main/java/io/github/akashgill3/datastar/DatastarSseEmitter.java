package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import io.github.akashgill3.datastar.events.*;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * SSE emitter for sending Datastar events to the browser.
 * <p>
 * Supports the following Datastar event types:
 * <ul>
 *   <li>{@link #patchElements(PatchElementsEvent)} - Patch DOM elements</li>
 *   <li>{@link #patchSignals(PatchSignalsEvent)} - Patch signal store</li>
 * </ul>
 * <p>
 * Also provides convenience methods:
 * <ul>
 *   <li>{@link #executeScript(String)} - Execute JavaScript in the browser</li>
 *   <li>{@link #consoleLog(String)} - Log to browser console</li>
 *   <li>{@link #consoleError(String)} - Log error to browser console</li>
 *   <li>{@link #redirect(String)} - Redirect browser</li>
 *   <li>{@link #replaceUrl(String)} - Replace url in the browser</li>
 * </ul>
 *
 * @author Akash Gill
 */
public class DatastarSseEmitter extends ResponseBodyEmitter {

    private final static Logger log = LoggerFactory.getLogger(DatastarSseEmitter.class);

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
     * @param event the patch elements event containing HTML and options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter patchElements(PatchElementsEvent event) throws IOException {
        super.send(formatPatchElementsEvent(event));
        return this;
    }

    /**
     * Send a patch signals event to update the signal store.
     *
     * @param event the patch signals event containing JSON signals and options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter patchSignals(PatchSignalsEvent event) throws IOException {
        super.send(formatPatchSignalsEvent(event));
        return this;
    }

    // ========================================================================
    // Execute Script - Convenience Methods
    // ========================================================================

    /**
     * Execute a JavaScript script in the browser with default options.
     * <p>
     * This is a convenience method that internally creates a {@code <script>} tag
     * and sends it via {@link #patchElements(PatchElementsEvent)}. It is not a
     * separate Datastar event type.
     *
     * @param script the JavaScript code to execute
     * @return this emitter
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter executeScript(String script) throws IOException {
        return executeScript(script, ExecuteScriptOptions.DEFAULT);
    }

    /**
     * Execute a JavaScript script in the browser with custom options.
     *
     * @param script  the JavaScript code to execute
     * @param options configuration for script execution
     * @return this emitter
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter executeScript(String script, ExecuteScriptOptions options) throws IOException {
        String element = buildScriptElement(script, options.autoRemove(), options.attributes());

        PatchElementOptions.Builder builder = PatchElementOptions.builder()
                .selector("body")
                .mode(ElementPatchMode.Append);

        if (options.eventId() != null && !options.eventId().isEmpty()) builder.eventId(options.eventId());
        if (options.retryDuration() != null) builder.retryDuration(options.retryDuration());

        return patchElements(PatchElementsEvent.withOptions(element, builder.build()));
    }

    // ========================================================================
    // Console Logging - Convenience Methods
    // ========================================================================

    /**
     * Log a message to the browser console.
     * <p>
     * Equivalent to calling {@code console.log(message)} in the browser.
     *
     * @param message the message to log
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter consoleLog(String message) throws IOException {
        return consoleLog(message, ExecuteScriptOptions.DEFAULT);
    }

    /**
     * Log a message to the browser console with custom options.
     *
     * @param message the message to log
     * @param options execution options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter consoleLog(String message, ExecuteScriptOptions options) throws IOException {
        String script = String.format("console.log(%s)", toJsString(message));
        return executeScript(script, options);
    }

    /**
     * Log an error to the browser console.
     * <p>
     * Equivalent to calling {@code console.error(message)} in the browser.
     *
     * @param message the error message to log
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter consoleError(String message) throws IOException {
        return consoleError(message, ExecuteScriptOptions.DEFAULT);
    }

    /**
     * Log an error to the browser console with custom options.
     *
     * @param message the error message to log
     * @param options execution options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter consoleError(String message, ExecuteScriptOptions options) throws IOException {
        String script = String.format("console.error(%s)", toJsString(message));
        return executeScript(script, options);
    }

    // ========================================================================
    // Browser Navigation - Convenience Methods
    // ========================================================================

    /**
     * Redirect the browser to a new URL.
     * <p>
     * Uses {@code setTimeout} to allow current event processing to complete
     * before redirecting.
     *
     * @param url the URL to redirect to
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter redirect(String url) throws IOException {
        return redirect(url, ExecuteScriptOptions.DEFAULT);
    }

    /**
     * Redirect the browser to a new URL with custom options.
     *
     * @param url     the URL to redirect to
     * @param options execution options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter redirect(String url, ExecuteScriptOptions options) throws IOException {
        String script = "setTimeout(() => window.location.href = %s)".formatted(toJsString(url));
        return executeScript(script, options);
    }

    /**
     * Replace the current URL in the browser's history without reloading.
     * <p>
     * Uses {@code window.history.replaceState} to update the URL bar.
     *
     * @param url the new URL
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter replaceUrl(String url) throws IOException {
        return replaceUrl(url, ExecuteScriptOptions.DEFAULT);
    }

    /**
     * Replace the current URL in the browser's history with custom options.
     *
     * @param url     the new URL
     * @param options execution options
     * @return this emitter for method chaining
     * @throws IOException if an I/O error occurs
     */
    public DatastarSseEmitter replaceUrl(String url, ExecuteScriptOptions options) throws IOException {
        String script = "setTimeout(() => window.history.replaceState({}, '', %s)".formatted(toJsString(url));
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
    }

    // ========================================================================
    // Internal Formatting Methods
    // ========================================================================

    /**
     * Format a PatchElementsEvent into SSE wire format.
     * <p>
     * Generates an SSE event with the following structure:
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
     * <p>
     * The generated event follows the
     * <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events">
     * Server-Sent Events specification</a> and the Datastar protocol.
     * <p>
     * Only non-default values are included in the output to minimize wire size.
     * Multi-line HTML is split with each line sent as a separate {@code data: elements} line.
     *
     * @param event the patch elements event to format
     * @return a set containing a single {@link DataWithMediaType} with the formatted SSE event
     * @see <a href="https://data-star.dev/reference/sse_events#datastar-patch-elements">Datastar Reference</a>
     */
    private Set<DataWithMediaType> formatPatchElementsEvent(PatchElementsEvent event) {
        StringBuilder sb = new StringBuilder();

        appendLine(sb, "event", DatastarEventType.PATCH_ELEMENTS.value);

        if (event.options().eventId() != null) {
            appendLine(sb, "id", event.options().eventId());
        }
        if (event.options().retryDuration() != Consts.DEFAULT_SSE_RETRY_DURATION) {
            appendLine(sb, "retry", event.options().retryDuration());
        }
        if (event.options().selector() != null && !event.options().selector().isEmpty()) {
            appendDataLine(sb, Consts.SELECTOR_DATALINE_LITERAL, event.options().selector().trim());
        }
        if (event.options().mode() != null && !event.options().mode().equals(Consts.DEFAULT_ELEMENT_PATCH_MODE)) {
            appendDataLine(sb, Consts.MODE_DATALINE_LITERAL, event.options().mode().value);
        }
        if (event.options().useViewTransition()) {
            appendDataLine(sb, Consts.USE_VIEW_TRANSITION_DATALINE_LITERAL, "true");
        }
        if (event.options().namespace() != null && !event.options().namespace().equals(Consts.DEFAULT_NAMESPACE)) {
            appendDataLine(sb, Consts.NAMESPACE_DATALINE_LITERAL, event.options().namespace().value);
        }

        if (event.elements() != null && !event.elements().isEmpty()) {
            event.elements().lines()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> appendDataLine(sb, Consts.ELEMENTS_DATALINE_LITERAL, line));
        }
        sb.append("\n");

        if (properties.debugLogging()) {
            log.debug("Formatted PatchElementsEvent into SSE event: {}", sb);
        }

        return Set.of(new DataWithMediaType(sb.toString(), TEXT_PLAIN));
    }

    /**
     * Format a PatchSignalsEvent into SSE wire format.
     * <p>
     * Generates an SSE event with the following structure:
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
     * Only non-default values are included in the output. Multi-line JSON is split
     * with each line sent as a separate {@code data: signals} line.
     *
     * @param event the patch signals event to format
     * @return a set containing a single {@link DataWithMediaType} with the formatted SSE event
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7386">RFC 7386 JSON Merge Patch</a>
     */
    private Set<DataWithMediaType> formatPatchSignalsEvent(PatchSignalsEvent event) {
        StringBuilder sb = new StringBuilder(128);

        appendLine(sb, "event", DatastarEventType.PATCH_SIGNALS.value);

        if (event.options().eventId() != null) {
            appendLine(sb, "id", event.options().eventId());
        }
        if (event.options().retryDuration() != null && !event.options().retryDuration().equals(Consts.DEFAULT_SSE_RETRY_DURATION)) {
            appendLine(sb, "retry", event.options().retryDuration());
        }

        if (event.options().onlyIfMissing()) {
            appendDataLine(sb, Consts.ONLY_IF_MISSING_DATALINE_LITERAL, true);
        }

        if (event.script() != null && !event.script().isEmpty()) {
            event.script().lines()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> appendDataLine(sb, Consts.SIGNALS_DATALINE_LITERAL, line));
        }
        sb.append("\n");

        if (properties.debugLogging()) {
            log.debug("Formatted PatchSignalsEvent into SSE event: {}", sb);
        }

        return Set.of(new DataWithMediaType(sb.toString(), TEXT_PLAIN));
    }

    /**
     * Append an SSE field line to the output buffer.
     * <p>
     * Generates a line in the format: {@code field: value\n}
     * <p>
     * Used for SSE control fields like {@code event}, {@code id}, and {@code retry}.
     * These fields appear before any {@code data} lines and control SSE behavior.
     *
     * @param sb      the string builder to append to
     * @param literal the field name (e.g., "event", "id", "retry")
     * @param value   the field value
     * @see <a href="https://html.spec.whatwg.org/multipage/server-sent-events.html#server-sent-events">
     * SSE Specification</a>
     */
    private void appendLine(StringBuilder sb, String literal, Object value) {
        sb.append("%s: %s\n".formatted(literal, value));
    }

    /**
     * Append a Datastar data line to the output buffer.
     * <p>
     * Generates a line in the format: {@code data: prefix value\n}
     * <p>
     * The prefix (literal) identifies the data type for Datastar's protocol:
     * <ul>
     *   <li>{@code elements} - HTML element data</li>
     *   <li>{@code signals} - JSON signal data</li>
     *   <li>{@code selector} - CSS selector</li>
     *   <li>{@code mode} - Patch mode</li>
     *   <li>{@code namespace} - XML namespace</li>
     *   <li>{@code useViewTransition} - View transition flag</li>
     *   <li>{@code onlyIfMissing} - Conditional patch flag</li>
     * </ul>
     * <p>
     * Multiple {@code data} lines with the same prefix can appear in a single event
     * (e.g., for multi-line HTML or JSON).
     *
     * @param sb      the string builder to append to
     * @param literal the data type prefix (e.g., "elements", "signals")
     * @param value   the data value
     */
    private void appendDataLine(StringBuilder sb, String literal, Object value) {
        sb.append("data: %s %s\n".formatted(literal, value));
    }

    /**
     * Build an HTML {@code <script>} element with the specified content and attributes.
     * <p>
     * Generates a script tag in the format:
     * <pre>
     * &lt;script [data-effect="el.remove()"] [custom-attrs]&gt;
     *   [script content]
     * &lt;/script&gt;
     * </pre>
     * <p>
     * The {@code data-effect="el.remove()"} attribute, when present, causes Datastar
     * to automatically remove the script tag from the DOM after execution, keeping
     * the DOM clean.
     * <p>
     * Custom attributes can be used to specify:
     * <ul>
     *   <li>{@code type="module"} - Load as ES6 module</li>
     *   <li>{@code async} - Asynchronous execution</li>
     *   <li>{@code defer} - Deferred execution</li>
     *   <li>Any other valid HTML script attributes</li>
     * </ul>
     * <p>
     * <strong>Security Note:</strong> This method does not escape the script content
     * or attribute values. Callers are responsible for ensuring inputs are safe and
     * do not contain user-controlled data that could lead to XSS vulnerabilities.
     *
     * @param script     the JavaScript code to execute
     * @param autoRemove if {@code true}, adds {@code data-effect="el.remove()"} to
     *                   auto-remove the script after execution
     * @param attributes custom HTML attributes to add to the script tag (nullable)
     * @return an HTML string containing the complete {@code <script>} element
     * @see ExecuteScriptOptions
     */
    private String buildScriptElement(String script, Boolean autoRemove, Map<String, String> attributes) {
        if (script.isEmpty() || script.contains("</script>")) {
            throw new IllegalArgumentException("Script cannot be empty or contain '</script>'");
        }

        StringBuilder el = new StringBuilder();
        el.append("<script");

        if (autoRemove != null && autoRemove) {
            el.append(" data-effect=\"el.remove()\"");
        }

        if (attributes != null && !attributes.isEmpty()) {
            attributes.forEach((name, value) -> {
                if (name != null && !name.isBlank() && value != null && !value.isBlank()) {
                    el.append(" %s=\"%s\"".formatted(name, value));
                }
            });
        }

        el.append(">").append(script).append("</script>");
        return el.toString();
    }

    /**
     * Convert a Java string to a JavaScript string literal.
     * <p>
     * Escapes special characters and wraps in quotes.
     *
     * @param str the string to convert
     * @return a JavaScript string literal
     */
    private String toJsString(String str) {
        if (str == null) return "null";

        // Escape special JavaScript characters
        String escaped = str
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return "\"" + escaped + "\"";
    }
}
