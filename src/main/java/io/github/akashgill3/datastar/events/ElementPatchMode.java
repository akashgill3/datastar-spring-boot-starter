package io.github.akashgill3.datastar.events;

public enum ElementPatchMode {
    Outer("outer"),
    Inner("inner"),
    Remove("remove"),
    Replace("replace"),
    Prepend("prepend"),
    Append("append"),
    Before("before"),
    After("after");

    public final String value;

    ElementPatchMode(String value) {
        this.value = value;
    }

}

