#!/bin/sh
echo Publishing Common Lib...
./gradlew common-lib:publish
echo Done!

echo Publishing PDM Runtime...
./gradlew pdm:publish
echo Done!

echo Publishing Gradle Plugin...
./gradlew pdm-gradle:publishPlugins
echo Done!
