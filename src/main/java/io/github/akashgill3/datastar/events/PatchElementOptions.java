package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

/**
 * Options for {@link PatchElementsEvent}.
 * <p>
 * These options control how a patch is applied (selector, patch mode, namespace)
 * and how the event is delivered over SSE (event id and retry duration).
 * <p>
 * Typical usage:
 * <pre>
 * PatchElementOptions options = PatchElementOptions.builder()
 *     .selector("#content")
 *     .mode(ElementPatchMode.Outer)
 *     .useViewTransition(true)
 *     .namespace(Namespace.HTML)
 *     .eventId("my-event-id")
 *     .retryDuration(1000)
 *     .build();
 * </pre>
 * <p>
 *
 * @param selector           CSS selector for the target element(s)
 * @param mode               how to apply the patch relative to the target (Default: {@link ElementPatchMode#Outer}
 * @param useViewTransition  whether to use view transitions (if supported) (Default: {@code false})
 * @param namespace          namespace used when parsing the incoming markup (Default: {@link Namespace#HTML})
 * @param eventId            optional SSE event id
 * @param retryDuration      SSE retry duration in milliseconds (Default: {@code 1000ms})
 *
 * @author Akash Gill
 */
public record PatchElementOptions(
        String selector,
        ElementPatchMode mode,
        boolean useViewTransition,
        Namespace namespace,
        String eventId,
        long retryDuration) {

    public static final PatchElementOptions DEFAULT = PatchElementOptions.builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private String selector;
        private ElementPatchMode mode = Consts.DEFAULT_ELEMENT_PATCH_MODE;
        private boolean useViewTransition = Consts.DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS;
        private Namespace namespace = Consts.DEFAULT_NAMESPACE;
        private long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION_MS;

        private Builder() {
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder selector(String selector) {
            this.selector = selector;
            return this;
        }

        public Builder mode(ElementPatchMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder useViewTransition(boolean useViewTransition) {
            this.useViewTransition = useViewTransition;
            return this;
        }

        public Builder namespace(Namespace namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder retryDuration(long retryDuration) {
            this.retryDuration = retryDuration;
            return this;
        }

        public PatchElementOptions build() {
            return new PatchElementOptions(selector, mode, useViewTransition, namespace, eventId, retryDuration);
        }
    }
}
