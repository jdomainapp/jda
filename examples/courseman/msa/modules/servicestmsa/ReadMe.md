Example: `jda-eg-coursemanmsa-servicestmsa`
===============================

The main version (version 2.0) of the `CourseMan` software that demonstrates the state-of-the-art MSA technologies.

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
The domain model of the [Use case 2 of the requirements](../../ReadMe.md).

# Run the example
## Note
- If you compile the code using the command line explained in this guide AND
  - If you then use **InteliJ** to run the services THEN
    - ensure that the **IDE's JDK is the same as** the one used to compile the services!
    - otherwise, you may get an exception about @EnableEurekaClient" not being able to execute

## 1. Run Apache Kafka and register the message topics

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
```
# Create topic: "streams-courseman-coursemodules"
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic courseChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic studentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic addressChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic classChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic enrolmentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic teacherChangeTopic --bootstrap-server localhost:9092
```

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

- Output:
<details><summary>Click to view</summary>
<p>

```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)

11:20:15.893 [main] INFO  o.j.e.c.c.ConfigurationServerApplication - The following profiles are active: git
11:20:16.679 [main] INFO  o.s.cloud.context.scope.GenericScope - BeanFactory id=86fe7d1a-d775-34bf-85cc-27ff6b49371c
11:20:17.075 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8071 (http)
11:20:17.085 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8071"]
11:20:17.086 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
11:20:17.086 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
11:20:17.244 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
11:20:17.245 [main] INFO  o.s.web.context.ContextLoader - Root WebApplicationContext: initialization completed in 1333 ms
11:20:17.960 [main] INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'applicationTaskExecutor'
11:20:18.334 [main] INFO  o.s.b.a.e.web.EndpointLinksResolver - Exposing 15 endpoint(s) beneath base path '/actuator'
11:20:18.373 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8071"]
11:20:18.415 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8071 (http) with context path ''
11:20:18.536 [main] INFO  o.j.e.c.c.ConfigurationServerApplication - Started ConfigurationServerApplication in 3.726 seconds (JVM running for 4.293)
```
</p></details>

### Discovery Service
- Description:  Discovery service like **Eureka** will abstract away the physical location of our services. Eureka can seamlessly add and remove service
  instances from an environment without impacting the service clients. We use Eureka to look up a service.
- Run by commandline
```
cd ../courseman/msa/modules/eurekaserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.eurekaserver.EurekaServerApplication`

- Output:
<details><summary>Click to view</summary>
<p>

