# Datastar Spring Boot Starter

[![JitPack](https://jitpack.io/v/akashgill3/datastar-spring-boot-starter.svg)](https://jitpack.io/#akashgill3/datastar-spring-boot-starter)

[Datastar](https://data-star.dev/) is a lightweight hypermedia framework for building everything from simple sites to
real-time collaborative web apps.

This **Spring Boot Starter** provides everything you need to integrate Datastar into your applications. It handles the
low-level Server-Sent Events (SSE) protocol and provides a clean, type-safe API for patching the DOM and managing
client-side state directly from your Spring controllers.

## Features

- **`DatastarSseEmitter`**: A specialized `SseEmitter` implementation that follows the Datastar SDK specification.
- **Fluent API**: Easily send element patches, signal updates, and execute scripts using a minimal API with defaults
  and functional configuration options.
- **Spring Boot Autoconfiguration**: Zero-configuration setup for common use cases.
- **Lifecycle Management**: Built-in tracking of concurrent connections and robust error handling.
- **Asynchronous & Virtual Thread Friendly**: Designed to work seamlessly with Spring's async support and Java 21+
  virtual threads.

## Quick Start

1. **Add the dependency** (see [Installation](#installation-using-jitpack) below).
2. **Create a Controller** that returns a `DatastarSseEmitter`.

```java

@RestController
public class SseController {
    private final Datastar datastar;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public SseController(Datastar datastar) {
        this.datastar = datastar;
    }

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public DatastarSseEmitter sse() {
        DatastarSseEmitter sseEmitter = datastar.createEmitter();

        executor.execute(() -> {
            try {
                // 1. Patch the DOM
                sseEmitter.patchElements("<div id=\"content\">Hello from Datastar!</div>");

                // 2. Update client-side signals (state)
                sseEmitter.patchSignals("{\"message\": \"Updated state\"}");

                // 3. Execute a script
                sseEmitter.executeScript("alert('Action performed!')");

                sseEmitter.complete();
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });

        return sseEmitter;
    }
}
```

## Sending Events

### Patch DOM Elements

The core of Datastar is patching the DOM. You can send any HTML fragment, and Datastar will efficiently update the
browser.

```java
import io.github.akashgill3.datastar.events.ElementPatchMode;

sseEmitter.patchElements("""
    <div id="status" class="alert">
        Operation successful!
    </div>
    """,options ->options
        .

selector("#status-bar")
        .

mode(ElementPatchMode.Append)
        .

useViewTransition(true)
);
```

### Patch Signals

Update the client-side state (signals) using JSON Merge Patch.

```java
sseEmitter.patchSignals("{ \"user\": { \"isLoggedIn\": true } }");
```

### Navigation & Scripting

Helper methods for common client-side actions.

```java
sseEmitter.executeScript("alert('Action performed!')");
sseEmitter.

redirect("/dashboard");
sseEmitter.

replaceUrl("/new-path");
sseEmitter.

consoleLog("Debugging info");
sseEmitter.

consoleError("Error message");
```

## Installation (using JitPack)

### Maven

Add the repository and dependency to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
<groupId>com.github.akashgill3</groupId>
<artifactId>datastar-spring-boot-starter</artifactId>
<version>0.2.0</version>
</dependency>
```

### Gradle

Add the repository to `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

And the dependency to `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.akashgill3:datastar-spring-boot-starter:0.2.0'
}
```

## Configuration

Configure behavior in your `application.yml` or `application.properties`:

| Property                              | Default | Description                                                                 |
|:--------------------------------------|:--------|:----------------------------------------------------------------------------|
| `datastar.max-concurrent-connections` | `1000`  | Limits the number of active SSE connections to prevent resource exhaustion. |
| `datastar.enable-logging`             | `false` | Enables detailed debug logging for every SSE event sent.                    |

## Requirements

- **Java 21+**
- **Spring Boot 4.x**

## Build

```bash
./mvnw clean verify
```
