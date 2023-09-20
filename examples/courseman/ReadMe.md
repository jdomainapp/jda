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

## Example: basics (`jda-eg-coursemanbasics`)
This example project demonstrates the core functionality of JDA.

Refer to the ReadMe file in the example project for details.

## Example: swgen (`jda-eg-coursemansw`)
This example project demonstrates the software generation (`SwGen`) capabilities of JDA.

Refer to the ReadMe file in the example project for details.

## Example: mosar (`jda-eg-coursemanmosar`)
This example project extends `jda-eg-coursemansw` to demonstrate support for RESTful software.

Refer to the ReadMe file in the example project for details.

## Example: msa (`jda-eg-coursemanmsa`)
This example project extends `jda-eg-coursemanmosar` to demonstrate support for microservices.

Refer to the ReadMe file in the example project for details.

## Example: mdsa (`jda-eg-coursemanmdsa`)
This example project extends `jda-eg-coursemanmsa` to formally support micro-domain service architecture and its software development method.

Refer to the ReadMe file in the example project for details.
