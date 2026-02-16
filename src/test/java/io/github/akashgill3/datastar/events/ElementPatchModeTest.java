package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ElementPatchModeTest {

  @ParameterizedTest
  @EnumSource(ElementPatchMode.class)
  void values_shouldBeCorrect(ElementPatchMode mode) {
    assertNotNull(mode.value);
    assertEquals(mode.value.toLowerCase(), mode.value);
  }

  @Test
  void specificValues_shouldBeCorrect() {
    assertEquals("outer", ElementPatchMode.Outer.value);
    assertEquals("inner", ElementPatchMode.Inner.value);
    assertEquals("remove", ElementPatchMode.Remove.value);
    assertEquals("replace", ElementPatchMode.Replace.value);
    assertEquals("prepend", ElementPatchMode.Prepend.value);
    assertEquals("append", ElementPatchMode.Append.value);
    assertEquals("before", ElementPatchMode.Before.value);
    assertEquals("after", ElementPatchMode.After.value);
  }
}
