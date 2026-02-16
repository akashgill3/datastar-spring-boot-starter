package io.github.akashgill3.datastar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import io.github.akashgill3.datastar.events.ElementPatchMode;
import io.github.akashgill3.datastar.events.Namespace;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@ExtendWith(MockitoExtension.class)
class DatastarSseEmitterTest {

  @Mock private ServerHttpResponse mockResponse;

  private DatastarSseEmitter emitter;

  @BeforeEach
  void setUp() {
    emitter = new DatastarSseEmitter(new DatastarProperties(false));
  }

  @Test
  void constructor_withNullProperties_shouldWork() {
    // The constructor doesn't throw NPE, it accepts null properties
    DatastarSseEmitter emitter = new DatastarSseEmitter(null);
    assertNotNull(emitter);
  }

  @Test
  void constructor_withProperties_shouldUse60SecondDefaultTimeout() {
    DatastarProperties properties = new DatastarProperties(false);
    DatastarSseEmitter emitter = new DatastarSseEmitter(properties);
    assertNotNull(emitter);
    assertEquals(60_000L, emitter.getTimeout());
  }

  @Test
  void constructor_withPropertiesAndTimeout_shouldUseCustomTimeout() {
    DatastarProperties properties = new DatastarProperties(false);
    DatastarSseEmitter emitter = new DatastarSseEmitter(properties, -1L);
    assertNotNull(emitter);
    assertEquals(-1L, emitter.getTimeout());
  }

  @Test
  void patchElements_shouldSendCorrectSseWireFormat_withDefaults() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements("<div>Hello World</div>");

