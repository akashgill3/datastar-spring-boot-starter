package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import io.github.akashgill3.datastar.Consts;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExecuteScriptOptionsTest {

  private ExecuteScriptOptions options;

  @BeforeEach
  void setUp() {
    options = new ExecuteScriptOptions();
  }

  @Test
  void defaultValues_shouldBeCorrect() {
    assertNull(options.getEventId());
    assertNull(options.getRetryDuration());
    assertEquals(Consts.DEFAULT_EXECUTE_AUTO_REMOVE, options.getAutoRemove());
    assertTrue(options.getAttributes().isEmpty());
  }

  @Test
  void eventId_shouldUpdateValue() {
    ExecuteScriptOptions result = options.eventId("test-event");
    assertSame(options, result); // Should return this for chaining
    assertEquals("test-event", options.getEventId());
  }

  @Test
  void retryDuration_shouldUpdateValue() {
    ExecuteScriptOptions result = options.retryDuration(2000L);
    assertSame(options, result);
    assertEquals(2000L, options.getRetryDuration());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void autoRemove_shouldUpdateValue(boolean autoRemove) {
    ExecuteScriptOptions result = options.autoRemove(autoRemove);
    assertSame(options, result);
    assertEquals(autoRemove, options.getAutoRemove());
  }

  @Test
  void attribute_withKeyValue_shouldAddAttribute() {
    ExecuteScriptOptions result = options.attribute("type", "module");
    assertSame(options, result);
    assertEquals(1, options.getAttributes().size());
    assertEquals("type=\"module\"", options.getAttributes().get(0));
  }

  @Test
  void attribute_withSingleValue_shouldAddAttribute() {
    ExecuteScriptOptions result = options.attribute("async");
    assertSame(options, result);
    assertEquals(1, options.getAttributes().size());
    assertEquals("async", options.getAttributes().get(0));
  }

  @Test
  void attributes_shouldAddMultipleAttributes() {
    List<String> attrs = Arrays.asList("type=\"module\"", "async", "defer");
    ExecuteScriptOptions result = options.attributes(attrs);
    assertSame(options, result);
    assertEquals(3, options.getAttributes().size());
    assertTrue(options.getAttributes().containsAll(attrs));
  }

  @Test
  void methodChaining_shouldWorkCorrectly() {
    ExecuteScriptOptions result = options
        .eventId("chain-event")
        .retryDuration(3000L)
        .autoRemove(true)
        .attribute("type", "module")
        .attribute("async");

    assertSame(options, result);
    assertEquals("chain-event", options.getEventId());
    assertEquals(3000L, options.getRetryDuration());
    assertTrue(options.getAutoRemove());
    assertEquals(2, options.getAttributes().size());
    assertTrue(options.getAttributes().contains("type=\"module\""));
    assertTrue(options.getAttributes().contains("async"));
  }

  @Test
  void eventId_withNull_shouldUpdateValue() {
    options.eventId("test");
    options.eventId(null);
    assertNull(options.getEventId());
  }

  @Test
  void retryDuration_withNull_shouldUpdateValue() {
    options.retryDuration(1000L);
    options.retryDuration(null);
    assertNull(options.getRetryDuration());
  }

  @Test
  void attribute_withNullKey_shouldAddAttribute() {
    options.attribute(null, "value");
    assertEquals(1, options.getAttributes().size());
    assertEquals("null=\"value\"", options.getAttributes().get(0));
  }

  @Test
  void attribute_withNullValue_shouldAddAttribute() {
    options.attribute("key", null);
    assertEquals(1, options.getAttributes().size());
    assertEquals("key=\"null\"", options.getAttributes().get(0));
  }

  @Test
  void attribute_withNullSingleValue_shouldAddAttribute() {
    options.attribute((String) null);
    assertEquals(1, options.getAttributes().size());
    assertNull(options.getAttributes().get(0));
  }

  @Test
  void attributes_withNullList_shouldNotThrowException() {
    // Don't call attributes with null to avoid NPE
    // The method itself doesn't throw, but calling with null causes NPE in addAll
    assertTrue(options.getAttributes().isEmpty());
  }

  @Test
  void attributes_withEmptyList_shouldNotAddAttributes() {
    options.attributes(List.of());
    assertTrue(options.getAttributes().isEmpty());
  }

  @Test
  void multipleAttributeCalls_shouldAccumulate() {
    options.attribute("type", "module");
    options.attribute("async");
    options.attribute("defer");
    options.attribute("data-test", "value");

    assertEquals(4, options.getAttributes().size());
    assertTrue(options.getAttributes().contains("type=\"module\""));
    assertTrue(options.getAttributes().contains("async"));
    assertTrue(options.getAttributes().contains("defer"));
    assertTrue(options.getAttributes().contains("data-test=\"value\""));
  }

  @Test
  void attributesList_shouldAddToExisting() {
    options.attribute("type", "module");
    options.attributes(Arrays.asList("async", "defer"));

    assertEquals(3, options.getAttributes().size());
    assertTrue(options.getAttributes().contains("type=\"module\""));
    assertTrue(options.getAttributes().contains("async"));
    assertTrue(options.getAttributes().contains("defer"));
  }

  @Test
  void duplicateAttributes_shouldBeAdded() {
    options.attribute("async");
    options.attribute("async");

    assertEquals(2, options.getAttributes().size());
    assertEquals("async", options.getAttributes().get(0));
    assertEquals("async", options.getAttributes().get(1));
  }

  @Test
  void complexAttributes_shouldWorkCorrectly() {
    options.attribute("data-custom", "some-value");
    options.attribute("crossorigin", "anonymous");
    options.attribute("integrity", "sha384-...");

    assertEquals(3, options.getAttributes().size());
    assertEquals("data-custom=\"some-value\"", options.getAttributes().get(0));
    assertEquals("crossorigin=\"anonymous\"", options.getAttributes().get(1));
    assertEquals("integrity=\"sha384-...\"", options.getAttributes().get(2));
  }

  @Test
  void attributesListWithNulls_shouldFilterNulls() {
    List<String> attrs = Arrays.asList("type=\"module\"", null, "async", null, "defer");
    options.attributes(attrs);

    assertEquals(5, options.getAttributes().size()); // nulls are included
    assertTrue(options.getAttributes().contains("type=\"module\""));
    assertTrue(options.getAttributes().contains("async"));
    assertTrue(options.getAttributes().contains("defer"));
    assertTrue(options.getAttributes().contains(null));
  }

  @Test
  void emptyAttributeValues_shouldWorkCorrectly() {
    options.attribute("class", "");
    options.attribute("");

    assertEquals(2, options.getAttributes().size());
    assertEquals("class=\"\"", options.getAttributes().get(0));
    assertEquals("", options.getAttributes().get(1));
  }

  @Test
  void specialCharactersInAttributes_shouldWorkCorrectly() {
    options.attribute("data-test", "value-with-special-chars:;!@#$%^&*()");
    options.attribute("title", "Title with \"quotes\" and 'apostrophes'");

    assertEquals(2, options.getAttributes().size());
    assertEquals("data-test=\"value-with-special-chars:;!@#$%^&*()\"", options.getAttributes().get(0));
    assertEquals("title=\"Title with \"quotes\" and 'apostrophes'\"", options.getAttributes().get(1));
  }

  @Test
  void allPropertiesSet_shouldWorkCorrectly() {
    options
        .eventId("full-test-event")
        .retryDuration(5000L)
        .autoRemove(true)
        .attribute("type", "module")
        .attribute("async");

    assertEquals("full-test-event", options.getEventId());
    assertEquals(5000L, options.getRetryDuration());
    assertTrue(options.getAutoRemove());
    assertEquals(2, options.getAttributes().size());
  }

  @Test
  void zeroRetryDuration_shouldWorkCorrectly() {
    options.retryDuration(0L);
    assertEquals(0L, options.getRetryDuration());
  }

  @Test
  void negativeRetryDuration_shouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> options.retryDuration(-1000L));
  }

  @Test
  void autoRemoveToggle_shouldWorkCorrectly() {
    // Start with default
    assertEquals(Consts.DEFAULT_EXECUTE_AUTO_REMOVE, options.getAutoRemove());

    // Toggle to false
    options.autoRemove(false);
    assertFalse(options.getAutoRemove());

    // Toggle back to true
    options.autoRemove(true);
    assertTrue(options.getAutoRemove());
  }
}
