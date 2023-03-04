# KoCL
Very lightweight utils & tools for OpenCL written in Kotlin

## What does it do
- Handles OpenCL errors, that may arise in the process
- Provides little useful utilities to work with memory
- Provides useful shorthands for buffer creation

## Structure
- [Lib itself](https://github.com/offeex/KoCL/tree/main/src/main/kotlin)
- [Kotlin example code](https://github.com/offeex/KoCL/blob/main/src/test/kotlin/HelloKotlinCL.kt)

## Setup

**1.**  Add it in your root build.gradle at the end of repositories:
```gradle
repositories {
   maven { url 'https://jitpack.io' }
}
```

**2.**  Add a dependency
```gradle
dependencies {
    implementation 'com.github.offeex:KoCL:SNAPSHOT'
}
```