```
 .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)

05:00:48.213 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
05:00:51.499 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=eureka-server, profiles=[default], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
05:00:51.509 [main] INFO  o.j.e.c.e.EurekaServerApplication - No active profile set, falling back to default profiles: default
05:00:52.121 [main] WARN  o.s.boot.actuate.endpoint.EndpointId - Endpoint ID 'service-registry' contains invalid characters, please migrate to a valid format.
05:00:52.234 [main] INFO  o.s.cloud.context.scope.GenericScope - BeanFactory id=5fb159de-ff08-34fb-b371-21170586b796
05:00:52.552 [main] INFO  org.eclipse.jetty.util.log - Logging initialized @7486ms to org.eclipse.jetty.util.log.Slf4jLog
05:00:52.635 [main] INFO  o.s.b.w.e.j.JettyServletWebServerFactory - Server initialized with port: 8070
05:00:52.639 [main] INFO  org.eclipse.jetty.server.Server - jetty-9.4.27.v20200227; built: 2020-02-27T18:37:21.340Z; git: a304fd9f351f337e7c0e2a7c28878dd536149c6c; jvm 11.0.15+10-Ubuntu-0ubuntu0.20.04.1
05:00:52.676 [main] INFO  o.e.j.s.h.ContextHandler.application - Initializing Spring embedded WebApplicationContext
05:00:52.677 [main] INFO  o.s.web.context.ContextLoader - Root WebApplicationContext: initialization completed in 1151 ms
...
05:00:54.088 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
05:00:54.237 [main] INFO  org.eclipse.jetty.server.session - DefaultSessionIdManager workerName=node0
05:00:54.237 [main] INFO  org.eclipse.jetty.server.session - No SessionScavenger set, using defaults
05:00:54.238 [main] INFO  org.eclipse.jetty.server.session - node0 Scavenging every 660000ms
05:00:54.244 [main] INFO  o.e.j.server.handler.ContextHandler - Started o.s.b.w.e.j.JettyEmbeddedWebAppContext@21f91efa{application,/,[file:///tmp/jetty-docbase.7582962306203503723.8070/],AVAILABLE}
05:00:54.245 [main] INFO  org.eclipse.jetty.server.Server - Started @9181ms
...
...
05:00:55.810 [main] INFO  o.s.c.n.eureka.InstanceInfoFactory - Setting initial instance status as: STARTING
05:00:55.824 [main] INFO  c.netflix.discovery.DiscoveryClient - Initializing Eureka in region us-east-1
05:00:55.825 [main] INFO  c.netflix.discovery.DiscoveryClient - Client configured to neither register nor query for data.
05:00:55.829 [main] INFO  c.netflix.discovery.DiscoveryClient - Discovery Client initialized at timestamp 1654603255828 with initial instances count: 0
05:00:55.853 [main] INFO  c.n.e.DefaultEurekaServerContext - Initializing ...
05:00:55.855 [main] WARN  c.n.eureka.cluster.PeerEurekaNodes - The replica size seems to be empty. Check the route 53 DNS Registry
05:00:55.902 [main] INFO  c.n.e.r.AbstractInstanceRegistry - Finished initializing remote region registries. All known remote regions: []
05:00:55.904 [main] INFO  c.n.e.DefaultEurekaServerContext - Initialized
05:00:55.913 [main] INFO  o.s.b.a.e.web.EndpointLinksResolver - Exposing 17 endpoint(s) beneath base path '/actuator'
05:00:55.941 [main] INFO  o.s.c.n.e.s.EurekaServiceRegistry - Registering application EUREKA-SERVER with eureka with status UP
05:00:55.944 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerBootstrap - Setting the eureka configuration..
05:00:55.957 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerBootstrap - Eureka data center value eureka.datacenter is not set, defaulting to default
05:00:55.958 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerBootstrap - Eureka environment value eureka.environment is not set, defaulting to test
05:00:55.980 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerBootstrap - isAws returned false
05:00:55.981 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerBootstrap - Initialized server context
05:00:55.981 [Thread-19] INFO  c.n.e.r.PeerAwareInstanceRegistryImpl - Got 1 instances from neighboring DS node
05:00:55.982 [Thread-19] INFO  c.n.e.r.PeerAwareInstanceRegistryImpl - Renew threshold is: 1
05:00:55.982 [Thread-19] INFO  c.n.e.r.PeerAwareInstanceRegistryImpl - Changing status to UP
05:00:56.002 [Thread-19] INFO  o.s.c.n.e.s.EurekaServerInitializerConfiguration - Started Eureka Server
05:00:56.021 [main] INFO  c.s.j.s.i.a.WebApplicationImpl - Initiating Jersey application, version 'Jersey: 1.19.1 03/11/2016 02:08 PM'
...
...
05:00:56.283 [main] INFO  o.e.j.s.h.ContextHandler.application - Initializing Spring DispatcherServlet 'dispatcherServlet'
05:00:56.283 [main] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
05:00:56.287 [main] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 4 ms
05:00:56.302 [main] INFO  o.e.jetty.server.AbstractConnector - Started ServerConnector@2001e48c{HTTP/1.1, (http/1.1)}{0.0.0.0:8070}
05:00:56.303 [main] INFO  o.s.b.w.e.jetty.JettyWebServer - Jetty started on port(s) 8070 (http/1.1) with context path '/'
05:00:56.304 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8070
05:00:57.306 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
05:00:57.311 [main] INFO  o.j.e.c.e.EurekaServerApplication - Started EurekaServerApplication in 11.719 seconds (JVM running for 12.247)
```
</p>
</details>

### Gateway Service
- Description: A service gateway acts as an intermediary between the service client and an invoked service, pulls apart the path coming in from the service client call and determines what service the service client is trying to invoke. It is a central point to apply rules, policies for requests and responses from/to servicies. The Spring Cloud Gateway allows us to implement custom business logic through filters (pre- and post-filters), it integrates with Netflix’s Eureka Server and can automatically map services registered with Eureka to a route.

