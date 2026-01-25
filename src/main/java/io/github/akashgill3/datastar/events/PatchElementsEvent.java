package io.github.akashgill3.datastar.events;

/**
 * Datastar event for patching DOM elements.
 * <p>
 * This event is sent over SSE with type {@code datastar-patch-elements} and
 * carries HTML markup plus {@link PatchElementOptions} that describe where and
 * how to apply the patch.
 * <p>
 * Convenience factory methods:
 * <ul>
 *   <li>{@link #of(String)} - create an event with {@link PatchElementOptions#DEFAULT}</li>
 *   <li>{@link #withOptions(String, PatchElementOptions)} - create an event with custom options</li>
 * </ul>
 *
 * @param elements the HTML markup to apply
 * @param options  patch options (selector, mode, namespace, etc.)
 * @author Akash Gill
 * @see PatchElementOptions
 */
public record PatchElementsEvent(String elements, PatchElementOptions options) implements DatastarEvent {
    public static PatchElementsEvent of(String elements) {
        return new PatchElementsEvent(elements, PatchElementOptions.DEFAULT);
    }

    public static PatchElementsEvent withOptions(String elements, PatchElementOptions options) {
        return new PatchElementsEvent(elements, options);
    }
}
