# PDM
*The Plugin Dependency Manager*

[![Build Status](https://travis-ci.org/knightzmc/pdm.svg?branch=master)](https://travis-ci.org/knightzmc/pdm)

[![Latest Runtime Version](https://img.shields.io/maven-metadata/v?color=blue&label=PDM%20Runtime&metadataUrl=https%3A%2F%2Frepo.bristermitten.me%2Frepository%2Fmaven-public%2Fme%2Fbristermitten%2Fpdm%2Fmaven-metadata.xml)](https://repo.bristermitten.me/#browse/browse:maven-releases:me%2Fbristermitten%2Fpdm)

[![Gradle Plugin Version](https://img.shields.io/maven-metadata/v?color=blue&label=Gradle%20%20Plugin&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fme%2Fbristermitten%2Fpdm-gradle%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/me.bristermitten.pdm)

[![Discord](https://img.shields.io/discord/728307032440176762?color=purple&label=Chat%20%2F%20Support)](https://discord.gg/ZtwmaCV)

![PDM Demo](https://img.bristermitten.me/nMpAG5yZQ2.gif)


# **PDM IS STILL IN ALPHA!** - Nothing is guaranteed to work and every update could include breaking changes!

PDM aims to reduce the amount of shading that plugin authors have to do
by creating a central repository for storing libraries.

This is done in the form of a new directory - `plugins/PluginLibraries`

Jars are downloaded to this directory and loaded into the classpath of plugins that need them.

## Why is this useful?

* Reduce overall Jar Size 
* No need to relocate dependencies
* Reduce build time when dealing with many libraries
* PDM is small! It uses absolutely no external dependencies that 
aren't provided by Spigot, making the size footprint of using it tiny - less than 50 KB!

## How to Use 

PDM is incredibly simple. Usage involves 2 processes: 

1. Declaring Dependencies
2. Loading Dependencies

### Declaring Dependencies

There are 2 ways of doing Dependency Declaration:
1. Programmatically
2. JSON File

Declaration can be done programmatically or via JSON file: 

**Programmatically (Java)**:

Create a new `PluginDependencyManager` with `PDMBuilder`, and call `PluginDependencyManager#loadAllDependencies`

For example: 
```java
PluginDependencyManager dependencyManager = PluginDependencyManager.of(this);
CompletableFuture<Void> onLoad = dependencyManager.loadAllDependencies();
//loadAllDependencies is async, the returned future is completed when downloading and loading completes
onLoad.thenRun(() -> System.out.println("Everything is loaded!"));
```

**JSON Based**:
Make a file called `dependencies.json` in your resources directory.

It should follow a format similar to this example: 

```json
{
  "repositories": {
    "jitpack": "https://jitpack.io"
  },
  "dependencies": [
    {
      "groupId": "com.github.knightzmc",
      "artifactId": "fluency",
      "version": "1.0",
      "repository": "jitpack"
    }
  ]
}
```

This file's contents will be loaded automatically and downloaded.


**Loading**

Loading the dependencies is as simple as calling `PluginDependencyManager#loadAllDependencies`

This will return a `CompletableFuture<Void>` which is completed when all libraries are loaded.

**That's it!** All dependencies should be downloaded and loaded into the classpath,
including transitive dependencies. Any that failed will be logged and handled gracefully.


## Gradle Plugin

PDM also includes a Gradle Plugin to automatically generate a `dependencies.json` file!
This is the recommended approach, as it does 99% of the work for you and is much more extendable.

This is a basic example of the usage:

```gradle
plugins {
  id "me.bristermitten.pdm" version "0.0.28" //Replace with the latest version 
}

dependencies {
    compileOnly "org.spigotmc:spigot-api:1.16.3-R0.1-SNAPSHOT"
    pdm "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72" //This will be added to the dependencies.json
}

jar.dependsOn project.tasks.getByName("pdm") //Always run the pdm task when we build. Alternatively, just run [gradle pdm build]
```

A full example can be found [here](/example).
