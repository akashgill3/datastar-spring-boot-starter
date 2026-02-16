package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import io.github.akashgill3.datastar.Consts;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class PatchElementOptionsTest {

  private PatchElementOptions options;

  @BeforeEach
  void setUp() {
    options = new PatchElementOptions();
  }

  @Test
  void defaultValues_shouldBeCorrect() {
    assertNull(options.getEventId());
    assertEquals(Consts.DEFAULT_SSE_RETRY_DURATION_MS, options.getRetryDuration());
    assertNull(options.getSelector());
    assertEquals(Consts.DEFAULT_ELEMENT_PATCH_MODE, options.getMode());
    assertEquals(Consts.DEFAULT_ELEMENTS_USE_VIEW_TRANSITIONS, options.isUseViewTransition());
    assertEquals(Consts.DEFAULT_NAMESPACE, options.getNamespace());
  }

  @Test
  void eventId_shouldUpdateValue() {
    PatchElementOptions result = options.eventId("test-event");
    assertSame(options, result); // Should return this for chaining
    assertEquals("test-event", options.getEventId());
  }

  @Test
  void retryDuration_shouldUpdateValue() {
    PatchElementOptions result = options.retryDuration(2000L);
    assertSame(options, result);
    assertEquals(2000L, options.getRetryDuration());
  }

  @Test
  void selector_shouldUpdateValue() {
    PatchElementOptions result = options.selector("#target");
    assertSame(options, result);
    assertEquals("#target", options.getSelector());
  }

  @ParameterizedTest
  @EnumSource(ElementPatchMode.class)
  void mode_shouldUpdateValue(ElementPatchMode mode) {
    PatchElementOptions result = options.mode(mode);
    assertSame(options, result);
    assertEquals(mode, options.getMode());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void useViewTransition_shouldUpdateValue(boolean useViewTransition) {
    PatchElementOptions result = options.useViewTransition(useViewTransition);
    assertSame(options, result);
    assertEquals(useViewTransition, options.isUseViewTransition());
  }

  @ParameterizedTest
  @EnumSource(Namespace.class)
  void namespace_shouldUpdateValue(Namespace namespace) {
    PatchElementOptions result = options.namespace(namespace);
    assertSame(options, result);
    assertEquals(namespace, options.getNamespace());
  }

  @Test
  void methodChaining_shouldWorkCorrectly() {
    PatchElementOptions result = options
        .eventId("chain-event")
        .retryDuration(3000L)
        .selector(".chain-selector")
        .mode(ElementPatchMode.Prepend)
        .useViewTransition(true)
        .namespace(Namespace.SVG);

    assertSame(options, result);
    assertEquals("chain-event", options.getEventId());
    assertEquals(3000L, options.getRetryDuration());
    assertEquals(".chain-selector", options.getSelector());
    assertEquals(ElementPatchMode.Prepend, options.getMode());
    assertTrue(options.isUseViewTransition());
    assertEquals(Namespace.SVG, options.getNamespace());
  }

  @Test
  void eventId_withNull_shouldUpdateValue() {
    options.eventId("test");
    options.eventId(null);
    assertNull(options.getEventId());
  }

  @Test
  void selector_withNull_shouldUpdateValue() {
    options.selector("#test");
    options.selector(null);
    assertNull(options.getSelector());
  }

  @Test
  void retryDuration_withNull_shouldUpdateValue() {
    options.retryDuration(1000L);
    options.retryDuration(null);
    assertNull(options.getRetryDuration());
  }

  @ParameterizedTest
  @ValueSource(longs = {0L, 1L, 1000L, Long.MAX_VALUE})
  void retryDuration_withValidValues_shouldUpdateValue(long retryDuration) {
    options.retryDuration(retryDuration);
    assertEquals(retryDuration, options.getRetryDuration());
  }

  @Test
  void selector_withEmptyString_shouldUpdateValue() {
    options.selector("");
    assertEquals("", options.getSelector());
  }

  @Test
  void eventId_withEmptyString_shouldUpdateValue() {
    options.eventId("");
    assertEquals("", options.getEventId());
  }

  @Test
  void multipleChanges_shouldWorkCorrectly() {
    // Make multiple changes
    options.eventId("event1");
    options.retryDuration(1500L);
    options.selector("#sel1");
    options.mode(ElementPatchMode.Inner);
    options.useViewTransition(false);
    options.namespace(Namespace.MATHML);

    assertEquals("event1", options.getEventId());
    assertEquals(1500L, options.getRetryDuration());
    assertEquals("#sel1", options.getSelector());
    assertEquals(ElementPatchMode.Inner, options.getMode());
    assertFalse(options.isUseViewTransition());
    assertEquals(Namespace.MATHML, options.getNamespace());

    // Change again
    options.eventId("event2");
    options.retryDuration(2500L);
    options.selector("#sel2");
    options.mode(ElementPatchMode.After);
    options.useViewTransition(true);
    options.namespace(Namespace.HTML);

    assertEquals("event2", options.getEventId());
    assertEquals(2500L, options.getRetryDuration());
    assertEquals("#sel2", options.getSelector());
    assertEquals(ElementPatchMode.After, options.getMode());
    assertTrue(options.isUseViewTransition());
    assertEquals(Namespace.HTML, options.getNamespace());
  }

  @Test
  void complexSelector_shouldWorkCorrectly() {
    String complexSelector = "div.container > ul.items li.item[data-type='important']:first-child";
    options.selector(complexSelector);
    assertEquals(complexSelector, options.getSelector());
  }

  @Test
  void allPatchModes_shouldBeConfigurable() {
    ElementPatchMode[] allModes = ElementPatchMode.values();
    
    for (ElementPatchMode mode : allModes) {
      options.mode(mode);
      assertEquals(mode, options.getMode());
    }
  }

  @Test
  void allNamespaces_shouldBeConfigurable() {
    Namespace[] allNamespaces = Namespace.values();
    
    for (Namespace namespace : allNamespaces) {
      options.namespace(namespace);
      assertEquals(namespace, options.getNamespace());
    }
  }
}
