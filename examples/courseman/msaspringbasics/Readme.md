# Getting Started: Spring boot Example CourseMan Microservices

Adapted from the `jda-training/traing/msa/` example.

### 1. Create Discovery Server name: discoveryserver
Create project Springboot with Dependency:
#### Eureka Server 
##### application.properties
`spring.application.name=discoveryserver`
`eureka.client.register-with-eureka=false`
`eureka.client.fetch-registry=false`
`server.port=8761`
##### DiscorveryserverApplication.java
`@EnableEurekaServer`
 
### 2. Create Config Server name: configserver
Create project Springboot with Dependency:
#### Config Server 
##### application.properties
`spring.application.name=configserver`
`server.port=8088`
`eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
`spring.cloud.config.server.git.uri = https://github.com/levanvinhskv/spring-eg-coursemanmsa-repo.git`
`spring.cloud.config.server.git.username=xxxx`
`spring.cloud.config.server.git.password=xxxx`
`spring.cloud.config.server.git.default-label=main`
##### ConfigserverApplication.java
`@EnableDiscoveryClient `
`@EnableConfigServer`
### 3. Create services: Sclass, Course, Student, Enrolment
Create project Springboot with Dependency:
#### Spring web
#### Spring Eureka client 
##### application.properties
##### name SCLASS, STUDENT, COURSE, ENROLMENT
##### port: 9004,9005,9006,9007
`spring.application.name=ENROLMENT`
`server.port=9007`
`eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
`spring.datasource.url=jdbc:mysql://localhost:3306/coursemanmsa`
`spring.datasource.username=root`
`spring.datasource.password=aaa111`
`eureka.instance.leaseRenewalIntervalInSeconds = 10`
`eureka.instance.metadataMap.instanceId=${vcap.application.instance_id:${spring.application.name}:${spring.application.instance_id:${random.value}}}`

`eureka.instance.instanceId=${spring.application.name}:${spring.application.instance_id:${random.value}}`
##### ConfigserverApplication.java
`@EnableEurekaClient`
`@EnableDiscoveryClient`
#### Create: MVC
#### Create: Database for services

### RUN
1. Start: discoveryserver
2. Start: configserver
3. Start: Services (Sclass, Student, Course, Enrolment)
