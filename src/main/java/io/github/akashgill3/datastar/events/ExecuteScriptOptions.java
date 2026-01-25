package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

import java.util.ArrayList;
import java.util.List;

/**
 * Options for executing JavaScript in the browser.
 * <p>
 * Note: This is not a Datastar event type. ExecuteScript is a convenience method
 * that internally uses PatchElementsEvent to inject a script tag.
 *
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
