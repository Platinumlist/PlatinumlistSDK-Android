# PlatinumlistSDK-Android
[![](https://jitpack.io/v/Platinumlist/PlatinumlistSDK-Android.svg)](https://jitpack.io/#Platinumlist/PlatinumlistSDK-Android)

## Setup

1. Add it to your build.gradle with:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.Platinumlist:PlatinumlistSDK-Android:{latest version}'
}
```

2. Add widget to your layout:

```xml
<com.platinumlist.sdk.PlatinumView
   android:id="@+id/platinumView"
   android:layout_width="match_parent"
   android:layout_height="match_parent" />
```

3. implement the PlatinumView.Adapter

4. set adapter to view:

```kotlin
platinumView.adapter = adapter
```

5. call invalidate method

```kotlin
adapter.invalidate()
```

6. call widget clear() method for release memory

```kotlin
platinumView.clear()
```
