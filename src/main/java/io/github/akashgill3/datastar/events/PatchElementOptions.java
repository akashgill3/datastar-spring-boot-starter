package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

public record PatchElementOptions(
        String selector,
        ElementPatchMode mode,
        boolean useViewTransition,
        Namespace namespace,
        String eventId,
        long retryDuration) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private String selector;
        private ElementPatchMode mode = Consts.DEFAULT_ELEMENT_PATCH_MODE;
        private boolean useViewTransition = Consts.DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS;
        private Namespace namespace = Consts.DEFAULT_NAMESPACE;
        private long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION;

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
