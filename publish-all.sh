#!/bin/sh
echo Running Tests...
gradle test
echo Done!

if $? != 0; then
  exit $?
fi

echo Publishing Common Lib...
gradle common-lib:publish
echo Done!

if $? != 0; then
  exit $?
fi

echo Publishing PDM Runtime...
gradle pdm:publish
echo Done!
if $? != 0; then
  exit $?
fi

echo Publishing Gradle Plugin...
gradle pdm-gradle:publishPlugins
echo Done!
