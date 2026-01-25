package io.github.akashgill3.datastar.events;

public record PatchElementsEvent(String elements, PatchElementOptions options) implements DatastarEvent {
    public static final PatchElementOptions DEFAULT_OPTIONS = PatchElementOptions.builder().build();

    public static PatchElementsEvent of(String elements) {
        return new PatchElementsEvent(elements, DEFAULT_OPTIONS);
    }

    public static PatchElementsEvent withOptions(String elements, PatchElementOptions options) {
        return new PatchElementsEvent(elements, options);
    }
}
