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

## How to Use 

PDM is incredibly simple. Usage involves 2 processes: 

1. Declaring Dependencies
2. Loading Dependencies

Declaration can be done programmatically or via JSON file: 

**Programmatically**:

Create a new `PluginDependencyManager`, and call `PluginDependencyManager#addRequiredDependency`

For example: 
```java
PluginDependencyManager dependencyManager = new PluginDependencyManager(this);
dependencyManager.addRequiredDependency(
        new Dependency(
            "org.jetbrains.kotlin", //groupId
            "kotlin-stdlib.jdk8", //artifactId
            "1.3.72" //version
        )
);
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


## Todo

- [ ] Create a Gradle plugin that will automatically generate a `dependencies.json` file
