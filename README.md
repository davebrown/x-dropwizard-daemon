# Dropwizard Daemon

## An integration of [Akuma](http://akuma.kohsuke.org) for [Dropwizard](http://www.dropwizard.io).

### Dependency Info
```
<dependency>
    <groupId>com.robertcboll.dropwizard</groupId>
    <artifactId>dropwizard-daemon</artifactId>
    <version>0.2</version>
</dependency>
```
Currently only available through a private maven repository:
```
<repository>
    <id>robertcboll-nexus-public</id>
    <name>robertcboll public</name>
    <url>http://nexus.robertcboll.com/nexus/content/groups/public/</url>
    <releases><enabled>true</enabled></releases>
    <snapshots><enabled>false</enabled></snapshots>
</repository>
```

### Basic Usage

Make your application inherit from `DaemonApplication`.
```java

public class ExampleDaemon extends DaemonApplication<Configuration>

```

In your application's `main` method, call `daemonize()` before `run(args)`.
```java

new ExampleDaemon().daemonize().run(args);

```

You're all set! Your application will launch as a daemon process into the background.

For a working example, fork the [example project](http://www.github.com/robertcboll/dropwizard-daemon-example).
