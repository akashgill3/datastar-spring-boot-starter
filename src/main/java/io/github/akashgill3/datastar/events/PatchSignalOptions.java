package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

/**
 * Options for {@code 'datastar-patch-signals'} event.
 * <p>
 * This class provides configuration options for signal patch events, controlling
 * SSE delivery (event id and retry duration) and how the incoming signal payload
 * is applied.
 * <p>
 * When {@link #isOnlyIfMissing()} returns {@code true}, the patch is applied conditionally:
 * signals are only set if they do not already exist on the client.
 * <p>
 * Typical usage:
 * <pre>
 * PatchSignalOptions options = new PatchSignalOptions()
 *     .eventId("signals-1")
 *     .onlyIfMissing(true)
 *     .retryDuration(1000L);
 * </pre>
 * <p>
 * Or using a Consumer pattern:
 * <pre>
 * PatchSignalOptions options = new PatchSignalOptions();
 * Consumer&lt;PatchSignalOptions&gt; configurator = opt -&gt; opt
 *     .eventId("signals-1")
 *     .onlyIfMissing(true)
 *     .retryDuration(1000L);
 * configurator.accept(options);
 * </pre>
 *
 * @author Akash Gill
 */
public class PatchSignalOptions {
    private String eventId;
    private boolean onlyIfMissing = Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING;
    private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION_MS;

    public PatchSignalOptions eventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public PatchSignalOptions onlyIfMissing(boolean onlyIfMissing) {
        this.onlyIfMissing = onlyIfMissing;
        return this;
    }

    public PatchSignalOptions retryDuration(Long retryDuration) {
        this.retryDuration = retryDuration;
        return this;
    }

    public String getEventId() {
        return eventId;
    }

    public boolean isOnlyIfMissing() {
        return onlyIfMissing;
    }

    public Long getRetryDuration() {
        return retryDuration;
    }
}
