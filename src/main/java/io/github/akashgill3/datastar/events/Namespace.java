package io.github.akashgill3.datastar.events;

public enum Namespace {
    HTML("html"),
    SVG("svg"),
    MATHML("mathml");

    public final String value;

    Namespace(String value) {
        this.value = value;
    }
}
