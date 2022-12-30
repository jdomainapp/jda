# Overview

JDA (previously known as `JDomainApp`) is a [multi-module Maven project](https://books.sonatype.com/mvnex-book/reference/multimodule.html) that implements Domain-Driven Design in Java. Key features of JDA include:

1. **Micro software architecture** (MOSA) which modularises the core domain model in micro-modules (one domain class per module). The architecture is also layered, MVC-enabled and service-ready (supports both RESTful and microservices).
2. MOSA consists in **3 main layers**: domain model (the core) layer, module layer and software layer. Each layer is described in a model.
3. **aDSL (annotation-based DSL)** is used to express the models directly in OOPLs
4. Language: Java
5. Deployed as a multi-module Maven project: each main feature is implemented in a separate module project, allowing them to be selectively imported into software projects

## Research and Development
JDA's development started in 2012 as a teaching tool in a software engineering subject at Hanoi University (Vietnam). It was first named jDomainApp, which means that it is a Java-based tool for domain-driven application development.

In 2014, the development of DomainAppTool, which is based on jDomainApp, began when a need was realised for a user-friendly tool that can be used to quickly and interactively
execute and test the domain model. 
We observe that the development of DomainAppTool and, more generally, that of the
jDomainApp framework continuously evolves. The tool is used not only to demonstrate the
framework’s capabilities but to quickly experiment with any new domain modelling ideas that
would eventually be incorporated into the framework. 

The 2016-2019 period played a crucial role in the framework's development. Three conference
papers were published with an aim to consolidate and formalise (to a certain extent)
the core theories that underlie the framework and tool. Two papers, in particular,
are the collaborative research works between the framework's author and Dr. Duc-Hanh Dang and Dr. Ha-Viet Nguyen from the Department of Software Engineering (VNU University of
Engineering and Technology). These works were later developed into two ISI journal papers.

Another major development of the framework started very recently in **2021** when the framework received a 2-year funding to develop the microservices capabilities. This project has just completed the first phase and is currently in the start of the second phase. Also starting with this project, jDomainApp is officially renamed to become **JDA**.

At this point, we feel that JDA is stable enough to be released  and thus decided to make the source code public. There are still issues to be resoved and untappeded potential to be explored. We hope that the framework would be received by the community, used in software development and be contributed to by developers.

# JDA project structure and documentation
Most of the feature documentations are written as ReadMe.md file in the framework modules that implement them. Thus, it is important to understand the JDA project structure and to browse it for detailed explanation of the features.

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

1. create a `deploy.xml` file for the module, place it in the folder `src/main/assembly`
2. update `pom.xml` to configure it to read `deploy.xml` as part of the `package` task
3. cd into the module's directory
4. Type this command:
   ```
   mvn package
   ```
   (add the option `-DskipTests=true` if needed)

## Deploy a module project together with test classes
TODO: update this section to extend the deployment task in the previous section.

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

Given:
- module whose artifact-id is `jda-X`
- JUnit test class (in `src/test/java`): `TestClass`
- test method of the test class: `testMethod` (omitted if run all test methods)

To run the JUnit test above:
```
mvn test -pl :jda-X -Dtest=TestClass#testMethod
```

### Short-cut commands
- run normally (terminates after completed): 
  
  `bin/mvn-test.bash <artifact> <FQN-JUnit-test-class>`

- run without termination (as a daemon, useful for GUI-typed test programs):
(this does not work correctly yet!)

  `bin/mvn-test-daemon.bash <artifact> <FQN-program-class>`

