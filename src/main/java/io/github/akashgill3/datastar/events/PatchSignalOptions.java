package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

public record PatchSignalOptions(String eventId, boolean onlyIfMissing, Long retryDuration) {
    public static OptionsBuilder builder() {
        return new OptionsBuilder();
    }

    public static class OptionsBuilder {
        private String eventId;
        private boolean onlyIfMissing = Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING;
        private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION;

        private OptionsBuilder() {
        }

        public OptionsBuilder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public OptionsBuilder onlyIfMissing(boolean onlyIfMissing) {
            this.onlyIfMissing = onlyIfMissing;
            return this;
        }

        public OptionsBuilder retryDuration(Long retryDuration) {
            this.retryDuration = retryDuration;
            return this;
        }

        public PatchSignalOptions build() {
            return new PatchSignalOptions(eventId, onlyIfMissing, retryDuration);
        }
    }
}
