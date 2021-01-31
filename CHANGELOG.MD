# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
this project does not yet adhere to semantic versioning as it's in very early stages. 

### Terminology
   - PDM: The `common-lib` module that is used everywhere in the project
   - Runtime: The `pdm` module
   - Gradle Plugin: The `pdm-gradle` module
   - Example: The `example` module
   - Global: Changes that apply everywhere
   - Readme: `README.MD`

## [0.0.31] - 2021-01-31
### Added
   - PDM: Maven Repositories will now cache an artifact's transitive dependencies to reduce repeated lookups in some situations
   - Example: Some basic documentation has been added to the example module
   - PDM: Artifact now defines an equals and hashCode implementation, so can be safely used in any hash based data structure
### Changed
   - Example: PDMExample now targets the latest PDM api
   - Readme: Code examples now also target the latest API
   - PDM: Some messy code in SpigotRepository.java has been cleaned up
   - PDM: Minor spacing and annotation tweaks to Repository classes
### Fixed
   - Gradle Plugin: No longer throws a StackOverflowError when dealing with recursive transitive dependencies (eg adventure-api / adventure-bom)
### Credits
   - Yakovliam: Minor tweaks and bug reports

## [0.0.30] - 2021-01-01
### Changed:
   - PDM: Minor code cleanup
### Fixed:
   - Runtime: PDM should no longer give a reflection warning when being used on Java 9+ (See #30)

## [0.0.29] - 2020-10-25
### Added:
   - Gradle Plugin: A new PDM maven repository provided by [Glare](https://github.com/darbyjack)
   - Gradle Plugin: Added a new task to clean the PDM cache
### Changed:
   - PDM: Lots of cleanup
   - Runtime: API now uses `PluginDependencyManager` instead of `PDMBuilder`

## [0.0.28] - 2020-09-09
### Added:
   - Gradle Plugin: Can now properly handle exclusions for transitive dependencies

## [0.0.27] - 2020-09-01
### Added:
   - Gradle Plugin: Can now properly handle pdm dependencies in submodules

## [0.0.26] - 2020-08-30
### Changed
   - PDM: The dependency loading process has hopefully been sped up if all dependencies are already downloaded.

## [0.0.25] - 2020-08-27
### Fixed
   - Global: The Kotlin stdlib should no longer be shaded into every plugin using pdm

## [0.0.24] - 2020-08-25
### Fixed
   - Gradle Plugin: No longer throws an exception relating to Kotlin property references (seems to be caused by the update to 1.4)

## [0.0.23] - 2020-08-25
### Fixed
   - Gradle Plugin: No longer throws `ClassNotFoundException: com.google.common.cache.CacheBuilder` when being used

## [0.0.22] - 2020-08-22
### Added
   - PDM: A method to download, but not load all dependencies
### Fixed
   - Common Lib: MavenRepository.java now correctly implements toString()
### Changed
   - Kotlin 1.4 is now used wherever applicable
   - Some docs have been slightly reworded 
   - Small refactor of some internals
  
## [0.0.21] - 2020-08-22
### Fixed
   - PDM: Transitive dependencies now load into the classpath if their "parent" dependency already exists

## [0.0.20] - 2020-08-17
### Fixed
   - Gradle Plugin: No longer tries to copy from the cached dependencies.json to the latest one if it already exists
   - The Kotlin Example now includes a workaround for the Intrinsics class problem

## [0.0.19] - 2020-08-15
### Fixed
   - Gradle Plugin: Now exposes common-lib with the api configuration 

## [0.0.18] - 2020-08-11
### Fixed
   - Gradle Plugin: If a dependencies.json isn't present, it will be copied from a cached version
### Added
   - Runtime: If there are no configured dependencies, a warning will be logged 

## [0.0.17] - 2020-08-11
### Fixed
  - Shading the runtime no longer bundles Gson, Guava, and Jetbrains Annotations
 
## [0.0.16] - 2020-08-11
### Added
  - Caching! Everything is now cached, but it can all be manually disabled if necessary.
### Changed
  - The placeholder parsing process a little to hopefully fix some bugs

 
## [0.0.13-0.0.15] - 2020-08-11
 - *Skipped because of Gradle again :/*


## [0.0.12] - 2020-08-11
### Added
 - Some shields to README.MD about the latest versions
 - A JSON schema for `dependencies.json`
 - Support for parent pom parsing
 - Another Unit Test
### Changed
 - Reworded a lot of README.MD
 - The pom parsing process has been reworked to accommodate the parent pom support

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


## [0.0.10] - 2020-08-7
### Fixed
  - Fixed tests failing because they couldn't find Gson classes
  - SnapshotArtifact will no longer attempt to parse a POM if the URL is invalid
### Changed
  - Remove a few unused imports from `PluginDependencyManager`
  - `publish-all.sh` now runs tests before publishing
 
 ## [0.0.9] - 2020-08-7
 ### Added
   - Add a method to `PDMBuilder` that can use the Plugin's ClassLoader to do dependency loading pre-plugin initialization
   - Add a slightly more detailed error message to SnapshotArtifact and potentially fix a bug in PDM.kt
   
## [0.0.8] - 2020-08-7
### Changed
  - No changes, just needed to update as Gradle Publishing had corrupted previous versions

*Versions between 0.0.2 and 0.0.8 are not on Gradle Plugins Portal due to a technical fault*
## [0.0.4] - 2020-08-5
### Changed
  - Cleaned up the buildscripts
  
## [0.0.3] - 2020-08-5
### Added
 - More Unit Tests
### Changed
 - Improved the version extraction process to support Jitpack dependencies now
 
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
 
## [0.0.1] - 2020-06-29
Basic functionality, buggy


