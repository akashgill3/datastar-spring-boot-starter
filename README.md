# Datastar Spring Boot Starter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.akashgill3/datastar-spring-boot-starter)](https://central.sonatype.com/artifact/io.github.akashgill3/datastar-spring-boot-starter)

[Datastar](https://data-star.dev/) is a lightweight hypermedia framework for building everything from simple sites to
real-time collaborative web apps.

This **Spring Boot Starter** provides everything you need to integrate Datastar into your applications. It handles the
low-level Server-Sent Events (SSE) transport and exposes a clean, type-safe API for emitting Datastar protocol events
directly from your Spring controllers.

## Features

- **`DatastarSseEmitter`**: A Spring MVC–native, push-based emitter for streaming Datastar protocol events. While the
  Datastar SDK refers to a `ServerSentEventGenerator`, Spring models long-lived responses as emitters; accordingly,
  this class extends ResponseBodyEmitter and preserves Datastar’s custom wire format.
- **Fluent API**: Easily emit element patches, signal updates, and script executions using a minimal API with sensible
  defaults and functional configuration options.
- **Spring Boot Autoconfiguration**: Zero-configuration setup for common use cases.
- **Asynchronous & Virtual Thread Friendly**: Designed to work seamlessly with Spring's async support and Java 21+
  virtual threads.

## Quick Start

1. **Add the dependency** (see [Installation](#installation) below).
2. **Create a Controller** that returns a `DatastarSseEmitter`.

```java
@RestController
public class SseController {
  private final Datastar datastar;
  private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

  public SseController(Datastar datastar) {
    this.datastar = datastar;
  }

  @PostMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public DatastarSseEmitter sse(HttpServletRequest request) throws IOException {
    // 1. Read signals from request
    MySignals signals = datastar.readSignals(request, MySignals.class);
    
    DatastarSseEmitter sseEmitter = datastar.createEmitter();

    executor.execute(() -> {
      try {
        // 2. Patch the DOM
        sseEmitter.patchElements("<div id=\"content\">Hello " + signals.getName() + " from Datastar!</div>");

        // 3. Update client-side signals (state)
        sseEmitter.patchSignals("{\"message\": \"Updated state\"}");

        // 4. Execute a script
        sseEmitter.executeScript("alert('Action performed!')");

        sseEmitter.complete();
      } catch (Exception e) {
        sseEmitter.completeWithError(e);
      }
    });

    return sseEmitter;
  }
  
  record MySignals(String name, String message) {}
}
```

## Sending Events

### Patch DOM Elements

The core of Datastar is patching the DOM. You can send any HTML fragment, and Datastar will efficiently update the
browser.

```java
import io.github.akashgill3.datastar.events.ElementPatchMode;

var html = """
    <div id="status" class="alert">
        Operation successful!
    </div>
    """;
sseEmitter.patchElements(html, options -> options.selector("#status-bar").mode(ElementPatchMode.Append)); 
```

### Patch Signals

Update the client-side state (signals) using JSON Merge Patch.

```java
sseEmitter.patchSignals("{ \"user\": { \"isLoggedIn\": true } }");
```

### Receiving Signals

Datastar sends signals back to the server as JSON. Use `readSignals` to parse them.

```java
// In your controller
MySignals signals = datastar.readSignals(request, MySignals.class);
```

For `GET` requests, it automatically looks for the `datastar` query parameter. For other methods, it reads the request body.

### Navigation & Scripting

Helper methods for common client-side actions.

```java
sseEmitter.executeScript("alert('Action performed!')");
sseEmitter.redirect("/dashboard");
sseEmitter.replaceUrl("/new-path");
sseEmitter.consoleLog("Debugging info");
sseEmitter.consoleError("Error message");
```

## Installation

### Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.akashgill3</groupId>
    <artifactId>datastar-spring-boot-starter</artifactId>
    <version>0.3.2</version>
    <scope>compile</scope>
</dependency>
```

### Gradle

And the dependency to `build.gradle`:

```groovy
dependencies {
    implementation("io.github.akashgill3:datastar-spring-boot-starter:0.3.2")
}
```

## Configuration

Configure behavior in your `application.yml` or `application.properties`:

| Property                              | Default | Description                                                                 |
|:--------------------------------------|:--------|:----------------------------------------------------------------------------|
| `datastar.enable-logging`             | `false` | Enables detailed debug logging for every SSE event sent.                    |

## Requirements

- **Java 21+**
- **Spring Boot 4.x**

## Build

```bash
./mvnw clean verify
```
