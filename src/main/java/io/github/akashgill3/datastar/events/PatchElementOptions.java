package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;

/**
 * Configuration options for {@code datastar-patch-elements} event.
 * <p>
 * These options control how a patch is applied (selector, patch mode, namespace, useViewTransition)
 * and how the event is delivered over SSE (event ID and retry duration).
 * <p>
 * Typical usage:
 * <pre>
 * sseEmitter.patchElements(elements, options -> options
 *     .selector("#content")
 *     .mode(ElementPatchMode.Outer)
 *     .useViewTransition(true)
 *     .namespace(Namespace.HTML)
 *     .eventId("my-event-id")
 *     .retryDuration(1000L));
 * </pre>
 *
 * @author Akash Gill
 */
public class PatchElementOptions {
    private String eventId;
    private Long retryDuration = Consts.DEFAULT_SSE_RETRY_DURATION_MS;
    private String selector;
    private ElementPatchMode mode = Consts.DEFAULT_ELEMENT_PATCH_MODE;
    private boolean useViewTransition = Consts.DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS;
    private Namespace namespace = Consts.DEFAULT_NAMESPACE;

    public PatchElementOptions eventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public PatchElementOptions retryDuration(Long retryDuration) {
        this.retryDuration = retryDuration;
        return this;
    }

    public PatchElementOptions selector(String selector) {
        this.selector = selector;
        return this;
    }

    public PatchElementOptions mode(ElementPatchMode mode) {
        this.mode = mode;
        return this;
    }

    public PatchElementOptions useViewTransition(boolean useViewTransition) {
        this.useViewTransition = useViewTransition;
        return this;
    }

    public PatchElementOptions namespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getEventId() {
        return eventId;
    }

    public Long getRetryDuration() {
        return retryDuration;
    }

    public String getSelector() {
        return selector;
    }

    public ElementPatchMode getMode() {
        return mode;
    }

    public boolean isUseViewTransition() {
        return useViewTransition;
    }

    public Namespace getNamespace() {
        return namespace;
    }
}
