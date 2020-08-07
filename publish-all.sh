#!/bin/sh
echo Publishing Common Lib...
./gradlew common-lib:publish
echo Done!

if $? != 0; then
  exit $?
fi

echo Publishing PDM Runtime...
./gradlew pdm:publish
echo Done!
if $? != 0; then
  exit $?
fi

echo Publishing Gradle Plugin...
./gradlew pdm-gradle:publishPlugins
echo Done!
