Example: `jda-eg-coursemanmsa`
===============================

A version of the `CourseMan` software that demonstrates the state-of-the-art MSA technologies.

# Prerequisites

## Dependencies
In addition to the standard dependencies, this example requires the following JDA libraries:

```
	modules/msacommon
```

## Infrastructure
Install the following platform software:
1. PostgreSQL: http://www.postgresql.org
2. Apache Kafka: https://kafka.apache.org/
3. Lombok plugin for your IDE: https://projectlombok.org (documentation: https://www.baeldung.com/lombok-ide)
- after installation: (1) restart the IDE, (2) Rebuild all projects

# Requirements

## Domain model
The domain model is based on that of the example project `jda-eg-coursemanmosar`.

The domain model is **fragmented** into one or more submodels based on the bounded contexts. 

The following sections describe some **use cases** of domain model fragmentation 
that are supported by the software. Some use cases include **extensions** of the domain model 
to demonstrate the increasing complexity of the domain requirements.

### Use case 1: No extensions

#### Domain modelling requirements

The domain modelling requirements of this use case are given below: 
 
|        Submodels        |                    Modules                     | 
|:-----------------------:|:----------------------------------------------:|
| Address Administration  |                    Address                     |
|  Student Registration   |                    Student                     |
|   Class Registration    |                  StudentClass                  |
| Academic Administration |                   Enrolment                    |
|  Course Administration  | CourseModule, CompulsoryModule, ElectiveModule |

#### Technical requirements

- Github branch: `usecase1`

### Use case 2: Academic and payment extensions

#### Domain modelling requirements

The domain modelling requirements of this use case are given below:  

|        Submodels         |                    Modules                     |                                           Extensions                                           |
|:------------------------:|:----------------------------------------------:|:----------------------------------------------------------------------------------------------:| 
|  Address Administration  |                    Address                     |                                                                                                |
|   Student Registration   |                    Student                     |                                                                                                |
|    Class Registration    |                  StudentClass                  |                                                                                                |
| Enrolment Administration |                   Enrolment                    |                          only with student and course module details                           |
| Academic Administration  |                   Enrolment                    |                                  with mark and grade details                                   |
|  Course Administration   | CourseModule, CompulsoryModule, ElectiveModule |                                  CourseModule has a unit fee                                   |
|  Finance Administration  |                 CoursePayment                  | Payment for course module enrolment, computed from the unit fees of the enrolled CourseModules |

#### Technical requirements
- Github branch: `usecase2`

## Architectural requirements

The core requirement is that this software must be **architectured based on MSA**. It consists of as many **microservices** as necessary to address the submodels.
Each service autonomously operates on a submodel of the domain model.

1. There must be at least one microservice that manages one submodel
2. Each service is based on and extends the functionality of a `CourseMan` **software module** in the `jda-eg-coursemanmosar` software. In this version, only the backend functionality needs to be considered.
3. Each service is realised by a **SpringBoot** application
4. Services communicate via **Kafka**'s event streaming (see `jda-eg-coursemankafka` for how to achieve this)
5. Standard **logging** must be used to log each service's action
6. Additional layers/APIs can and should be added on top of Kafka to help more effectively manage the data exchange between services

# Run the example

## Software versions
There are two CourseMan-MSA versions that differ in that one version implements the TMSA architecture and the other does not. The two versions are located under the `modules` sub-folder as follows:

|            Version 1.0 (**without** TMSA)            |                 Version 2.0 (**with** TMSA)                  |
|:------------------------------------------------:|:--------------------------------------------------------:|
| [`modules/services`](modules/services/ReadMe.md) | [`modules/servicestmsa`](modules/servicestmsa/ReadMe.md) | 

The general procedure for running the CourseMan-MSA software consists of 4 main steps. We outline the steps below. Refer to the `ReadMe.md` file in each software version folder for the details of each step. 

## 1. Run Apache Kafka and register the topics

### Start Apache Kafka

```
# Start the ZooKeeper service
bin/zookeeper-server-start.sh config/zookeeper.properties
```

```
# Start the Kafka broker service
bin/kafka-server-start.sh config/server.properties
```

### Register the message topics

## 2. Build the service projects
1. Manually install library folder `com.jayway.json-path` (located in the folder `$JDA/local-maven-repo`) using the following command:
Assume that the `$JDA` folder is `/jda` then:
```
mvn install:install-file -Dfile=/jda/local-maven-repo/jayway/jsonpath/json-path/2.4.0/json-path-2.4.0.jar -DpomFile=/jda/local-maven-repo/jayway/jsonpath/json-path/2.4.0/json-path-2.4.0.pom
```
2. Build the source code:
```
cd ../courseman/msa/
mvn clean install -DskipTests
```

## 3. Run the infrastructure services 

### Config service
- Description: The Spring Cloud Configuration Server (aka Config Server) allows you to set
up application properties with environment-specific values. The other services will retrieve its properties from Config Server when the services lanch.
- Run by commandline
```
cd ../courseman/msa/modules/configserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.configserver.ConfigurationServerApplication`

### Discovery Service
- Description:  Discovery service like **Eureka** will abstract away the physical location of our services. Eureka can seamlessly add and remove service
instances from an environment without impacting the service clients. We use Eureka to look up a service.
- Run by commandline
```
cd ../courseman/msa/modules/eurekaserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.eurekaserver.EurekaServerApplication`

### Gateway Service
- Description: A service gateway acts as an intermediary between the service client and an invoked service, pulls apart the path coming in from the service client call and determines what service the service client is trying to invoke. It is a central point to apply rules, policies for requests and responses from/to servicies. The Spring Cloud Gateway allows us to implement custom business logic through filters (pre- and post-filters), it integrates with Netflix’s Eureka Server and can automatically map services registered with Eureka to a route. 

- Run by commandline
```
cd ../courseman/msa/modules/gatewayserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.gatewayserver.ApiGatewayServerApplication`

## 4. Run the domain (business) services
### Database setup
- Create a PostgreSQL database named `domainds`, owned by the account: (user, password) = `(admin, password)`
- Create the database schemas for the services.
- Run each service by using the command: `mvn spring-boot:run` or directly using the service application class. 