- Run by commandline
```
cd ../courseman/msa/modules/gatewayserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.gatewayserver.ApiGatewayServerApplication`

- Output:
<details><summary>Click to view</summary>
<p>

```
 .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)

2022-06-07 05:06:31.165  INFO 187066 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8071
2022-06-07 05:06:35.005  INFO 187066 --- [           main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=gateway-server, profiles=[default], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
2022-06-07 05:06:35.008  INFO 187066 --- [           main] b.c.PropertySourceBootstrapConfiguration : Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/gateway-server.yml'}]
2022-06-07 05:06:35.103  INFO 187066 --- [           main] o.j.e.c.g.ApiGatewayServerApplication    : No active profile set, falling back to default profiles: default
...
...
...
2022-06-07 05:06:42.093  WARN 187066 --- [           main] iguration$LoadBalancerCaffeineWarnLogger : Spring Cloud LoadBalancer is currently working with the default cache. You can switch to using Caffeine cache, by adding it to the classpath.
2022-06-07 05:06:42.133  INFO 187066 --- [           main] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
...
2022-06-07 05:06:43.160  INFO 187066 --- [           main] o.s.c.n.e.s.EurekaServiceRegistry        : Registering application GATEWAY-SERVER with eureka with status UP
2022-06-07 05:06:43.440  INFO 187066 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8072
2022-06-07 05:06:43.452  INFO 187066 --- [           main] .s.c.n.e.s.EurekaAutoServiceRegistration : Updating port to 8072
...
2022-06-07 05:06:45.456  INFO 187066 --- [           main] o.j.e.c.g.ApiGatewayServerApplication    : Started ApiGatewayServerApplication in 17.272 seconds (JVM running for 17.529)
```
</p>
</details>

## 4. Run the domain (business) services
### Database setup
- Create a PostgreSQL database named `domainds`, owned by the account: (user, password) = `(admin, password)`

- Create the **database schemas** for the services:
```
create schema if not exists assessmenthub;
  -- tables: student, class, address, teacher, coursemodule, electivemodule, compulsorymodule, enrolment
create schema if not exists coursemgnt;
  -- tables: student, teacher, coursemodule, electivemodule, compulsorymodule, enrolment, class, address
create schema if not exists class; 
  -- tables: class
create schema if not exists address;
  -- tables: address
create schema if not exists coursemodule; 
  -- tables: coursemodule, electivemodule, compulsorymodule
create schema if not exists teacher;
  -- tables: teacher
create schema if not exists enrolment;
  -- tables: enrolment
create schema if not exists student;
  -- tables: student
```

### Service: Address (address-service)
- Run by command:
```
cd ../courseman/msa/modules/servicestmsa/address-service
mvn spring-boot:run
```

### Service: AcademicAdmin (academicadmin-service)
- Run by command:
```
cd ../courseman/msa/modules/servicestmsa/academicadmin-service
mvn spring-boot:run
```
### Service: Student Class (class-service)
- Run by command:
```
cd ../courseman/msa/modules/servicestmsa/class-service
mvn spring-boot:run
```
### Service: AssessmentHub (assessmenthub-service)
- Run by command:
```
cd ../courseman/msa/modules/servicestmsa/assessmenthub-service
mvn spring-boot:run
```
### Service: Course Management (coursemgmt-service)
- Run by command:
```
cd ../courseman/msa/modules/servicestmsa/coursemgmt-service
mvn spring-boot:run
```
# Service: Application Reconfiguration

**Note**: This is an advanced topic and work-in-progress. You can skip it for now! 

## Service(Reconfigurer)
Version 2.0 logic flow (as described in the paper). This involves the coordination of two instances SR1 and SR2.

- `gateway-server: localhost:8072`

### Promote

#### SR1: Service(Reconfigurer)
- SR1 is run on the same host as `sourceServ`

  + in-request:
    - url: http://$gateway-server/reconfigurer-service/promote
    + body (parameters):
      - sourceServ (s1): academicadmin-service
      - module (m):	coursemodulemgmt
      - targetServ (s2): academicadmin-service
        --> promote($sourceServ, $module, $targetServ)

  + promoteModule(s1, m, s2)
    --> D : ModuleDesc = transform(s1, m)
    --> SR2.runService(s2, D)
    --> promoteCompleted(s1, D.pid)
    --> s1.remove(m)

  + transform(service, module): ModuleDesc
    --> create and return D: ModuleDesc containing all information about `module` in `service`. This includes module id, name and the service's executable jar file

    	- service: academicadmin-service
    	- module: cmodulemgnt
    - `serviceDeployPath`: /data/projects/jda/examples/courseman/msa/tmp
      - `jarFile`: $serviceDeployPath/coursemodulemgmt-service/coursemodulemgmt-service.jar

