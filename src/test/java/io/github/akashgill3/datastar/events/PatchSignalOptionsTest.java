package io.github.akashgill3.datastar.events;

import static org.junit.jupiter.api.Assertions.*;

import io.github.akashgill3.datastar.Consts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PatchSignalOptionsTest {

  private PatchSignalOptions options;

  @BeforeEach
  void setUp() {
    options = new PatchSignalOptions();
  }

  @Test
  void defaultValues_shouldBeCorrect() {
    assertNull(options.getEventId());
    assertEquals(Consts.DEFAULT_SSE_RETRY_DURATION_MS, options.getRetryDuration());
    assertEquals(Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING, options.isOnlyIfMissing());
  }

  @Test
  void eventId_shouldUpdateValue() {
    PatchSignalOptions result = options.eventId("test-event");
    assertSame(options, result); // Should return this for chaining
    assertEquals("test-event", options.getEventId());
  }

  @Test
  void retryDuration_shouldUpdateValue() {
    PatchSignalOptions result = options.retryDuration(2000L);
    assertSame(options, result);
    assertEquals(2000L, options.getRetryDuration());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void onlyIfMissing_shouldUpdateValue(boolean onlyIfMissing) {
    PatchSignalOptions result = options.onlyIfMissing(onlyIfMissing);
    assertSame(options, result);
    assertEquals(onlyIfMissing, options.isOnlyIfMissing());
  }

  @Test
  void methodChaining_shouldWorkCorrectly() {
    PatchSignalOptions result = options
        .eventId("chain-event")
        .retryDuration(3000L)
        .onlyIfMissing(true);

    assertSame(options, result);
    assertEquals("chain-event", options.getEventId());
    assertEquals(3000L, options.getRetryDuration());
    assertTrue(options.isOnlyIfMissing());
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

  @ParameterizedTest
  @ValueSource(longs = {0L, 1L, 1000L, Long.MAX_VALUE})
  void retryDuration_withValidValues_shouldUpdateValue(long retryDuration) {
    options.retryDuration(retryDuration);
    assertEquals(retryDuration, options.getRetryDuration());
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
    options.onlyIfMissing(true);

    assertEquals("event1", options.getEventId());
    assertEquals(1500L, options.getRetryDuration());
    assertTrue(options.isOnlyIfMissing());

    // Change again
    options.eventId("event2");
    options.retryDuration(2500L);
    options.onlyIfMissing(false);

    assertEquals("event2", options.getEventId());
    assertEquals(2500L, options.getRetryDuration());
    assertFalse(options.isOnlyIfMissing());
  }

  @Test
  void complexEventId_shouldWorkCorrectly() {
    String complexEventId = "signal-patch-2024-01-15-12:34:56.789";
    options.eventId(complexEventId);
    assertEquals(complexEventId, options.getEventId());
  }

  @Test
  void onlyIfMissingToggle_shouldWorkCorrectly() {
    // Start with default
    assertEquals(Consts.DEFAULT_PATCH_SIGNAL_ONLY_IF_MISSING, options.isOnlyIfMissing());

    // Toggle to true
    options.onlyIfMissing(true);
    assertTrue(options.isOnlyIfMissing());

    // Toggle to false
    options.onlyIfMissing(false);
    assertFalse(options.isOnlyIfMissing());

    // Toggle back to true
    options.onlyIfMissing(true);
    assertTrue(options.isOnlyIfMissing());
  }

  @Test
  void allPropertiesSet_shouldWorkCorrectly() {
    options
        .eventId("full-test-event")
        .retryDuration(5000L)
        .onlyIfMissing(true);

    assertEquals("full-test-event", options.getEventId());
    assertEquals(5000L, options.getRetryDuration());
    assertTrue(options.isOnlyIfMissing());
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
}
