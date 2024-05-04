MDSA - Micro-domain software application/architecture
=========
# Version History:
- 1.0: first version

# Overview
Extends `module-mosar` for micro-services architecture.

# Example: Courseman 
## Set up
- run infra services:
  - config server
  - zookeeper > kaffa
  - eureka server
  - gateway server

## Generate a service

- modify /sofware/config/SCCCourseMan"
    - outputPath: path to save generated files (eg. "E:\\jda-f90\\jda\\modules\\mdsa\\src\\example")
    - modelsPath: path to domain models (eg. "E:\\jda-f90\\jda\\modules\\mdsa\\src\\example\\src\\main\\java\\org\\coursemanmdsa\\models")

- modify /sofware/config/SCCCourseMan", mccServices - add service configuration
    eg. mccServices = {
        ServiceAddress.class
    }

- run generator: CourseManMDSGen

## Execute a service
services are generated in specified outputPath/services folder
[ReadMe.md](..%2F..%2F..%2F..%2F..%2Fjda-swgen%2Fopasys%2FReadMe.md)
- copy generated config-file into configserver/resources
- restart configserver

  foreach service:
- build the service: run `mvn clean install -DskipTests`
- run the service: run the corresponding XServiceApplication.java file (eg. AddressServiceApplication)
  or by command: `mvn spring-boot:run`

# Resilience testing
(see [TMSA Evaluation report](https://docs.google.com/document/d/1MO4gI4OxDuJWgh3_92eQL13ukzZtH1zzVGyG7fwDy78/edit#heading=h.nj3guu1yjdg0))