#### SR2: Service(Reconfigurer)
- SR2 is run on the same host as `targetServ`

  + runService(service, modDesc: ModuleDesc)		
    --> store file modDesc.file to storage
    --> use `java -jar` command to execute this file
    --> registerChildService()
  + registerChildService()
    - targetServ: academicadmin-service
    - registerChildPath: http://localhost:8072/academicadmin-service/registerChildService
    + request: multipart-form-data
      - childName: hello-service


#### s1: Service(AcademicAdmin)

	+ in-request
		- url: 

	+ remove(m)
		--> remove m from service tree of this 

### Demote

#### SR1: Service(Reconfigurer)
- SR1 is run on the same host as `sourceServ`

  + in-request:
    - url:  http://$gateway-server/reconfigurer-service/demote
    + body (parameters):
      - sourceServ (s1): academicadmin-service
      - demServ (sc):	cmodulemgnt
      - targetServ (s2): academicadmin-service
      - targetParentMod (p): coursemgnt

      --> demote($sourceServ, $demServ, $targetServ, targetParentMod)

    + demote(sourceServ, sc, targetServ, targetParentMod)
      --> D : ModuleDesc = deform(s1, sc)
      --> initRunModule(SR2, s2, p, D)
      --> SR2.runModule(s2, p, D)
      --> demoteCompleted(s1, D.pid)
      --> s1.remove(sc)

## Version 1.0 logic flow
### Service(Source)
	+ in-request:
		- url: localhost:8098/promote/cmodulemgnt
		+ body (parameters):
			- databaseSchema: ll3tlOTvp/cmodulemgnt.sql
			- targetHost: http://localhost:8099
	
	+ promote()
		- serviceDir: /home/ducmle/projects/jda/examples/courseman/msa/modules/servicestmsa/example-service
		
		--> sendFile() --> runService() --> promoteCompleted()

	+ sendFile()
		- module: cmodulemgnt
		- jarfile:  $serviceDir/target/cmodulemgnt-service-0.0.1-SNAPSHOT.jar
		--> $Target.receiveFile()

	+ runService()
		- fullTargerURL: $targetHost/runService/cmodulemgnt
		--> $Service(Target)

	+ promoteCompleted()
		- removeUri: http://localhost:8072/academicadmin-service/coursemgnt/removemodule/cmodulemgnt
		--> $Service(AcademicAdmin)

### Service(Target)
	+ in-request: 
		- url: http://localhost:8099 

	+ receiveFile()
		- fileName: cmodulemgnt-service-0.0.1-SNAPSHOT.jar
		- fileFolder: /data/projects/jda/execute/cmodulemgnt
		- module: cmodulemgnt

	+ runService()
		- module: cmodulemgnt
		- jarFileName: cmodulemgnt-service-0.0.1-SNAPSHOT.jar
		- jarFilePath: /data/projects/jda/execute/cmodulemgnt/cmodulemgnt-service-0.0.1-SNAPSHOT.jar

### Service(AcademicAdmin)
	+ in-request:
		- url: http://localhost:8072/academicadmin-service/coursemgnt/removemodule/cmodulemgnt
	+ handleCourseManagement():
		- path: http://$gateway-server/coursemgnt-service/removemodule/cmodulemgnt
		- requestData: ""
		--> $Service(CourseMgnt)

### Service(CourseMgnt)
	+ in-request
		- url: http://$gateway-server/coursemgnt-service/removemodule/cmodulemgnt

	+ removeModule()
		- serviceName: cmodulemgnt
		- servicePath: /cmodulemgnt/**
		- mappingInfo: RequestMappingInfo($servicePath)
		- requestMappingHandlerMapping: applicationContext.beans[RequestMappingHandlerMapping]
		--> RequestMappingHandlerMapping.unregisterMapping($mappingInfo)



















