package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.DatastarSseEmitter;

/**
 * Marker interface for Datastar SSE events.
 * <p>
 * Only these event types are sent over the SSE wire:
 * <ul>
 *   <li>{@link PatchElementsEvent} - datastar-patch-elements</li>
 *   <li>{@link PatchSignalsEvent} - datastar-patch-signals</li>
 * </ul>
 * <p>
 * Note: Other helpers like {@link DatastarSseEmitter#executeScript(String)} are convenience
 * methods that use these underlying events.
 *
 * @author Akash Gill
 */
public sealed interface DatastarEvent permits PatchElementsEvent, PatchSignalsEvent {
}
