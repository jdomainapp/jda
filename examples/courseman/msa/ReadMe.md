Example: `jda-eg-coursemanmsa`
===============================

A version of the `CourseMan` software that demonstrates the state-of-the-art MSA technologies.

It extends a previous version (`jda-eg-coursemanmosar`) to use MSA instead of the the RESTFUL API.

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
 
| Submodels | Modules | |
| :--: | :--: |  :--: |
| Address Administration | Address | 
| Student Registration | Student | uses Address 
| Class Registration | StudentClass | uses Student
| Enrolment Administration | Enrolment | uses Student, Course Module
| Course Administration | CourseModule, CompulsoryModule, ElectiveModule  | 

#### Technical requirements

- Github branch: `usecase1`

### Use case 2: Academic and payment extensions

#### Domain modelling requirements

The domain modelling requirements of this use case are given below:  

| Submodels | Modules | Extensions 
| :--: | :--: | :--: | 
|Address Administration | Address | 
| Student Registration | Student | 
| Class Registration | StudentClass |
| Enrolment Administration | Enrolment | only with student and course module details
| Academic Administration | Enrolment | with mark and grade details
| Course Administration | CourseModule, CompulsoryModule, ElectiveModule |  CourseModule has a unit fee
| Finance Administration | CoursePayment | Payment for course module enrolment, computed from the unit fees of the enrolled CourseModules

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

## 1. Start Apache Kafka and register topics

```
# Start the ZooKeeper service
bin/zookeeper-server-start.sh config/zookeeper.properties
```

```
# Start the Kafka broker service
bin/kafka-server-start.sh config/server.properties
```

