# KoCL
Simple OpenCL Wrapper for Java/Kotlin with minimum overhead


## Why?
While learning and exploring OpenCL (for Java development) i found myself in the uncomfortable situation:

**There are simply no examples or tutorials from which i can learn more about this wonderful API**

And this is primarily the main issue why a lot of people getting overwhelmed by the complexity of API, and burn out from this.

Which is why i decided to create this lib, so you don't have to worry about bunch of tricky moments and nuances of OpenCL, while maintaining similarity of OpenCL coding approach.

## Structure
- [Lib itself](https://github.com/offeex/KoCL/tree/main/src/main/kotlin)
- [Java example code](https://github.com/offeex/KoCL/blob/main/src/test/java/HelloJavaCL.java)
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
    implementation 'com.github.offeex:KoCL:Tag'
}
```

## Warning
OpenCL 1.1/1.2's events are not supported. I will bring this feature in future.
