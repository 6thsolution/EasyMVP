#!/bin/bash

set -e

# Generate weaver processor
./gradlew clean easymvp-weaver:jar
# Manipulate java classes
./gradlew transformClassesWithWeaverForDebug --info
# Run tests
./gradlew connectedAndroidTest -PdisablePreDex -PwithDexcount -Dscan --info