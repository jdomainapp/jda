# Common commands

## Compile the root project and all sub-projects

- Compile source code: `mvn compile`

- Compile test source code: `mvn test-compile`

## Compile a sub-project

- subproject artifact: `jda.examples.courseman:basics`
- command: `mvn -pl jda.examples.courseman:basics compile`

## Install

- Install without running JUnit tests: 
`mvn install -DskipTests=true`

## Execute a program

- command to use: `bin/mvn-java`
- artifact/project: `jda.examples.courseman:basics`
- program class: `org.courseman.software.Main`
- command: `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.Main`


## Execute a JUnit test class

- command to use: `bin/mvn-test-single.bash`
- artifact/project: `jda:main`
- test class: `domainapp.test.modules.modulegen.ModuleStudentGenTest`
- command: `bin/mvn-test-single.bash jda:main domainapp.test.modules.modulegen.ModuleStudentGenTest`

