# JDA project structure

JDA (formerly known as `JDomainApp`) is a [multi-module Maven project](https://books.sonatype.com/mvnex-book/reference/multimodule.html). 

In Maven's terminology, a **module** is implemented as an **artifact project**.

JDA has a root (top-level) project whose structure is as follows:

```
jda:root	-> the root project
  common  -> module-common: (base) module used by other modules
  :dcsl    -> module-dcsl: implements DCSL language (used by most other modules)
  jda:main -> contains the core components of JDA
  jda:modules:root -> consists of independent modules that extend the core
  	:mbsl    -> implements MBSL language
  	:mccl    -> implements MCCL language
  	:sccl    -> implements SCCL language
    :restful  -> implements restful service
    ...
  jda.examples:root -> consists of application examples for JDA
  	jda.examples:courseman  -> coursemain examples
  	  jda.examples.courseman:basics
      :basics2
      :extsoftware
      :soict2019
    - jda.examples:drawing
    - :kengine
    - :processman
    - :vendingmachine
    - ...
```

# How to...

**REMEMBER:** Always perform commands on a module project **from the JDA root** directory:

## Install a project

From the root folder of the project, type this command:

`mvn clean install -DskipTests=true`

## Work with an artifact project in Eclipse

1. Use the "Import existing Maven project" to import the artifact project you want to work with
   - browse to the artifact folder and select it's pom on the dialog
2. Configure the source code and test directories following the Maven standard:
   1. If the project has already been imported before and its Eclipse's `.classpath` has been commited then you just need to verify that the source code directories have their respective output directories set correctly (see below)
   2. Add these two source directories to Eclipse:
      - main source code: `src/main/java`
      - test source code: `src/test/java`
      - (optional) example source code: `src/example/java`
   3. Ensure the following output directory arrangement:
      - `src/main/java` -> `target/classes` (default)
      - `src/test/java` -> `target/test-classes`
      - `src/example/java` -> `target/example-classes`
3. Eclipse will automatically look for dependencies in the local repo, and if not found then download them from Maven Central

## Compile a module project

1. cd into the JDA's root directory
2. type this command, where replace `module-X` by the actual module artifact name:
   ```
   mvn clean compile -pl :module-X -am
   ```
   - Option `-am` is needed to compile the projects that this module depends on 
  
## Package a module project

1. cd into the JDA's root directory
2. type this command, where replace `module-X` by the actual module artifact name:
   ```
   mvn package -pl :module-X -am -DskipTests=true
   ```
   - Option `-am` is needed to compile the projects that this module depends on 
   - Option `-DskipTests=true` is used to skip executing the JUnit tests

## Deploy a module project
This requires creating a single **assembly** containing all the project resources (not just the compile classes)

1. Check that `maven-assembly-plugin` is added to the JDA root's `pom.xml` and configured
2. cd into the JDA's root directory
3. Type this command, replace `module-X` by the actual module artifact name:
   ```
  mvn compile assembly:single -pl :module-X -am
   ```
  - the `compile` target is to ensure that source codes are compiled before being packaged

## Deploy a module project together with test classes
This creates an assembly that contains everything from the previous section plus all the test classes.

1. Check that `maven-assembly-plugin` is added to the JDA root's `pom.xml` and configured
2. Copy the template `assemply-test.xml` file to the module's directory
3. Edit `pom.xml` to add 2 goals as shown in the template `pom.xml`:
   1. one uses `maven-jar-plugin` with the `test-jar` goal
   2. the other uses `maven-assembly-plugin` with the `single` goal
4. cd into the JDA's root directory
5. Type this command, replace `module-X` by the actual module artifact name:
   ```
  mvn clean compile test-compile assembly:single -pl :module-X -am
   ```
  - the `test-compile` target is to ensure that test sources are compiled

## Run a program

`bin/mvn-java.bash <artifact> <FQN-program-class>`

## Run a test program 

- run normally (terminates after completed): 
  
  `bin/mvn-test.bash <artifact> <FQN-JUnit-test-class>`

- run without termination (as a daemon, useful for GUI-typed test programs):
(this does not work correctly yet!)

  `bin/mvn-test-daemon.bash <artifact> <FQN-program-class>`