```
# create topic: "streams-courseman-coursemodules"
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic courseChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic studentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic addressChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic classChangeTopic --bootstrap-server localhost:9092
```
## 2. Build projects
```
cd ../courseman/msa/
mvn clean install -DskipTests
```
- Output:
```
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] Example: CourseMan MSA (basics) 1.0 ................ SUCCESS [  0.116 s]
[INFO] Example: CourseMan MSA basics (Modules) 1.0 ........ SUCCESS [  0.006 s]
[INFO] Configuration Server 0.0.1-SNAPSHOT ................ SUCCESS [  1.613 s]
[INFO] Eureka Server 0.0.1-SNAPSHOT ....................... SUCCESS [  0.562 s]
[INFO] API Gateway server 0.0.1-SNAPSHOT .................. SUCCESS [  0.494 s]
[INFO] Example: CourseMan MSA basics (Modules) 1.0 ........ SUCCESS [  0.004 s]
[INFO] address-service 0.0.1-SNAPSHOT ..................... SUCCESS [  5.157 s]
[INFO] class-service 0.0.1-SNAPSHOT ....................... SUCCESS [  1.209 s]
[INFO] course-service 0.0.1-SNAPSHOT ...................... SUCCESS [  0.996 s]
[INFO] student-service 0.0.1-SNAPSHOT ..................... SUCCESS [  1.192 s]
[INFO] Example: CourseMan MSA basics (Business Services) 1.0 SUCCESS [  0.007 s]
[INFO] enrolment-service1 0.0.1-SNAPSHOT .................. SUCCESS [  7.284 s]
[INFO] Example: CourseMan MSA basics (Business Services) 1.0 SUCCESS [  0.016 s]
[INFO] enrolment-service2 0.0.1-SNAPSHOT .................. SUCCESS [ 15.795 s]
[INFO] academic-service2 0.0.1-SNAPSHOT ................... SUCCESS [  1.295 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
## 3. Run common services in order

### Run Config service
- Description:
- Run by commandline
```
cd ../courseman/msa/modules/configserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.configserver.ConfigurationServerApplication`

- Output
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

### Run Discovery Service
- Description:
- Run by commandline
```
cd ../courseman/msa/modules/eurekaserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.eurekaserver.EurekaServerApplication`
- Output
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
05:00:51.501 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/eureka-server.yml'}]
05:00:51.509 [main] INFO  o.j.e.c.e.EurekaServerApplication - No active profile set, falling back to default profiles: default
05:00:52.121 [main] WARN  o.s.boot.actuate.endpoint.EndpointId - Endpoint ID 'service-registry' contains invalid characters, please migrate to a valid format.
05:00:52.234 [main] INFO  o.s.cloud.context.scope.GenericScope - BeanFactory id=5fb159de-ff08-34fb-b371-21170586b796
05:00:52.552 [main] INFO  org.eclipse.jetty.util.log - Logging initialized @7486ms to org.eclipse.jetty.util.log.Slf4jLog
05:00:52.635 [main] INFO  o.s.b.w.e.j.JettyServletWebServerFactory - Server initialized with port: 8070
05:00:52.639 [main] INFO  org.eclipse.jetty.server.Server - jetty-9.4.27.v20200227; built: 2020-02-27T18:37:21.340Z; git: a304fd9f351f337e7c0e2a7c28878dd536149c6c; jvm 11.0.15+10-Ubuntu-0ubuntu0.20.04.1
05:00:52.676 [main] INFO  o.e.j.s.h.ContextHandler.application - Initializing Spring embedded WebApplicationContext
05:00:52.677 [main] INFO  o.s.web.context.ContextLoader - Root WebApplicationContext: initialization completed in 1151 ms
05:00:52.809 [main] WARN  c.n.c.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.
05:00:52.809 [main] INFO  c.n.c.sources.URLConfigurationSource - To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
05:00:52.821 [main] INFO  c.n.config.DynamicPropertyFactory - DynamicPropertyFactory is initialized with configuration sources: com.netflix.config.ConcurrentCompositeConfiguration@5e1a986c
05:00:54.088 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
05:00:54.237 [main] INFO  org.eclipse.jetty.server.session - DefaultSessionIdManager workerName=node0
05:00:54.237 [main] INFO  org.eclipse.jetty.server.session - No SessionScavenger set, using defaults
05:00:54.238 [main] INFO  org.eclipse.jetty.server.session - node0 Scavenging every 660000ms
05:00:54.244 [main] INFO  o.e.j.server.handler.ContextHandler - Started o.s.b.w.e.j.JettyEmbeddedWebAppContext@21f91efa{application,/,[file:///tmp/jetty-docbase.7582962306203503723.8070/],AVAILABLE}
05:00:54.245 [main] INFO  org.eclipse.jetty.server.Server - Started @9181ms
05:00:54.253 [main] WARN  c.n.c.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.
05:00:54.254 [main] INFO  c.n.c.sources.URLConfigurationSource - To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
05:00:54.417 [main] INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'applicationTaskExecutor'
05:00:55.642 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
05:00:55.688 [main] WARN  o.s.c.l.c.LoadBalancerCacheAutoConfiguration$LoadBalancerCaffeineWarnLogger - Spring Cloud LoadBalancer is currently working with the default cache. You can switch to using Caffeine cache, by adding it to the classpath.
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.thoughtworks.xstream.core.util.Fields (file:/home/vietdo/.m2/repository/com/thoughtworks/xstream/xstream/1.4.11.1/xstream-1.4.11.1.jar) to field java.util.TreeMap.comparator
WARNING: Please consider reporting this to the maintainers of com.thoughtworks.xstream.core.util.Fields
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
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
05:00:56.059 [main] INFO  c.n.d.p.DiscoveryJerseyProvider - Using JSON encoding codec LegacyJacksonJson
05:00:56.059 [main] INFO  c.n.d.p.DiscoveryJerseyProvider - Using JSON decoding codec LegacyJacksonJson
05:00:56.059 [main] INFO  c.n.d.p.DiscoveryJerseyProvider - Using XML encoding codec XStreamXml
05:00:56.059 [main] INFO  c.n.d.p.DiscoveryJerseyProvider - Using XML decoding codec XStreamXml
05:00:56.283 [main] INFO  o.e.j.s.h.ContextHandler.application - Initializing Spring DispatcherServlet 'dispatcherServlet'
05:00:56.283 [main] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
05:00:56.287 [main] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 4 ms
05:00:56.302 [main] INFO  o.e.jetty.server.AbstractConnector - Started ServerConnector@2001e48c{HTTP/1.1, (http/1.1)}{0.0.0.0:8070}
05:00:56.303 [main] INFO  o.s.b.w.e.jetty.JettyWebServer - Jetty started on port(s) 8070 (http/1.1) with context path '/'
05:00:56.304 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8070
05:00:57.306 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
05:00:57.311 [main] INFO  o.j.e.c.e.EurekaServerApplication - Started EurekaServerApplication in 11.719 seconds (JVM running for 12.247)
```
### Run Gateway Service
- Description:
- Run by commandline
```
cd ../courseman/msa/modules/gatewayserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.gatewayserver.ApiGatewayServerApplication`

