AppModel4Flow
=============

AppModel4Flow is a library designed to make it easier to build complex user interfaces using server-side Java and the
Vaadin platform. With complex user interfaces, I mean user interfaces where you have to be able to present and interact 
with the same data through different views and in different ways, or where the the user interface changes depending
on what options the user selects, or where the user interface is backed by a complex model and not just a simple POJO.

The primary idea is to separate the "model" from the "presentation" and use bindings to bind these two
together. The idea is not to hide the UI implementation from the model - AppModel4Flow is very much aware of Vaadin and 
it would not be usable with any other UI framework.

The inspiration behind this library comes from many places; JavaFX, the old Vaadin 6/7 `ObjectProperty` and also some
personal experiences from various customer projects.

I have not actually tried this library out in a real-world application. It is based on ideas that I think should work 
and should be useful, but that remains to be seen. Any feedback is very much appreciated.

## Prerequisites

AppModel4Flow is intentionally using the latest versions of everything since I wrote it with some of my hobby projects
in mind and my hobby projects are almost always about teaching me new stuff. Therefore, you will need:

* Java 10
* Vaadin 11
* A maven-version that works with Java 10 (I use 3.5.3 at the time of writing)

I have no plans to backport this library to any older versions of Java or Vaadin.

## Maven Coordinates

Snapshots are uploaded to [Sonatype](https://oss.sonatype.org/content/repositories/snapshots) every now and then.
Releases are uploaded to Maven Central. My projects tend to get stuck in version 0.x since I can never decide when
something is good enough to be released. This time I'm going to boldly go where I have not gone before and just release
early and often. Anyway, here are the Maven coordinates (reviewed on October 23, 2018):

```xml
<dependency>
    <groupId>net.pkhapps.appmodel4flow</groupId>
    <artifactId>appmodel4flow</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Building

* Clone this repository to your local computer.
* Make sure you have JDK 10 installed and that Maven is using it (`JAVA_HOME` should point to the JDK 10 directory).
* Invoke `mvn clean install`.

## Demo Application

You can find a simple demo application [here](appmodel4flow-demo). It's a good starting point for getting an idea of
what this library is all about.

## Documentation

The documentation consists of a [README](appmodel4flow/README.md)-file and JavaDocs.

## Incubator

There is an [incubator](appmodel4flow-incubator)-project that I use to try out new stuff. **Never use this in real 
projects as it is extremely unstable.** I also will not upload this to Maven Central.

## License

This software is licensed under [Apache 2.0](LICENSE-2.0.txt).
