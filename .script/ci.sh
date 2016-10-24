#!/bin/bash
cd ..
./gradlew clean easymvp-weaver:jar
./gradlew transformClassesWithWeaverForDebug --info
./gradlew connectedAndroidTest -PdisablePreDex -PwithDexcount -Dscan --info