package io.github.akashgill3.datastar.events;

/**
 * The namespace to use when patching elements into the DOM.
 * <p>
 * This controls which namespace the browser uses when parsing the incoming
 * markup (e.g., HTML vs. SVG).
 * <p>
 * Available namespaces:
 * <ul>
 *   <li>{@link #HTML} - standard HTML</li>
 *   <li>{@link #SVG} - SVG elements</li>
 *   <li>{@link #MATHML} - MathML elements</li>
 * </ul>
 *
 * @author Akash Gill
 */
public enum Namespace {
    HTML("html"),
    SVG("svg"),
    MATHML("mathml");

    /**
     * The literal value sent over the wire for the {@code namespace} data line.
     */
    public final String value;

    Namespace(String value) {
        this.value = value;
    }
}
