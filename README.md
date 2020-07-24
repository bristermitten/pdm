# PDM
*The Plugin Dependency Manager*

PDM aims to reduce the amount of shading that plugin authors have to do
by creating a central repository for storing libraries.

This is done in the form of a new directory - `plugins/PluginLibraries`

Jars are downloaded to this directory and loaded into the classpath of plugins that need them.

## Why is this useful?

* Reduce overall Jar Size 
* No need to relocate dependencies
* Reduce build time when dealing with many libraries
* PDM is small! It uses absolutely no external dependencies that 
aren't provided by Spigot, making the size footprint of using it tiny - less than 20KB!

## How to Use 

PDM is incredibly simple. Usage involves 2 processes: 

1. Declaring Dependencies
2. Loading Dependencies

### Declaring Dependencies

There are 2 ways of doing Dependency Declaration:
1. Programmatically
2. JSON File

Declaration can be done programmatically or via JSON file: 

**Programmatically (java)**:

Create a new `PluginDependencyManager`, and call `PluginDependencyManager#loadAllDependencies`

For example: 
```java
PluginDependencyManager dependencyManager = new PluginDependencyManager(this);
dependencyManager.loadAllDependencies().thenRun(() -> getLogger().info("All Loaded!"));
```

**Programmatically (kotlin)**:

Create a new `PluginDependencyManager`, and call `PluginDependencyManager#loadAllDependencies`

For example:
```kotlin
val dependencyManager = PluginDependencyManager(this)
dependencyManager.loadAllDependencies().join()
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

This file's contents will be loaded automatically when you create a new `PluginDependencyManager`


**Loading**

Loading the dependencies is as simple as calling `PluginDependencyManager#loadAllDependencies`

This will return a `CompletableFuture<Void>` which is completed when all libraries are loaded.


**That's it!** All dependencies should be downloaded and loaded into the classpath,
including transitive dependencies. Any that failed will be logged gracefully.

## Repositories
PDM has 2 default repositories:
* Maven Central
* Spigot - this provides the spigot classes based on the current server version.

Custom repositories can be added with `RepositoryManager#addRepository`. 
An instance of `RepositoryManager` is exposed through 
`PluginDependencyManager#getManager#getRepositoryManager`.


## Gradle Plugin

PDM also includes a Gradle Plugin to automatically generate a `dependencies.json` file!
This is the recommended approach, as it does 99% of the work for you and is much more extendable.

This is a basic example of the usage that will be improved on in future:

```gradle
plugins {
  id "me.bristermitten.pdm" version "0.0.1"
}

dependencies {
    compileOnly 'org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT'
    pdm 'org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72' //This will be added to the dependencies.json
}

pdm {
    outputDirectory = 'Hello' //Change the output directory
}

jar.dependsOn project.tasks.getByName('pdm') //Always run the pdm task when we build 

```
