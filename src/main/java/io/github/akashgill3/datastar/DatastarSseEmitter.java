package io.github.akashgill3.datastar;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import io.github.akashgill3.datastar.events.DatastarEventType;
import io.github.akashgill3.datastar.events.PatchElementsEvent;
import io.github.akashgill3.datastar.events.PatchSignalsEvent;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Set;

import static org.springframework.http.MediaType.TEXT_PLAIN;

public class DatastarSseEmitter extends ResponseBodyEmitter {

    private final static Logger log = LoggerFactory.getLogger(DatastarSseEmitter.class);

    private final DatastarProperties properties;

    public DatastarSseEmitter(DatastarProperties properties) {
        super();
        this.properties = properties;
    }

    @Override
    protected void extendResponse(@NonNull ServerHttpResponse outputMessage) {
        super.extendResponse(outputMessage);

        HttpHeaders headers = outputMessage.getHeaders();
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        }
    }

    public DatastarSseEmitter patchElements(PatchElementsEvent event) throws IOException {
        super.send(formatPatchElementsEvent(event));
        return this;
    }

    public DatastarSseEmitter patchSignals(PatchSignalsEvent event) throws IOException {
        super.send(formatPatchSignalsEvent(event));
        return this;
    }

    public void executeScript(String script) {
    }

    public void executeScript(String script, boolean autoRemove, String[] attributes, Long retryDuration) {
    }

    public void redirect() {
    }

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
            appendDataLine(sb, Consts.NAMESPACE_DATALINE_LITERAL, event.options().namespace());
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

    private void appendLine(StringBuilder sb, String literal, Object value) {
        sb.append("%s: %s\n".formatted(literal, value));
    }

    private void appendDataLine(StringBuilder sb, String literal, Object value) {
        sb.append("data: %s %s\n".formatted(literal, value));
    }
}
