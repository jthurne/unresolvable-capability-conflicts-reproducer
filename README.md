Unresolvable Capability Conflict Reproducer Project
----------------------------------------------------

This project reproduces the following problem with the Gradle build tool:

If all of the following conditions are true...

  1. the build's dependency tree contains multiple dependencies that declare the same capability (capability conflict)
  2. the build contains a capability selection rule to resolve the conflict
  3. the build is configured to fail on version conflicts (by calling `resolutionStrategy.failOnVersionConflict()`)
  
...then the capability conflict fails to resolve and the build fails.

To see the build failure, run:

```
./gradlew build
```

This will result in output similar to:

```
Calculating task graph as no configuration cache is available for tasks: build

FAILURE: Build failed with an exception.

* What went wrong:
Configuration cache state could not be cached: field `classpath` of task `:compileJava` of type `org.gradle.api.tasks.compile.JavaCompile`: error writing value of type 'org.gradle.api.internal.artifacts.configurations.DefaultUnlockedConfiguration'
> Could not resolve all dependencies for configuration ':compileClasspath'.
   > Conflict found for the following module:
       - org.hamcrest:hamcrest On capability org.hamcrest:hamcrest-core prefer the full hamcrest library

* Try:
> Run with :dependencyInsight --configuration compileClasspath --dependency org.hamcrest:hamcrest to get more insight on how to solve the conflict.
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Get more help at https://help.gradle.org.

BUILD FAILED in 836ms
Configuration cache entry discarded due to serialization error.
```

This project enables the configuration cache and thus all dependencies are
resolved at configuration time. However, the use of the configuration cache is
most likely not related to the problem that is demonstrated here.

### Notes

The example build adds the java-ecosystem-capabilities plugin, which adds a
number of capabilities to common libraries that exist in the java ecosystem. It
then adds dependencies which pull in both the full hamcrest library and the
slimmer hamcrest-core library (which conflict with each other).

The java-ecosystem-capabilities plugin defines its own dependency resolution
rules that normally would resolve the hamcrest conflict automatically. However,
to make the issue easier to "see and understand", this project disables the
default java-ecosystem-capabilities plugin's dependency resolution rules and
explicitly adds capability selection rules for hamcrest.

