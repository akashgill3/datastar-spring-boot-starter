package io.github.akashgill3.datastar.events;

import io.github.akashgill3.datastar.Consts;
import io.github.akashgill3.datastar.DatastarSseEmitter;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration options for executing JavaScript in the browser.
 *
 * <p>This class provides configuration options for script execution, controlling how the script tag
 * is injected and managed, along with SSE delivery options (event ID and retry duration).
 *
 * <p>Note: This is not a Datastar event type. {@link DatastarSseEmitter#executeScript} is a
 * convenience method that internally uses {@link DatastarEventType#PATCH_ELEMENTS} to inject a
 * script tag.
 *
 * <p>When {@link #autoRemove(boolean)} is set to {@code true}, the script tag is automatically
 * removed from the DOM after execution (Default: {@code true}).
 *
 * <p>Typical usage:
 *
 * <pre>
 * sseEmitter.executeScript(script, options -> options
 *     .autoRemove(true)
 *     .attribute("type", "module")
 *     .eventId("script-1")
 *     .retryDuration(1000L));
 * </pre>
 *
 * @author Akash Gill
 */
public class ExecuteScriptOptions {

  private String eventId;
  private Long retryDuration;
  private boolean autoRemove = Consts.DEFAULT_EXECUTE_AUTO_REMOVE;
  private final List<String> attributes = new ArrayList<>();

  public ExecuteScriptOptions eventId(String eventId) {
    this.eventId = eventId;
    return this;
  }

  public ExecuteScriptOptions retryDuration(Long retryDuration) {
    if (retryDuration != null && retryDuration < 0) {
      throw new IllegalArgumentException("retryDuration must be >= 0");
    }
    this.retryDuration = retryDuration;
    return this;
  }

  public ExecuteScriptOptions autoRemove(boolean autoRemove) {
    this.autoRemove = autoRemove;
    return this;
  }

  public ExecuteScriptOptions attribute(String key, String value) {
    this.attributes.add("%s=\"%s\"".formatted(key, value));
    return this;
  }

  public ExecuteScriptOptions attribute(String attribute) {
    this.attributes.add(attribute);
    return this;
  }

  public ExecuteScriptOptions attributes(List<String> attributes) {
    this.attributes.addAll(attributes);
    return this;
  }

  public String getEventId() {
    return eventId;
  }

  public Long getRetryDuration() {
    return retryDuration;
  }

  public boolean getAutoRemove() {
    return autoRemove;
  }

  public List<String> getAttributes() {
    return attributes;
  }
}