    verify((ResponseBodyEmitter) spyEmitter)
        .send(
            eq(
                """
                event: datastar-patch-elements
                data: elements <div>Hello World</div>

                """),
            eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchElements_shouldIncludeOnlyNonDefaultFields() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements(
        "<div>Hello</div>",
        opts -> {
          opts.selector("#target");
          opts.mode(ElementPatchMode.Prepend);
          opts.eventId("test-event");
          opts.retryDuration(2000L);
          opts.useViewTransition(true);
          opts.namespace(Namespace.SVG);
        });

    String expected =
        """
            event: datastar-patch-elements
            id: test-event
            retry: 2000
            data: selector #target
            data: mode prepend
            data: useViewTransition true
            data: namespace svg
            data: elements <div>Hello</div>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchElements_withNullElements_shouldNotAddElementsDataLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements(null);

    verify((ResponseBodyEmitter) spyEmitter)
        .send(eq("event: datastar-patch-elements\n\n"), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchElements_withEmptyElements_shouldNotAddElementsDataLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements("");

    verify((ResponseBodyEmitter) spyEmitter)
        .send(eq("event: datastar-patch-elements\n\n"), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchElements_withMultilineContent_shouldSplitIntoMultipleDataLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements("<div>Line 1</div>\n<div>Line 2</div>\n<div>Line 3</div>");

    String expected =
        """
            event: datastar-patch-elements
            data: elements <div>Line 1</div>
            data: elements <div>Line 2</div>
            data: elements <div>Line 3</div>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchSignals_shouldSendCorrectSseWireFormat_withDefaults() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchSignals("{\"name\":\"value\"}");

    verify((ResponseBodyEmitter) spyEmitter)
        .send(
            eq(
                """
                event: datastar-patch-signals
                data: signals {"name":"value"}

                """),
            eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchSignals_shouldIncludeOptionalFields_whenConfigured() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchSignals(
        "{\"name\":\"value\"}",
        opts -> {
          opts.eventId("test-event");
          opts.retryDuration(3000L);
          opts.onlyIfMissing(true);
        });

    String expected =
        """
            event: datastar-patch-signals
            id: test-event
            retry: 3000
            data: onlyIfMissing true
            data: signals {"name":"value"}

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchSignals_withNullSignals_shouldNotAddSignalsDataLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchSignals(null);

    verify((ResponseBodyEmitter) spyEmitter)
        .send(eq("event: datastar-patch-signals\n\n"), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchSignals_withEmptySignals_shouldNotAddSignalsDataLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchSignals("");

    verify((ResponseBodyEmitter) spyEmitter)
        .send(eq("event: datastar-patch-signals\n\n"), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void executeScript_shouldSendPatchElementsAppendToBody_withAutoRemoveByDefault()
      throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.executeScript("console.log('Hello World');");

    String expected =
        """
            event: datastar-patch-elements
            data: selector body
            data: mode append
            data: elements <script data-effect="el.remove()">console.log('Hello World');</script>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void executeScript_shouldMapOptions_toPatchElementsOptions_andScriptAttributes()
      throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.executeScript(
        "console.log('Hello World');",
        opts -> {
          opts.eventId("script-event");
          opts.retryDuration(1500L);
          opts.autoRemove(true);
          opts.attributes(java.util.Arrays.asList("type=\"module\"", "async"));
        });

    String expected =
        """
            event: datastar-patch-elements
            id: script-event
            retry: 1500
            data: selector body
            data: mode append
            data: elements <script data-effect="el.remove()" type="module" async>console.log('Hello World');</script>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void executeScript_withNullScript_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          emitter.executeScript(null);
        });
  }

  @Test
  void executeScript_withEmptyScript_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          emitter.executeScript("");
        });
  }

  @Test
  void executeScript_withScriptTag_shouldThrowException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          emitter.executeScript("console.log('</script>');");
        });
  }

  @Test
  void consoleLog_shouldEscapeMessage_andWrapInConsoleLogCall() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.consoleLog("Special \"chars\"\nline2");

    String expectedScript = "console.log(\"Special \\\"chars\\\"\\nline2\")";
    String expected =
        "event: datastar-patch-elements\n"
            + "data: selector body\n"
            + "data: mode append\n"
            + "data: elements <script data-effect=\"el.remove()\">"
            + expectedScript
            + "</script>\n\n";
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void consoleError_shouldEscapeMessage_andWrapInConsoleErrorCall() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.consoleError("err\tmsg");

    String expectedScript = "console.error(\"err\\tmsg\")";
    String expected =
        "event: datastar-patch-elements\n"
            + "data: selector body\n"
            + "data: mode append\n"
            + "data: elements <script data-effect=\"el.remove()\">"
            + expectedScript
            + "</script>\n\n";
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void redirect_shouldWrapUrlInSetTimeoutWindowLocation() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.redirect("https://example.com/path?x=1");

    String expectedScript =
        "setTimeout(() => window.location.href = \"https://example.com/path?x=1\")";
    String expected =
        "event: datastar-patch-elements\n"
            + "data: selector body\n"
            + "data: mode append\n"
            + "data: elements <script data-effect=\"el.remove()\">"
            + expectedScript
            + "</script>\n\n";
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void replaceUrl_shouldWrapUrlInHistoryReplaceState() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.replaceUrl("/new-path");

    String expectedScript = "setTimeout(() => window.history.replaceState({}, '', \"/new-path\"))";
    String expected =
        "event: datastar-patch-elements\n"
            + "data: selector body\n"
            + "data: mode append\n"
            + "data: elements <script data-effect=\"el.remove()\">"
            + expectedScript
            + "</script>\n\n";
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void convenienceMethods_shouldPropagateEventIdAndRetry_toUnderlyingPatchElements()
      throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.consoleLog("hi", opts -> opts.eventId("e1").retryDuration(2000L).autoRemove(true));

    String expectedScript = "console.log(\"hi\")";
    String expected =
        "event: datastar-patch-elements\n"
            + "id: e1\n"
            + "retry: 2000\n"
            + "data: selector body\n"
            + "data: mode append\n"
            + "data: elements <script data-effect=\"el.remove()\">"
            + expectedScript
            + "</script>\n\n";
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void extendResponse_shouldSetCorrectHeaders() {
    when(mockResponse.getHeaders()).thenReturn(new org.springframework.http.HttpHeaders());

    emitter.extendResponse(mockResponse);

    assertEquals(MediaType.TEXT_EVENT_STREAM, mockResponse.getHeaders().getContentType());
    assertEquals("no-cache", mockResponse.getHeaders().getCacheControl());
  }

  @Test
  void extendResponse_shouldNotOverrideExistingContentType() {
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    when(mockResponse.getHeaders()).thenReturn(headers);

    emitter.extendResponse(mockResponse);

    assertEquals(MediaType.APPLICATION_JSON, mockResponse.getHeaders().getContentType());
    assertEquals("no-cache", mockResponse.getHeaders().getCacheControl());
  }

  @Test
  void patchElements_shouldHandleCrlfAndSkipBlankLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchElements("<div>1</div>\r\n\r\n   \n<div>2</div>\n<div>3</div>");

    String expected =
        """
            event: datastar-patch-elements
            data: elements <div>1</div>
            data: elements <div>2</div>
            data: elements <div>3</div>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void patchSignals_shouldSplitMultilineJsonAndSkipBlankLines() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.patchSignals("{\n  \"a\": 1\n\n  ,\"b\": 2\n}");

    String expected =
        """
            event: datastar-patch-signals
            data: signals {
            data: signals   "a": 1
            data: signals   ,"b": 2
            data: signals }

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }

  @Test
  void executeScript_withAutoRemoveFalse_shouldOmitDataEffectAttribute() throws IOException {
    DatastarSseEmitter spyEmitter = spy(emitter);
    doNothing().when((ResponseBodyEmitter) spyEmitter).send(any(), any(MediaType.class));

    spyEmitter.executeScript("console.log('test');", opts -> opts.autoRemove(false));

    String expected =
        """
            event: datastar-patch-elements
            data: selector body
            data: mode append
            data: elements <script>console.log('test');</script>

            """;
    verify((ResponseBodyEmitter) spyEmitter).send(eq(expected), eq(MediaType.TEXT_PLAIN));
  }
}
