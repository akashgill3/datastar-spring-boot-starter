package io.github.akashgill3.datastar.events;

/**
 * Patch modes for updating DOM elements via {@link DatastarEventType#PATCH_ELEMENTS}.
 * <p>
 * The chosen mode in which an element is patched into the DOM.
 * <p>
 * Common modes:
 * <ul>
 *   <li>{@link #Outer} - Morphs the element into the existing element</li>
 *   <li>{@link #Inner} - Replaces the inner HTML of the existing element</li>
 *   <li>{@link #Remove} - Removes the existing element</li>
 *   <li>{@link #Replace} - replaces the existing element with the new element</li>
 *   <li>{@link #Prepend} - Prepends the element inside to the existing element</li>
 *   <li>{@link #Append} - Appends the element inside the existing element</li>
 *   <li>{@link #Before} - Inserts the element before the existing element</li>
 *   <li>{@link #After} - Inserts the element after the existing element</li>
 * </ul>
 *
 * @author Akash Gill
 */
public enum ElementPatchMode {
    Outer("outer"),
    Inner("inner"),
    Remove("remove"),
    Replace("replace"),
    Prepend("prepend"),
    Append("append"),
    Before("before"),
    After("after");

    /**
     * The literal value sent over the wire for the {@code mode} data line.
     */
    public final String value;

    ElementPatchMode(String value) {
        this.value = value;
    }

}

