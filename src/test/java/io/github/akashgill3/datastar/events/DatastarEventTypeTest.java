package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DatastarEventTypeTest {

  @ParameterizedTest
  @EnumSource(DatastarEventType.class)
  void values_shouldBeCorrect(DatastarEventType type) {
    assertNotNull(type.value);
    assertTrue(type.value.startsWith("datastar-"));
  }

  @Test
  void specificValues_shouldBeCorrect() {
    assertEquals("datastar-patch-elements", DatastarEventType.PATCH_ELEMENTS.value);
    assertEquals("datastar-patch-signals", DatastarEventType.PATCH_SIGNALS.value);
  }
}
