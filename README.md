# Flightpath
Flying is better than using the (event) bus anyway.

## Jars
...can be grabbed from the [Maven server on Tethys](http://tethys.drakon.io/maven/). For Gradle users:

```gradle
repositories {
  maven {
    name 'Tethys (drakon.io)'
    url 'http://tethys.drakon.io/maven/'
  }
}

dependencies {
  compile 'io.drakon:flightpath:0.2.1'
  // or...
  compile group: 'io.drakon', name: 'flightpath', version: '0.2.1'
}
```

If you're shading/shadowing, please use the `all` classifier to get a repackable version with Javassist shaded in:

```
dependencies {
  compile 'io.drakon:flightpath:0.2.1:all'
  // or...
  compile group: 'io.drakon', name: 'flightpath', version: '0.2.1', classifier: 'all'
}
```