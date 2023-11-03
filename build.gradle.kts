
plugins {
  java
  id("org.gradlex.java-ecosystem-capabilities") version "1.3.1"
}

repositories {
    mavenCentral()
}

dependencies {
  implementation("org.spockframework:spock-core:2.3-groovy-4.0")
  implementation("org.spockframework:spock-junit4:2.3-groovy-4.0")
  implementation("org.apache.groovy:groovy:4.0.6")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val groovyVersion = "4.0.6"
val hamcrestVersion = "2.2"

// Deactivate default resolution strategy for all rules.
// The java-ecosystem-capabilities plugin adds a resolution strategy that resolves the conflict with hamcrest, 
// but disabling the default behavior and adding our own resolution strategy helps to more explicitly 
// demonstrate the problem.
javaEcosystemCapabilities {
    deactivatedResolutionStrategies.addAll(allCapabilities)
}

allprojects {
  configurations.all {

    resolutionStrategy.capabilitiesResolution {
      withCapability("org.hamcrest:hamcrest-core") {
        val toBeSelected = candidates.firstOrNull { it.id.let { id -> id is ModuleComponentIdentifier && id.module == "hamcrest"}}
        if (toBeSelected != null) {
          select(toBeSelected)
          because("prefer the full hamcrest library")
        }
      }
      withCapability("org.hamcrest:hamcrest") {
        val toBeSelected = candidates.firstOrNull { it.id.let { id -> id is ModuleComponentIdentifier && id.module == "hamcrest"}}
        if (toBeSelected != null) {
          select(toBeSelected)
          because("prefer the full hamcrest library")
        }
      }
    }

    resolutionStrategy.failOnVersionConflict()

    resolutionStrategy.eachDependency {
      if (requested.group == "org.codehaus.groovy" && requested.name == "groovy-all") {
        useVersion(groovyVersion)
      } else if (requested.group == "org.codehaus.groovy" && requested.name != "groovy-bom") {
        useVersion(groovyVersion)
      } else if (requested.group == "org.apache.groovy" && requested.name == "groovy-all") {
        useVersion(groovyVersion)
      } else if (requested.group == "org.apache.groovy" && requested.name != "groovy-bom") {
        useVersion(groovyVersion)
      } 
      else if (requested.group == "org.hamcrest") {
        useVersion(hamcrestVersion)
      }
    }
  }
}

