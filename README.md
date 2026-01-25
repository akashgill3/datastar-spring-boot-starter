# Datastar Spring Boot Starter

A lightweight **Spring Boot starter** that autoconfigures a `Datastar` helper for **Server-Sent Events (SSE)** 
integrations, with simple event types for patching elements and signals.

## Requirements
- Java **21**
- Spring Boot **4.x**
- Maven (or use the included Maven Wrapper)

## Install
TODO

## Usage
 TODO
``` java
 ``` 

## Configuration

Configure via `application.yml` / `application.properties` under the `datastar` prefix:

| Property                              | Default | Description                                                          |
|---------------------------------------|--------:|----------------------------------------------------------------------|
| `datastar.timeout`                    |   `300` | SSE timeout (seconds)                                                |
| `datastar.max-concurrent-connections` |  `1000` | Max number of concurrent SSE connections                             |
| `datastar.debug-logging`              | `false` | Enables debug logs for formatted SSE events and connection lifecycle |

Example:

``` yaml 
datastar: 
    timeout: 300
    retry-duration: 1000
    debug-logging: false
    max-concurrent-connections: 1000
```

## Build

``` bash 
./mvnw clean verify
``` 
