# Changelog

## [0.3.1] - 28-01-2026

### Added

- `deployment` profile for Maven Central deployment

### Removed

- Dependency on `spring-boot-starter-web` and others from initial project structure

---

## [0.3.0] - 26-01-2026

### Changed

- Redesigned core SDK functions to accept elements and signals directly.
- Updated options to use functional configuration.
- Renamed `debug-logging` property to `enable-logging`.

### Removed

- `DatastarEvent`, `PatchElementsEvent` and `PatchSignalsEvent` entities in favor of using elements directly and
  options.

---

## [0.2.0] - 25-01-2026

### Added

- Implemented remaining SDK methods and supporting helper utilities.
- Added comprehensive Javadocs to all public classes.

### Changed

- Restructured the `events` package for better organization and usability.
- Removed a few properties from DatastarProperties.

### Fixed

- Fixed minor typos in code and documentation.

---

## [0.1.0] - 24-01-2026

### Added

- Basic SSE emitter support
- PatchElements and PatchSignals events
- Autoconfiguration for Spring Boot