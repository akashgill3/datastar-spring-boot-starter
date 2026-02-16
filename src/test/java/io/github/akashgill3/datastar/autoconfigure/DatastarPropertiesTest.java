package io.github.akashgill3.datastar.autoconfigure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DatastarPropertiesTest {

  @Test
  void defaultValues_shouldBeCorrect() {
    DatastarProperties properties = new DatastarProperties(false);
    assertFalse(properties.enableLogging());
  }

  @Test
  void constructor_withCustomValues_shouldWorkCorrectly() {
    DatastarProperties properties = new DatastarProperties(true);
    assertTrue(properties.enableLogging());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void constructor_withValidEnableLogging_shouldAcceptValue(boolean enableLogging) {
    DatastarProperties properties = new DatastarProperties(enableLogging);
    assertEquals(enableLogging, properties.enableLogging());
  }

  @Test
  void toString_shouldContainPropertyValues() {
    DatastarProperties properties = new DatastarProperties(false);

    String toString = properties.toString();

    assertTrue(toString.contains("false"));
  }
}
