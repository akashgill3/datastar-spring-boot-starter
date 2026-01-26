# Datastar Spring Boot Starter

A lightweight **Spring Boot starter** that autoconfigures a `Datastar` helper for **Server-Sent Events (SSE)**
integrations, with simple event types for patching elements and signals.

## Quick start

Create an SSE endpoint that returns a `DatastarSseEmitter` and write events asynchronously.

```java 
package com.example.demo;

import io.github.akashgill3.datastar.Datastar;
import io.github.akashgill3.datastar.DatastarSseEmitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SseController {
    private final Datastar datastar;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public SseController(Datastar datastar) {
        this.datastar = datastar;
    }

    @GetMapping(path = "/sse")
    public DatastarSseEmitter sse() {
        DatastarSseEmitter sse = datastar.createEmitter();
        executor.execute(() -> {
            try {
                PatchElementsEvent event = PatchElementsEvent.of("<div id=\"content\">Hello from Datastar</div>");
                sse.patchElements(event);

                PatchSignalsEvent signalEvent = PatchSignalsEvent.of("{\"message\":\"Hello from Datastar\"}");
                sse.patchSignals(signalEvent);

                sse.executeScript("alert(\"hello from the server\");");
                sse.complete();
            } catch (Exception e) {
                sse.completeWithError(e);
            }
        });
        return sse;
    }
}
```

## Sending events

### Patch DOM elements

```java 
import io.github.akashgill3.datastar.events.ElementPatchMode;
import io.github.akashgill3.datastar.events.PatchElementOptions;

// ...
DatastarSseEmitter sse = datastar.createEmitter();
        PatchElementOptions options = PatchElementOptions.builder().selector("#content").mode(ElementPatchMode.Outer).build();
        PatchElementsEvent event = PatchElementsEvent.withOptions("<div>Updated</div>", options);
sse.

        patchElements(event);
// ...
```

### Patch signals

```java


// ...
String signals = """
        { "user": { "id": 5432 } }
        """;
sse.

        patchSignals(PatchSignalsEvent.of(signals));
```

### Execute script / console logging / navigation helpers

```java
// Execute an arbitrary script
sse.executeScript("console.log('Hello from server');");

// Convenience helpers 
sse.consoleLog("This goes to the browser console"); 
sse.consoleError("This is an error message"); 
sse.redirect("/somewhere-else"); 
sse.replaceUrl("/url-without-reload");
```

### ExecuteScriptOptions (attributes, event id, retry)

```java

import io.github.akashgill3.datastar.events.ExecuteScriptOptions;

// ...
ExecuteScriptOptions opts = ExecuteScriptOptions.builder()
        .autoRemove(true)
        .attribute("type", "module")
        .eventId("script-1")
        .retryDuration(1000L)
        .build();

sse.executeScript("console.log('module script')", opts);
```

## Install (using JitPack)

### Maven
Add to `pom.xml`
```xml
<!--This repository is required-->
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
Add it in your root settings.gradle at the end of repositories:
```groovy
//Add it in your root settings.gradle at the end of repositories:
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
    implementation 'com.github.akashgill3:datastar-spring-boot-starter:0.2.0'
}
```



## Requirements

- Java **21**
- Spring Boot **4.x**
- Maven (or use the included Maven Wrapper)

## Configuration

Configure via `application.yml` / `application.properties` under the `datastar` prefix:

| Property                              | Default | Description                                                          |
|---------------------------------------|--------:|----------------------------------------------------------------------|
| `datastar.max-concurrent-connections` |  `1000` | Max number of concurrent SSE connections                             |
| `datastar.debug-logging`              | `false` | Enables debug logs for formatted SSE events and connection lifecycle |

Example:

``` yaml 
datastar: 
    max-concurrent-connections: 1000
    debug-logging: false
```

## Build

``` bash 
./mvnw clean verify
``` 
