package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for executing JavaScript in the browser.
 * <p>
 * These options control how the script tag is injected and managed, along with
 * SSE delivery options (event id and retry duration).
 * <p>
 * Note: This is not a Datastar event type. ExecuteScript is a convenience method
 * that internally uses {@link PatchElementsEvent} to inject a script tag.
 * <p>
 * When {@link #autoRemove()} is enabled, the script tag is automatically removed
 * from the DOM after execution (Default: {@code true}).
 * <p>
 * Typical usage:
 * <pre>
 * ExecuteScriptOptions options = ExecuteScriptOptions.builder()
 *     .autoRemove(true)
 *     .attribute("type", "module")
 *     .eventId("script-1")
 *     .retryDuration(1000L)
 *     .build();
 * </pre>
 *
 * @param autoRemove    automatically remove script tag after execution (Default: {@code true})
 * @param attributes    list of HTML attributes to apply to the script tag
 * @param eventId       optional SSE event id
 * @param retryDuration SSE retry duration in milliseconds (Default: {@code 1000ms})
 * @author Akash Gill
 */
public record ExecuteScriptOptions(Boolean autoRemove, List<String> attributes, String eventId,
                                   Long retryDuration) {
    public static final ExecuteScriptOptions DEFAULT = ExecuteScriptOptions.builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Boolean autoRemove = Consts.DEFAULT_EXECUTE_AUTO_REMOVE;
        private final List<String> attributes = new ArrayList<>();
        private String eventId;
        private Long retryDuration;

        private Builder() {
        }

        public Builder autoRemove(Boolean autoRemove) {
            this.autoRemove = autoRemove;
            return this;
        }

        public Builder attribute(String key, String value) {
            this.attributes.add("%s=\"%s\"".formatted(key, value));
            return this;
        }

        public Builder attribute(String attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public Builder attributes(List<String> attributes) {
            this.attributes.addAll(attributes);
            return this;
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder retryDuration(Long retryDuration) {
            this.retryDuration = retryDuration;
            return this;
        }

        public ExecuteScriptOptions build() {
            return new ExecuteScriptOptions(autoRemove, List.copyOf(attributes), eventId, retryDuration);
        }
    }
}
