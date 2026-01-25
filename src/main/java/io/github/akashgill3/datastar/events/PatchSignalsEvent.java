package io.github.akashgill3.datastar.events;

/**
 * Datastar event for patching the signal store.
 * <p>
 * This event is sent over SSE with type {@code datastar-patch-signals} and
 * carries a JSON payload plus {@link PatchSignalOptions}.
 * <p>
 * The payload is typically a JSON Merge Patch document.
 * <p>
 * Convenience factory methods:
 * <ul>
 *   <li>{@link #of(String)} - create an event with {@link PatchSignalOptions#DEFAULT}</li>
 *   <li>{@link #withOptions(String, PatchSignalOptions)} - create an event with custom options</li>
 * </ul>
 *
 * @param signals JSON payload to apply to the signal store
 * @param options signal patch options
 * @author Akash Gill
 */
public record PatchSignalsEvent(String signals, PatchSignalOptions options) implements DatastarEvent {
    public static PatchSignalsEvent of(String signals) {
        return new PatchSignalsEvent(signals, PatchSignalOptions.DEFAULT);
    }

    public static PatchSignalsEvent withOptions(String signals, PatchSignalOptions options) {
        return new PatchSignalsEvent(signals, options);
    }
}
