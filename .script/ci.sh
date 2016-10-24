#!/bin/bash

set -e

# Generate weaver processor
./gradlew clean easymvp-weaver:jar
# Manipulate java classes
./gradlew transformClassesWithWeaverForDebug --info
# Run test
./gradlew connectedAndroidTest -PdisablePreDex -PwithDexcount -Dscan --info