#!/bin/bash
# Generate weaver processor
sh ./gradlew clean easymvp-weaver:jar
# Manipulate java classes
sh ./gradlew transformClassesWithWeaverForDebug --info
# Run test
sh ./gradlew connectedAndroidTest -PdisablePreDex -PwithDexcount -Dscan --info