- Output
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
2022-06-07 05:06:35.978  WARN 187066 --- [           main] o.s.boot.actuate.endpoint.EndpointId     : Endpoint ID 'service-registry' contains invalid characters, please migrate to a valid format.
2022-06-07 05:06:36.096  INFO 187066 --- [           main] o.s.cloud.context.scope.GenericScope     : BeanFactory id=d1ed60df-c907-37cb-9a0b-ab19462c43e9
2022-06-07 05:06:36.269  INFO 187066 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2022-06-07 05:06:36.271  INFO 187066 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig' of type [org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration$ReactorDeferringLoadBalancerFilterConfig] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2022-06-07 05:06:36.272  INFO 187066 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'reactorDeferringLoadBalancerExchangeFilterFunction' of type [org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2022-06-07 05:06:36.431  WARN 187066 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
2022-06-07 05:06:36.435  WARN 187066 --- [           main] c.n.c.sources.URLConfigurationSource     : No URLs will be polled as dynamic configuration sources.
2022-06-07 05:06:37.596  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:38.673  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:39.678  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [After]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Before]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Between]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Cookie]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Header]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Host]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Method]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Path]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Query]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [ReadBodyPredicateFactory]
2022-06-07 05:06:40.502  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [RemoteAddr]
2022-06-07 05:06:40.503  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Weight]
2022-06-07 05:06:40.503  INFO 187066 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [CloudFoundryRouteService]
2022-06-07 05:06:40.844  INFO 187066 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 18 endpoint(s) beneath base path '/actuator'
2022-06-07 05:06:42.060  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:42.093  WARN 187066 --- [           main] iguration$LoadBalancerCaffeineWarnLogger : Spring Cloud LoadBalancer is currently working with the default cache. You can switch to using Caffeine cache, by adding it to the classpath.
2022-06-07 05:06:42.133  INFO 187066 --- [           main] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.thoughtworks.xstream.core.util.Fields (file:/home/vietdo/.m2/repository/com/thoughtworks/xstream/xstream/1.4.11.1/xstream-1.4.11.1.jar) to field java.util.TreeMap.comparator
WARNING: Please consider reporting this to the maintainers of com.thoughtworks.xstream.core.util.Fields
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
2022-06-07 05:06:43.160  INFO 187066 --- [           main] o.s.c.n.e.s.EurekaServiceRegistry        : Registering application GATEWAY-SERVER with eureka with status UP
2022-06-07 05:06:43.440  INFO 187066 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8072
2022-06-07 05:06:43.452  INFO 187066 --- [           main] .s.c.n.e.s.EurekaAutoServiceRegistration : Updating port to 8072
2022-06-07 05:06:44.453  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:45.454  INFO 187066 --- [           main] o.s.cloud.commons.util.InetUtils         : Cannot determine local hostname
2022-06-07 05:06:45.456  INFO 187066 --- [           main] o.j.e.c.g.ApiGatewayServerApplication    : Started ApiGatewayServerApplication in 17.272 seconds (JVM running for 17.529)
```

## 3. Run business services
### Setup
- Each service create a postgresql database `domainds` with user/password: admin/password

### Student Service
- Description:
- Create database
  + Create schema `student`
  + Create tables
```
CREATE TABLE IF NOT EXISTS student.student
(
    id character varying(6) COLLATE pg_catalog."default" NOT NULL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    studentclass_id integer,
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student.class
(
    id integer NOT NULL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT class_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS student.address
(
    id integer NOT NULL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT address_pkey PRIMARY KEY (id)
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/student-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.student.StudentServiceApplication`

- Output
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.2.6.RELEASE)

15:27:04.559 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
15:27:05.912 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=student-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
15:27:05.913 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service.properties'}]
15:27:05.949 [main] INFO  o.j.e.c.s.StudentServiceApplication - The following profiles are active: dev
15:27:06.612 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.613 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.635 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.635 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.658 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.658 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.671 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.671 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.682 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.682 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.693 [main] INFO  i.g.r.u.RxJava2OnClasspathCondition - RxJava2 related Aspect extensions are not activated, because RxJava2 is not on the classpath.
15:27:06.694 [main] INFO  i.g.r.u.ReactorOnClasspathCondition - Reactor related Aspect extensions are not activated because Resilience4j Reactor module is not on the classpath.
15:27:06.905 [main] INFO  o.s.d.r.c.RepositoryConfigurationDelegate - Bootstrapping Spring Data JPA repositories in DEFAULT mode.
15:27:07.029 [main] INFO  o.s.d.r.c.RepositoryConfigurationDelegate - Finished Spring Data repository scanning in 118ms. Found 3 JPA repository interfaces.
15:27:07.174 [main] WARN  o.s.boot.actuate.endpoint.EndpointId - Endpoint ID 'service-registry' contains invalid characters, please migrate to a valid format.
15:27:07.406 [main] INFO  o.s.cloud.context.scope.GenericScope - BeanFactory id=668c88e7-f27d-3996-af98-b49d6a2198bd
15:27:07.546 [main] INFO  o.s.i.c.DefaultConfiguringBeanFactoryPostProcessor - No bean named 'errorChannel' has been explicitly defined. Therefore, a default PublishSubscribeChannel will be created.
15:27:07.551 [main] INFO  o.s.i.c.DefaultConfiguringBeanFactoryPostProcessor - No bean named 'taskScheduler' has been explicitly defined. Therefore, a default ThreadPoolTaskScheduler will be created.
15:27:07.556 [main] INFO  o.s.i.c.DefaultConfiguringBeanFactoryPostProcessor - No bean named 'integrationHeaderChannelRegistry' has been explicitly defined. Therefore, a default DefaultHeaderChannelRegistry will be created.
15:27:07.822 [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'integrationChannelResolver' of type [org.springframework.integration.support.channel.BeanFactoryChannelResolver] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:27:07.827 [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'integrationDisposableAutoCreatedBeans' of type [org.springframework.integration.config.annotation.Disposables] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:27:07.871 [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'org.springframework.integration.config.IntegrationManagementConfiguration' of type [org.springframework.integration.config.IntegrationManagementConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:27:08.259 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8080 (http)
15:27:08.267 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8080"]
15:27:08.268 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
15:27:08.268 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
15:27:08.363 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
15:27:08.535 [main] WARN  c.n.c.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.
15:27:09.782 [main] INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService 'taskScheduler'
15:27:10.065 [main] INFO  o.h.jpa.internal.util.LogHelper - HHH000204: Processing PersistenceUnitInfo [name: default]
15:27:10.114 [main] INFO  org.hibernate.Version - HHH000412: Hibernate ORM core version 5.4.12.Final
15:27:10.202 [main] INFO  o.h.annotations.common.Version - HCANN000001: Hibernate Commons Annotations {5.1.0.Final}
15:27:10.281 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
15:27:10.537 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
15:27:10.552 [main] INFO  org.hibernate.dialect.Dialect - HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
15:27:11.083 [main] INFO  o.h.e.t.j.p.i.JtaPlatformInitiator - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
15:27:11.089 [main] INFO  o.s.o.j.LocalContainerEntityManagerFactoryBean - Initialized JPA EntityManagerFactory for persistence unit 'default'
15:27:11.537 [main] WARN  o.s.b.a.o.j.JpaBaseConfiguration$JpaWebConfiguration - spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
15:27:11.841 [main] WARN  c.n.c.sources.URLConfigurationSource - No URLs will be polled as dynamic configuration sources.
15:27:12.046 [main] INFO  o.s.c.s.f.FunctionConfiguration$FunctionBindingRegistrar - Functional binding is disabled due to the presense of @EnableBinding annotation in your configuration
15:27:12.073 [main] INFO  o.s.c.f.c.c.BeanFactoryAwareFunctionRegistry - Looking up function 'null' with acceptedOutputTypes: []
15:27:12.080 [main] INFO  o.s.c.f.c.c.BeanFactoryAwareFunctionRegistry - Looking up function 'null' with acceptedOutputTypes: []
15:27:12.584 [main] WARN  o.s.c.l.c.LoadBalancerCacheAutoConfiguration$LoadBalancerCaffeineWarnLogger - Spring Cloud LoadBalancer is currently working with the default cache. You can switch to using Caffeine cache, by adding it to the classpath.
15:27:12.628 [main] INFO  o.s.b.a.e.web.EndpointLinksResolver - Exposing 34 endpoint(s) beneath base path '/actuator'
15:27:12.761 [main] INFO  o.s.c.s.m.DirectWithAttributesChannel - Channel 'student-service-1.inboundAddressChanges' has 1 subscriber(s).
15:27:12.761 [main] INFO  o.s.c.s.m.DirectWithAttributesChannel - Channel 'student-service-1.inboundClassChanges' has 1 subscriber(s).
15:27:12.781 [main] INFO  o.s.i.endpoint.EventDrivenConsumer - Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
15:27:12.781 [main] INFO  o.s.i.c.PublishSubscribeChannel - Channel 'student-service-1.errorChannel' has 1 subscriber(s).
15:27:12.781 [main] INFO  o.s.i.endpoint.EventDrivenConsumer - started bean '_org.springframework.integration.errorLogger'
15:27:12.821 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
15:27:13.354 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=student-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
15:27:13.354 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service.properties'}]
15:27:13.579 [main] INFO  o.s.c.s.b.k.p.KafkaTopicProvisioner - Using kafka topic for outbound: studentChangeTopic
15:27:13.583 [main] INFO  o.a.k.c.admin.AdminClientConfig - AdminClientConfig values: 
	bootstrap.servers = [localhost:9092]
	client.dns.lookup = default
	client.id = 
	connections.max.idle.ms = 300000
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 120000
	retries = 5
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS

15:27:13.625 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:13.625 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:13.625 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640833624
15:27:13.818 [main] INFO  o.a.k.c.producer.ProducerConfig - ProducerConfig values: 
	acks = 1
	batch.size = 16384
	bootstrap.servers = [localhost:9092]
	buffer.memory = 33554432
	client.dns.lookup = default
	client.id = 
	compression.type = none
	connections.max.idle.ms = 540000
	delivery.timeout.ms = 120000
	enable.idempotence = false
	interceptor.classes = []
	key.serializer = class org.apache.kafka.common.serialization.ByteArraySerializer
	linger.ms = 0
	max.block.ms = 60000
	max.in.flight.requests.per.connection = 5
	max.request.size = 1048576
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partitioner.class = class org.apache.kafka.clients.producer.internals.DefaultPartitioner
	receive.buffer.bytes = 32768
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retries = 0
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	transaction.timeout.ms = 60000
	transactional.id = null
	value.serializer = class org.apache.kafka.common.serialization.ByteArraySerializer

15:27:13.844 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:13.845 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:13.846 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640833844
15:27:13.862 [kafka-producer-network-thread | producer-1] INFO  org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: tUi9gttaSlifOFG7cymdtA
15:27:13.863 [main] INFO  o.a.k.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 30000 ms.
15:27:13.879 [main] INFO  o.s.c.s.m.DirectWithAttributesChannel - Channel 'student-service-1.output' has 1 subscriber(s).
15:27:13.893 [main] INFO  o.s.c.n.eureka.InstanceInfoFactory - Setting initial instance status as: STARTING
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.thoughtworks.xstream.core.util.Fields (file:/home/vietdo/.m2/repository/com/thoughtworks/xstream/xstream/1.4.11.1/xstream-1.4.11.1.jar) to field java.util.TreeMap.comparator
WARNING: Please consider reporting this to the maintainers of com.thoughtworks.xstream.core.util.Fields
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
15:27:14.423 [main] INFO  o.s.c.n.e.s.EurekaServiceRegistry - Registering application STUDENT-SERVICE with eureka with status UP
15:27:14.457 [main] INFO  o.a.k.c.admin.AdminClientConfig - AdminClientConfig values: 
	bootstrap.servers = [localhost:9092]
	client.dns.lookup = default
	client.id = 
	connections.max.idle.ms = 300000
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 120000
	retries = 5
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS

15:27:14.459 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.459 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.459 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834459
15:27:14.493 [main] INFO  o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 100
	auto.offset.reset = earliest
	bootstrap.servers = [localhost:9092]
	check.crcs = true
	client.dns.lookup = default
	client.id = 
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = false
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = studentAddressGroup
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer

15:27:14.526 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.527 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.528 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834526
15:27:14.610 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'addressChangeTopic.studentAddressGroup.errors' has 1 subscriber(s).
15:27:14.613 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'addressChangeTopic.studentAddressGroup.errors' has 0 subscriber(s).
15:27:14.614 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'addressChangeTopic.studentAddressGroup.errors' has 1 subscriber(s).
15:27:14.615 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'addressChangeTopic.studentAddressGroup.errors' has 2 subscriber(s).
15:27:14.648 [main] INFO  o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 100
	auto.offset.reset = earliest
	bootstrap.servers = [localhost:9092]
	check.crcs = true
	client.dns.lookup = default
	client.id = 
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = false
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = studentAddressGroup
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer

15:27:14.652 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.652 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.653 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834652
15:27:14.656 [main] INFO  o.a.k.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Subscribed to topic(s): addressChangeTopic
15:27:14.661 [main] INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService
15:27:14.682 [main] INFO  o.s.i.k.i.KafkaMessageDrivenChannelAdapter - started org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter@1d39d660
15:27:14.689 [main] INFO  o.a.k.c.admin.AdminClientConfig - AdminClientConfig values: 
	bootstrap.servers = [localhost:9092]
	client.dns.lookup = default
	client.id = 
	connections.max.idle.ms = 300000
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 120000
	retries = 5
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS

15:27:14.695 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.695 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.695 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834695
15:27:14.723 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  org.apache.kafka.clients.Metadata - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Cluster ID: tUi9gttaSlifOFG7cymdtA
15:27:14.726 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Discovered group coordinator ubuntu:9092 (id: 2147483647 rack: null)
15:27:14.741 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Revoking previously assigned partitions []
15:27:14.742 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.s.c.s.b.k.KafkaMessageChannelBinder$1 - studentAddressGroup: partitions revoked: []
15:27:14.743 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] (Re-)joining group
15:27:14.759 [main] INFO  o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 100
	auto.offset.reset = earliest
	bootstrap.servers = [localhost:9092]
	check.crcs = true
	client.dns.lookup = default
	client.id = 
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = false
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = studentClassGroup
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer

15:27:14.761 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.761 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.762 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834761
15:27:14.770 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'classChangeTopic.studentClassGroup.errors' has 1 subscriber(s).
15:27:14.770 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'classChangeTopic.studentClassGroup.errors' has 0 subscriber(s).
15:27:14.770 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'classChangeTopic.studentClassGroup.errors' has 1 subscriber(s).
15:27:14.771 [main] INFO  o.s.c.s.binder.BinderErrorChannel - Channel 'classChangeTopic.studentClassGroup.errors' has 2 subscriber(s).
15:27:14.771 [main] INFO  o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 100
	auto.offset.reset = earliest
	bootstrap.servers = [localhost:9092]
	check.crcs = true
	client.dns.lookup = default
	client.id = 
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = false
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = studentClassGroup
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2, TLSv1.1, TLSv1]
	ssl.endpoint.identification.algorithm = https
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLS
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.ByteArrayDeserializer

15:27:14.779 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
15:27:14.783 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] (Re-)joining group
15:27:14.789 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
15:27:14.789 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1654640834779
15:27:14.789 [main] INFO  o.a.k.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-4, groupId=studentClassGroup] Subscribed to topic(s): classChangeTopic
15:27:14.790 [main] INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService
15:27:14.804 [main] INFO  o.s.i.k.i.KafkaMessageDrivenChannelAdapter - started org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter@268f0cd1
15:27:14.817 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  org.apache.kafka.clients.Metadata - [Consumer clientId=consumer-4, groupId=studentClassGroup] Cluster ID: tUi9gttaSlifOFG7cymdtA
15:27:14.820 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] Discovered group coordinator ubuntu:9092 (id: 2147483647 rack: null)
15:27:14.828 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] Revoking previously assigned partitions []
15:27:14.829 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.s.c.s.b.k.KafkaMessageChannelBinder$1 - studentClassGroup: partitions revoked: []
15:27:14.829 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] (Re-)joining group
15:27:14.843 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] (Re-)joining group
15:27:14.901 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Successfully joined group with generation 1
15:27:14.903 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Setting newly assigned partitions: addressChangeTopic-0
15:27:14.904 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.AbstractCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] Successfully joined group with generation 1
15:27:14.904 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] Setting newly assigned partitions: classChangeTopic-0
15:27:14.927 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Found no committed offset for partition addressChangeTopic-0
15:27:14.927 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.ConsumerCoordinator - [Consumer clientId=consumer-4, groupId=studentClassGroup] Found no committed offset for partition classChangeTopic-0
15:27:14.952 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.SubscriptionState - [Consumer clientId=consumer-4, groupId=studentClassGroup] Resetting offset for partition classChangeTopic-0 to offset 0.
15:27:14.952 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.a.k.c.c.i.SubscriptionState - [Consumer clientId=consumer-2, groupId=studentAddressGroup] Resetting offset for partition addressChangeTopic-0 to offset 1.
15:27:14.979 [KafkaConsumerDestination{consumerDestinationName='addressChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.s.c.s.b.k.KafkaMessageChannelBinder$1 - studentAddressGroup: partitions assigned: [addressChangeTopic-0]
15:27:14.979 [KafkaConsumerDestination{consumerDestinationName='classChangeTopic', partitions=1, dlqName='null'}.container-0-C-1] INFO  o.s.c.s.b.k.KafkaMessageChannelBinder$1 - studentClassGroup: partitions assigned: [classChangeTopic-0]
15:27:15.044 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
15:27:15.092 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http) with context path ''
15:27:15.093 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8080
15:27:15.105 [main] INFO  o.j.e.c.s.StudentServiceApplication - Started StudentServiceApplication in 11.353 seconds (JVM running for 11.847)
```

- Functions:

### Address Service
- Description:
- Create database
  + Create schema `address`
  + Create tables
```
CREATE TABLE IF NOT EXISTS address.address
(
    id SERIAL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT address_pkey PRIMARY KEY (id)
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/address-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.address.AddressServiceApplication`
- Output
```
```
- Functions:

### Class Service
- Description: 
- Create database
  + Create schema `class`
  + Create tables
```
CREATE TABLE IF NOT EXISTS class.studentclass
(
    id SERIAL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT studentclass_pkey PRIMARY KEY (id)
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/class-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.class.ClassServiceApplication`
- Output
```
```
- Functions:

### Course Service
- Description:
- Create database
  + Create schema `course`
  + Create tables
```
CREATE TABLE IF NOT EXISTS course.coursemodule
(
    id SERIAL,
    code character varying(12) COLLATE pg_catalog."default",
    name character varying(30) COLLATE pg_catalog."default",
    semester integer,
    credits integer,
    CONSTRAINT coursemodule_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS course.compulsorymodule
(
    id integer NOT NULL,
    CONSTRAINT course_compulsorymodulepk PRIMARY KEY (id),
    CONSTRAINT course_compulsorymodulefk1 FOREIGN KEY (id)
        REFERENCES course.coursemodule (id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS course.electivemodule
(
    id integer NOT NULL,
    deptname character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT course_electivemodulepk PRIMARY KEY (id),
    CONSTRAINT course_electivemodulefk1 FOREIGN KEY (id)
        REFERENCES course.coursemodule (id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE CASCADE
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/course-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.course.CourseServiceApplication`
- Output:
```
```
- Functions: 
## Usecase1
### Enrolment Service
- Description:
- Create database
  + Create schema `enrolment`
  + Create tables
```
CREATE TABLE IF NOT EXISTS enrolment.coursemodule
(
    id integer NOT NULL,
    code character varying(12) COLLATE pg_catalog."default",
    name character varying(30) COLLATE pg_catalog."default",
    semester integer,
    credits integer,
    coursemoduletype character varying(30) COLLATE pg_catalog."default",
    deptname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT coursemodule_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS enrolment.enrolment
(
    id SERIAL,
    student_id character varying(6) COLLATE pg_catalog."default",
    coursemodule_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1) COLLATE pg_catalog."default",
    CONSTRAINT enrolment_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS enrolment.student
(
    id character varying(6) COLLATE pg_catalog."default" NOT NULL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    studentclass_id integer,
    addressname character varying(30) COLLATE pg_catalog."default",
    studentclassname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT student_pkey PRIMARY KEY (id)
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/service1/enrolment-service1
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.enrolment.EnrolmentServiceApplication`
- Output:
```
```
- Functions:

## Usecase2
### Enrolment Service
- Description:
- Create database: Use `enrolment` database of usecase1
- Run 
By commandline
```
cd ../courseman/msa/modules/services/service2/enrolment-service2
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.academic.EnrolmentServiceApplication`
- Functions:

### Academic Service
- Description
- Create database
  + Create schema `academic`
  + Create tables
```
CREATE TABLE IF NOT EXISTS academic.academic
(
    id SERIAL,
    enrolment_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1) COLLATE pg_catalog."default"
)
```
- Run 
By commandline
```
cd ../courseman/msa/modules/services/service2/academic-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.academic.AcademicServiceApplication`
- Output:
```
```
- Functions: 
