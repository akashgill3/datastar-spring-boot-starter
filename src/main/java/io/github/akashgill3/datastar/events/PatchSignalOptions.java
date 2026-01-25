package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

/**
 * Options for {@link PatchSignalsEvent}.
 * <p>
 * These options control SSE delivery (event id and retry duration) and how the
 * incoming signal payload is applied.
 * <p>
 * When {@link #onlyIfMissing()} is enabled, the patch is applied conditionally:
 * signals are only set if they do not already exist on the client.
 * <p>
 * Typical usage:
 * <pre>
 * PatchSignalOptions options = PatchSignalOptions.builder()
 *     .eventId("signals-1")
 *     .onlyIfMissing(true)
 *     .retryDuration(1000L)
 *     .build();
 * </pre>
 *
 * @param eventId       optional SSE event id
 * @param onlyIfMissing apply only if the target signals are missing (Default: {@code false})
 * @param retryDuration SSE retry duration in milliseconds (Default: {@code 1000ms})
 * @author Akash Gill
 */
public record PatchSignalOptions(String eventId, boolean onlyIfMissing, Long retryDuration) {
    public static final PatchSignalOptions DEFAULT = PatchSignalOptions.builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventId;
        private boolean onlyIfMissing = Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING;
        private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION_MS;

        private Builder() {
        }

        public Builder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder onlyIfMissing(boolean onlyIfMissing) {
            this.onlyIfMissing = onlyIfMissing;
            return this;
        }

        public Builder retryDuration(Long retryDuration) {
            this.retryDuration = retryDuration;
            return this;
        }

        public PatchSignalOptions build() {
            return new PatchSignalOptions(eventId, onlyIfMissing, retryDuration);
        }
    }
}
