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
        DatastarSseEmitter sseEmitter = datastar.createEmitter();
        executor.execute(() -> {
            try {
                sseEmitter.patchElements("<div id=\"content\">Hello from Datastar</div>");

                sseEmitter.patchSignals("{\"message\":\"Hello from Datastar\"}");

                sseEmitter.executeScript("alert(\"hello from the server\");");
                sseEmitter.complete();
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });
        return sseEmitter;
    }
}
```

## Sending events

### Patch DOM elements

```java 
import io.github.akashgill3.datastar.events.ElementPatchMode;

// ...
DatastarSseEmitter sseEmitter = datastar.createEmitter();
sseEmitter.patchElements("<div>Updated</div>", options -> options
        .selector("#content")
        .mode(ElementPatchMode.Outer));
// ...
```

### Patch signals

```java


// ...
sseEmitter.patchSignals("{\"user\": {\"id\": 5432 }}");
```

### Execute script / console logging / navigation helpers

```java
// Execute an arbitrary script
sseEmitter.executeScript("console.log('Hello from server');");

// Convenience helpers 
sseEmitter.consoleLog("This goes to the browser console"); 
sseEmitter.consoleError("This is an error message"); 
sseEmitter.redirect("/somewhere-else"); 
sseEmitter.replaceUrl("/url-without-reload");
```

### ExecuteScriptOptions (attributes, event id, retry)

```java
// ...
sseEmitter.executeScript("console.log('module script')", options -> options
        .autoRemove(true)
        .attribute("type", "module")
        .eventId("script-1")
        .retryDuration(1000L));
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
| `datastar.enable-logging`             | `false` | Enables debug logs for formatted SSE events and connection lifecycle |

Example:

``` yaml 
datastar: 
    max-concurrent-connections: 1000
    enable-logging: false
```

## Build

``` bash 
./mvnw clean verify
``` 
