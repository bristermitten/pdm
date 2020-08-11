# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
this project does not yet adhere to semantic versioning as it's in very early stages. 


## [Unreleased]

## [0.0.1] - 2020-06-29
Basic functionality, buggy

*Versions between 0.0.2 and 0.0.8 are not on Gradle Plugins Portal due to a technical fault*
## [0.0.2] - 2020-07-29
### Added
- Simple example of Kotlin usage to README.MD and example module
- publish-all.sh, a script for publishing all modules
- A simple Unit Test suite
- A demo gif to README.MD
- Maven Placeholder/Property support, meaning things like ${hikari.version} should be replaced with the appropriate values
- Add `PDMBuilder`, a builder class for constructing a `PluginDependencyManager`
### Fixed
- An inverted boolean in SpigotRepository#download that would throw a warning if the server's spigot version is "downloaded"
- The DependencyManager cache not actually removing values
- The Gradle plugin throwing an exception about not being able to find Gson
### Changed
- Use OS channel based file writing when available, potential performance improvements
- Repositories now provide `InputStream`s rather than byte arrays wherever possible
- A `Function<String, Logger>` is now passed around rather than a single Logger instance
- README.MD is now a bit more detailed and shows the more recent file size of around 50 KB
### Removed
 - The logging side effect from `PluginDependencyManager#loadAllDependencies`
 - The redundant plugin url specification from settings.gradle
 - The redundant @NotNull from `HTTPService#ping` as it returns a primitive type
 
 ## [0.0.3] - 2020-08-5
 ### Added
 - More Unit Tests
 ### Changed
 - Improved the version extraction process to support Jitpack dependencies now
 
## [0.0.4] - 2020-08-5
### Changed
  - Cleaned up the buildscripts

*Versions from 0.0.8 are on the Gradle Plugins Portal*
## [0.0.8] - 2020-08-7
### Changed
  - No changes, just needed to update as Gradle Publishing had corrupted previous versions

## [0.0.9] - 2020-08-7
### Added
  - Add a method to `PDMBuilder` that can use the Plugin's ClassLoader to do dependency loading pre-plugin initialization
  - Add a slightly more detailed error message to SnapshotArtifact and potentially fix a bug in PDM.kt
  

## [0.0.10] - 2020-08-7
### Fixed
  - Fixed tests failing because they couldn't find Gson classes
  - SnapshotArtifact will no longer attempt to parse a POM if the URL is invalid
### Changed
  - Remove a few unused imports from `PluginDependencyManager`
  - `publish-all.sh` now runs tests before publishing
  
## [0.0.11] - 2020-08-7
### Fixed
 - The Gradle plugin now adds the `java` plugin to ensure that the `compile` configuration is always available
 - Broken imports in DefaultParseProcessTest.kt
 - The buildscripts now process environment variables correctly for publishing
### Changed
 - Cleaned up all the buildscripts
 - Modernised the example module
### Removed
 - Type arguments from `PluginDependencyManager`'s Javadocs
 - KtLint dependency as it was never used
 - `publish-all.sh` no longer runs tests as the CI does that
### Added
 - CI with Travis
 - A build status to README.MD

## [0.0.12] - 2020-08-11
### Added
 - Some shields to README.MD about the latest versions
 - A JSON schema for `dependencies.json`
 - Support for parent pom parsing
 - Another Unit Test
### Changed
 - Reworded a lot of README.MD
 - The pom parsing process has been reworked to accommodate the parent pom support
 
## [0.0.13-0.0.15] - 2020-08-11
 - *Skipped because of Gradle again :/*

## [0.0.16] - 2020-08-11
### Added
  - Caching! Everything is now cached, but it can all be manually disabled if necessary.
### Changed
  - The placeholder parsing process a little to hopefully fix some bugs
 
## [0.0.17] - 2020-08-11
### Fixed
  - Shading the runtime no longer bundles Gson, Guava, and Jetbrains Annotations
  
## [0.0.18] - 2020-08-11
### Fixed
   - Gradle Plugin: If a dependencies.json isn't present, it will be copied from a cached version
### Added
   - Runtime: If there are no configured dependencies, a warning will be logged 
