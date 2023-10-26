# CourseMan Examples

This is the parent of all CourseMan example projects. Each example is created as a module subproject.

The `modules` element of the `pom.xml` file (shown below) lists all the examples:

```
<modules>
    <module>basics</module>
    <module>swgen</module>
    <module>mosar</module>
    <module>msa</module>
    <module>mdsa</module>			
    <module>kse2016</module>
    <module>kse2017</module>
    <module>rivf2016</module>
    <module>soict2019</module>
</modules>
```

The module ordering listed above is also our **recommended sequence of modules** that you should use when learning JDA. You should **comment out the examples that are not (yet) relevant**, as this will help significantly save build time. This is because some examples (especially the microservices ones) use quite a lot of dependencies.

Note also that you need to comment the corresponding modules in the `jda/modules/pom.xml` file. 
For example, if you commented out the example module `mosar` from the above list then you need to comment out the corresponding module `mosar` in `jda/modules/pom.xml`.

## Running an example project

## Using the command line

1. Package the example as a complete jar file (including all the dependencies).

To do this, issue an `mvn ... assembly:single ...` command from the `jda` root folder. This command has an argument to specify the example project id.

For instance, the following command package the example project whose artifact-id is `jda-eg-coursemanbasics`. You can find this id in the `pom.xml` file: 
```
# mvn clean compile test-compile assembly:single -pl :jda-eg-coursemanbasics -am
```

This command will create a distribution jar in the `target` directory of the example project. For the above example, it creates the following jar file: `target/jda-eg-coursemanbasics-5.4-SNAPSHOT-jar-with-dependencies.jar`.

2. Run the main class from the jar file

With the distribution jar file created, you can use the `java -cp...` command to run the main class. For example, the following command executes the main class `org.jda.example.courseman.software.MainUI` of the `jda-eg-coursemanbasics` project:

```
java -cp examples/courseman/basics/target/jda-eg-coursemanbasics-5.4-SNAPSHOT-jar-with-dependencies.jar org.jda.example.courseman.software.MainUI

```
## Using an IDE
- The preferred IDE is Intellij, which has very good support for such multi-projects as JDA
- Locate the main class and run it in your preferred IDE

## Example: basics (`jda-eg-coursemanbasics`)
This example project demonstrates the core functionality of JDA. In particular, it demonstrates the solution for the basic problem of software generation. The enhanced software generation features of JDA are developed based on this basic idea.

```
<modules>
    <module>basics</module>
</modules>
```

Refer to the ReadMe file in the example project for details.

## Example: swgen (`jda-eg-coursemansw`)
This example project demonstrates the software generation (`SwGen`) capabilities of JDA. It includes the two other core aDSLs, namely, MCCL and SCCL 

```
<modules>
    <module>basics</module>
    <module>swgen</module>    
</modules>
```

Refer to the ReadMe file in the example project for details.

## Example: mosar (`jda-eg-coursemanmosar`)
This example project extends `jda-eg-coursemansw` to demonstrate support for RESTful software.

```
<modules>
    <module>basics</module>
    <module>swgen</module>
    <module>mosar</module>
</modules>
```

Refer to the ReadMe file in the example project for details.

## Example: msa (`jda-eg-coursemanmsa`)
This example project extends `jda-eg-coursemanmosar` to demonstrate support for microservices.

```
<modules>
    <module>basics</module>
    <module>swgen</module>
    <module>mosar</module>
    <module>msa</module>
</modules>
```

Refer to the ReadMe file in the example project for details.

## Example: mdsa (`jda-eg-coursemanmdsa`)
This example project extends `jda-eg-coursemanmsa` to formally support micro-domain service architecture and its software development method.

```
<modules>
    <module>basics</module>
    <module>swgen</module>
    <module>mosar</module>
    <module>msa</module>
    <module>mdsa</module>			
</modules>
```

Refer to the ReadMe file in the example project for details.
