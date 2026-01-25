package io.github.akashgill3.datastar.events;

public record PatchSignalsEvent(String script, PatchSignalOptions options) implements DatastarEvent {
    public static final PatchSignalOptions DEFAULT_OPTIONS = PatchSignalOptions.builder().build();

    public static PatchSignalsEvent of(String script) {
        return new PatchSignalsEvent(script, DEFAULT_OPTIONS);
    }

    public static PatchSignalsEvent withOptions(String script, PatchSignalOptions options) {
        return new PatchSignalsEvent(script, options);
    }
}
