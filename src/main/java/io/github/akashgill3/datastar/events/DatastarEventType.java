package io.github.akashgill3.datastar.events;

/**
 * Datastar SSE event type names as sent on the wire.
 * <p>
 * These values map directly to the SSE {@code event: ...} field.
 * They are used by {@code DatastarSseEmitter} when formatting events.
 * <p>
 * Supported event types:
 * <ul>
 *   <li>{@link #PATCH_ELEMENTS} - {@code datastar-patch-elements}</li>
 *   <li>{@link #PATCH_SIGNALS} - {@code datastar-patch-signals}</li>
 * </ul>
 *
 * @author Akash Gill
 */
public enum DatastarEventType {
    PATCH_ELEMENTS("datastar-patch-elements"),
    PATCH_SIGNALS("datastar-patch-signals");

    /**
     * The literal value written to the SSE {@code event:} field.
     */
    public final String value;

    DatastarEventType(String value) {
        this.value = value;
    }
}
