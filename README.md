AppModel4Flow
=============

AppModel4Flow is a library designed to make it easier to build complex user interfaces using the Vaadin Platform.
The primary idea is to separate the "model" from the "presentation" and use bindings to bind these two together. The
idea is not to hide the UI implementation from the model - AppModel4Flow is very much aware of the Vaadin Platform
and it would not be usable with any other UI framework.

## Prerequisites

AppModel4Flow is intentionally using pushing edge technology as I wrote it with some of my hobby projects in mind and
my hobby projects are almost always about learning new stuff. Therefore, you will need:

* Java 10
* Vaadin Platform 11

I have no plans to backport this library to any older versions of Java or Vaadin.

## Building

Eventually I will upload this library to Maven Central but for now, you have to clone and build it yourself:

* Clone this repository to your local computer.
* Make sure you have JDK 10 installed and that Maven is using it (`JAVA_HOME` should point to the JDK 10 directory).
* Invoke `mvn clean install`
* Import the library into your project using these Maven coordinates: `net.pkhapps.appmodel4flow:appmodel4flow:1.0-SNAPSHOT`

## Documentation

The documentation consists of a [README](appmodel4flow/README.md)-file and JavaDocs.

## Demo Application

You can find a simple demo application [here](appmodel4flow-demo).

## Incubator

There is an [incubator](appmodel4flow-incubator)-project that I use to try out new stuff. **Never use this in real 
projects as it is extremely unstable.** I also will not upload this to Maven Central.

## License

This software is licensed under [Apache 2.0](LICENSE-2.0.txt).
