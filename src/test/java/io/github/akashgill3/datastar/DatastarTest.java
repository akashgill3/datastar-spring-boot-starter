package io.github.akashgill3.datastar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.akashgill3.datastar.autoconfigure.DatastarProperties;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import tools.jackson.core.exc.StreamReadException;

@ExtendWith(MockitoExtension.class)
class DatastarTest {

  @Mock private HttpServletRequest mockRequest;

  private Datastar datastar;

  @BeforeEach
  void setUp() {
    datastar = new Datastar(new DatastarProperties(false));
  }

  @Test
  void createEmitter_shouldCreateEmitter() {
    DatastarSseEmitter emitter = datastar.createEmitter();
    assertNotNull(emitter);
  }

  @ParameterizedTest(name = "timeout={0}")
  @ValueSource(longs = {0L, 30_000L, Long.MAX_VALUE, -1L})
  void createEmitter_withTimeout_shouldCreateEmitter(long timeout) {
    DatastarSseEmitter emitter = datastar.createEmitter(timeout);
    assertNotNull(emitter);
    assertEquals(timeout, emitter.getTimeout());
  }

  @Test
  void readSignals_getRequestWithNoDatastarParam_shouldReturnEmptyObject() throws IOException {
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockRequest.getParameter(Consts.DATASTAR_KEY)).thenReturn(null);

    TestSignals result = datastar.readSignals(mockRequest, TestSignals.class);
    
    assertNotNull(result);
    assertNull(result.value());
  }

  @Test
  void readSignals_getRequestWithEmptyDatastarParam_shouldReturnEmptyObject() throws IOException {
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockRequest.getParameter(Consts.DATASTAR_KEY)).thenReturn("");

    TestSignals result = datastar.readSignals(mockRequest, TestSignals.class);
    
    assertNotNull(result);
    assertNull(result.value());
  }

  @Test
  void readSignals_getRequestWithDatastarParam_shouldParseJson() throws IOException {
    String jsonPayload = "{\"value\":\"test\"}";
    when(mockRequest.getMethod()).thenReturn("GET");
    when(mockRequest.getParameter(Consts.DATASTAR_KEY)).thenReturn("%7B%22value%22%3A%22test%22%7D");

    TestSignals result = datastar.readSignals(mockRequest, TestSignals.class);
    
    assertNotNull(result);
    assertEquals("test", result.value());
  }

  @Test
  void readSignals_postRequest_shouldParseJsonFromBody() throws IOException {
    String jsonPayload = "{\"value\":\"test\"}";
    MockHttpServletRequest postRequest = new MockHttpServletRequest("POST", "/test");
    postRequest.setContent(jsonPayload.getBytes());

    TestSignals result = datastar.readSignals(postRequest, TestSignals.class);
    
    assertNotNull(result);
    assertEquals("test", result.value());
  }

  @Test
  void readSignals_invalidJson_shouldThrowIOException() {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
    request.setContent("invalid json".getBytes());

    // Jackson throws StreamReadException, which extends IOException
    assertThrows(StreamReadException.class, () -> {
      datastar.readSignals(request, TestSignals.class);
    });
  }

  @Test
  void constructor_withNullProperties_shouldWork() {
    // The constructor doesn't throw NPE, it accepts null properties
    Datastar datastar = new Datastar(null);
    assertNotNull(datastar);
  }

  record TestSignals(String value) {}
}
