package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class NamespaceTest {

  @ParameterizedTest
  @EnumSource(Namespace.class)
  void values_shouldBeCorrect(Namespace namespace) {
    assertNotNull(namespace.value);
    assertEquals(namespace.value.toLowerCase(), namespace.value);
  }

  @Test
  void specificValues_shouldBeCorrect() {
    assertEquals("html", Namespace.HTML.value);
    assertEquals("svg", Namespace.SVG.value);
    assertEquals("mathml", Namespace.MATHML.value);
  }
}
