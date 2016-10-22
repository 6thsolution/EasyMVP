# EasyMVP 
[![Build Status](https://travis-ci.org/6thsolution/EasyMVP.svg?branch=master)](https://travis-ci.org/6thsolution/EasyMVP)  [ ![Download](https://api.bintray.com/packages/6thsolution/easymvp/easymvp-plugin/images/download.svg) ](https://bintray.com/6thsolution/easymvp/easymvp-plugin/_latestVersion)

A full-featured framework that allows building android applications following the principles of Clean Architecture.

- [Features](#features)
- [Installation](#installation)
- [License](#license)

## Features
* Easy integration
* Less boilerplate
* Composition over inheritance
* Implement MVP with just few annotations
* Use [Loaders](https://developer.android.com/guide/components/loaders.html) to preserve presenters across configurations changes
* Support [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) approach.

## Installation
Configure your project-level `build.gradle` to include the 'easymvp' plugin:
```groovy
buildscript {
  repositories {
    jcenter()
   }
  dependencies {
    classpath 'com.sixthsolution.easymvp:easymvp-plugin:1.0.0-beta3'
  }
}
```
Then, apply the 'easymvp' plugin in your module-level `build.gradle`:
```groovy
apply plugin: 'easymvp'

android {
  ...
}
```
For reactive api, simply apply the 'easymvp-rx' plugin in your module-level `build.gradle`  and then add the RxJava dependency:
```groovy
apply plugin: 'easymvp-rx'

dependencies {
  compile 'io.reactivex:rxjava:x.y.z'
}

```

## License

EasyMVP is under the Apache 2.0 license. See [LICENSE](LICENSE) file for details.