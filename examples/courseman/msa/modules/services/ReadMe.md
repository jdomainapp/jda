Example: `jda-eg-coursemanmsa` version 1.0
============================================

Version 1.0 of the `CourseMan` software that demonstrates the state-of-the-art MSA technologies.

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
The domain model of the [Use case 1 of the requirements](../../ReadMe.md).

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
# create topic: "streams-courseman-coursemodules"
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic courseChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic studentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic addressChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic classChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic enrolmentChangeTopic --bootstrap-server localhost:9092
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
create schema if not exists address;
create schema if not exists student;
create schema if not exists class;
create schema if not exists enrolment;
create schema if not exists coursemodule;
create schema if not exists academic;
```

### Service: Address
- Description: This service manages all information about `address`, provides data for `student-service`
- Require a database schema named `address`.

- Run by command:
```
cd ../courseman/msa/modules/services/address-service
mvn spring-boot:run
```
or Run by class:  `org.jda.example.coursemanmsa.address.AddressServiceApplication`

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

20:54:04.629 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
20:54:04.982 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=address-service, profiles=[dev], label=null, version=null, state=null
20:54:04.983 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-classpath:/config/address-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-classpath:/config/address-service.properties'}]
20:54:05.049 [main] INFO  o.j.e.c.a.AddressServiceApplication - The following profiles are active: dev
...
...
20:54:05.876 [main] INFO  o.s.d.r.c.RepositoryConfigurationDelegate - Bootstrapping Spring Data JPA repositories in DEFAULT mode.
20:54:06.000 [main] INFO  o.s.d.r.c.RepositoryConfigurationDelegate - Finished Spring Data repository scanning in 118ms. Found 1 JPA repository interfaces.
...
...
20:54:07.420 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8082 (http)
20:54:07.430 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8082"]
20:54:07.430 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
20:54:07.431 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
20:54:07.596 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
20:54:09.307 [main] INFO  o.s.s.c.ThreadPoolTaskScheduler - Initializing ExecutorService 'taskScheduler'
20:54:09.753 [main] INFO  o.h.jpa.internal.util.LogHelper - HHH000204: Processing PersistenceUnitInfo [name: default]
20:54:09.822 [main] INFO  org.hibernate.Version - HHH000412: Hibernate ORM core version 5.4.12.Final
20:54:09.944 [main] INFO  o.h.annotations.common.Version - HCANN000001: Hibernate Commons Annotations {5.1.0.Final}
20:54:10.040 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
20:54:10.333 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
20:54:10.352 [main] INFO  org.hibernate.dialect.Dialect - HHH000400: Using dialect: org.hibernate.dialect.PostgreSQLDialect
Hibernate: create table address.address (id  serial not null, name varchar(255) not null, primary key (id))
20:54:10.991 [main] INFO  o.h.e.t.j.p.i.JtaPlatformInitiator - HHH000490: Using JtaPlatform implementation: [org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform]
20:54:10.997 [main] INFO  o.s.o.j.LocalContainerEntityManagerFactoryBean - Initialized JPA EntityManagerFactory for persistence unit 'default'
20:54:11.931 [main] INFO  o.s.c.s.f.FunctionConfiguration$FunctionBindingRegistrar - Functional binding is disabled due to the presense of @EnableBinding annotation in your configuration
20:54:12.540 [main] WARN  o.s.c.l.c.LoadBalancerCacheAutoConfiguration$LoadBalancerCaffeineWarnLogger - Spring Cloud LoadBalancer is currently working with the default cache. You can switch to using Caffeine cache, by adding it to the classpath.
20:54:12.597 [main] INFO  o.s.b.a.e.web.EndpointLinksResolver - Exposing 34 endpoint(s) beneath base path '/actuator'
20:54:12.767 [main] INFO  o.s.i.endpoint.EventDrivenConsumer - Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
20:54:12.768 [main] INFO  o.s.i.c.PublishSubscribeChannel - Channel 'address-service-1.errorChannel' has 1 subscriber(s).
20:54:12.768 [main] INFO  o.s.i.endpoint.EventDrivenConsumer - started bean '_org.springframework.integration.errorLogger'
20:54:12.814 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
20:54:12.853 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=address-service, profiles=[dev], label=null, version=null, state=null
20:54:12.854 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-classpath:/config/address-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-classpath:/config/address-service.properties'}]
20:54:13.068 [main] INFO  o.s.c.s.b.k.p.KafkaTopicProvisioner - Using kafka topic for outbound: addressChangeTopic
20:54:13.072 [main] INFO  o.a.k.c.admin.AdminClientConfig - AdminClientConfig values: 
        bootstrap.servers = [localhost:9092]
        client.dns.lookup = default
        client.id = 
        connections.max.idle.ms = 300000
        metadata.max.age.ms = 300000
        ...
        ...

20:54:13.115 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
20:54:13.115 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
20:54:13.115 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1698328453114
20:54:13.295 [main] INFO  o.a.k.c.producer.ProducerConfig - ProducerConfig values: 
        acks = 1
        batch.size = 16384
        bootstrap.servers = [localhost:9092]
        ...
        ...
20:54:13.311 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka version: 2.3.1
20:54:13.312 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka commitId: 18a913733fb71c01
20:54:13.312 [main] INFO  o.a.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1698328453311
20:54:13.319 [kafka-producer-network-thread | producer-1] INFO  org.apache.kafka.clients.Metadata - [Producer clientId=producer-1] Cluster ID: iVrWPxS6R6mdaOXXzESKKw
20:54:13.321 [main] INFO  o.a.k.clients.producer.KafkaProducer - [Producer clientId=producer-1] Closing the Kafka producer with timeoutMillis = 30000 ms.
20:54:13.332 [main] INFO  o.s.c.s.m.DirectWithAttributesChannel - Channel 'address-service-1.outboundAddressChanges' has 1 subscriber(s).
20:54:13.349 [main] INFO  o.s.c.n.eureka.InstanceInfoFactory - Setting initial instance status as: STARTING
...
...
20:54:13.836 [main] INFO  o.s.c.n.e.s.EurekaServiceRegistry - Registering application ADDRESS-SERVICE with eureka with status UP
20:54:13.891 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8082"]
20:54:13.905 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8082 (http) with context path ''
20:54:13.906 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8082
20:54:13.922 [main] INFO  o.j.e.c.a.AddressServiceApplication - Started AddressServiceApplication in 10.033 seconds (JVM running for 10.569)
CHECK SERVICES: addressRepository_org.springframework.data.jpa.repository.support.SimpleJpaRepository@46808854
CHECK Controller: addressController_org.jda.example.coursemanmsa.address.modules.AddressController@6a0ca728
```
</p>
</details>
	
#### API
**Any changes to `address` will be notified to `student-service` through `addressChangeTopic` Kafka topic**
+ Create a new address: `http://localhost:8072/address-service/v1/address/`
![image](https://user-images.githubusercontent.com/89120031/172946004-d9109f25-3f3c-46cf-a828-46572eedfb36.png)

+ Edit a address: `http://localhost:8072/address-service/v1/address/1`
![image](https://user-images.githubusercontent.com/89120031/172946079-d76089a2-d714-443e-b156-9fc75aea1aaa.png)

+ Get a address by Id: `http://localhost:8072/address-service/v1/address/1`
![image](https://user-images.githubusercontent.com/89120031/172951195-5931bc51-5271-437e-8b59-051a22207b70.png)

+ List all address: `http://localhost:8072/address-service/v1/address/`
![image](https://user-images.githubusercontent.com/89120031/172946905-6836067f-1822-4949-957b-c6d39e403ff2.png)

+ Delete a address: `http://localhost:8072/address-service/v1/address/2`
![image](https://user-images.githubusercontent.com/89120031/172947320-6c025bed-a0ea-40fb-81a7-e2feec6b109d.png)

### Service: StudentClass
- Description: This service manages all information about `class`, provides data for `student-service`
- Require a database schema named `class`.
- Run by commandline:
```
cd ../courseman/msa/modules/services/class-service
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.class.ClassServiceApplication`

- Output
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

16:14:12.976 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
16:14:13.745 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=class-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
16:14:13.746 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/class-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/class-service.properties'}]
16:14:13.782 [main] INFO  o.j.e.c.s.ClassServiceApplication - The following profiles are active: dev
...
...
...
16:14:16.560 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8082 (http)
...
...
...
16:14:36.822 [main] INFO  o.s.c.n.e.s.EurekaServiceRegistry - Registering application CLASS-SERVICE with eureka with status UP
16:14:36.857 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8082"]
16:14:36.912 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8082 (http) with context path ''
16:14:36.913 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8082
16:14:36.924 [main] INFO  o.j.e.c.s.ClassServiceApplication - Started ClassServiceApplication in 24.632 seconds (JVM running for 25.066)
```
</p>
</details>
	
#### API
**Any changes to `class` will be notified to `student-service` through `classChangeTopic` Kafka topic**
+ Create a new class: `http://localhost:8072/class-service/v1/class/`
![image](https://user-images.githubusercontent.com/89120031/172952921-18aaa826-5f37-44ef-9b87-62658870db18.png)

+ Edit a class: `http://localhost:8072/class-service/v1/class/1`
![image](https://user-images.githubusercontent.com/89120031/172953267-fde5ec87-8657-44c5-acdc-ad9dca73fe60.png)

+ Get a address by Id: `http://localhost:8072/class-service/v1/class/1`
![image](https://user-images.githubusercontent.com/89120031/172953374-b686e499-a8d3-46c9-8b19-b54e64d64912.png)

+ List all class: `http://localhost:8072/class-service/v1/class/`
![image](https://user-images.githubusercontent.com/89120031/172953433-7b955b79-c22f-4456-9b1b-38822a337d26.png)

+ Delete a class: `http://localhost:8072/class-service/v1/class/2`
![image](https://user-images.githubusercontent.com/89120031/172953483-65100eba-a3da-4238-8061-2d2f70a0dc86.png)

### Service: Course
- Description: This service manages all information about `course`, provides data for `enrolment-service`
- Requires the database schema named `course`.

- Run by commandline:
```
cd ../courseman/msa/modules/services/course-service
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.course.CourseServiceApplication`
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

16:20:17.759 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
16:20:19.093 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=course-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
16:20:19.094 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/course-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/course-service.properties'}]
16:20:19.277 [main] INFO  o.j.e.c.c.CourseServiceApplication - The following profiles are active: dev
...
...
...
16:20:32.531 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8083 (http)
16:20:32.588 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8083"]
16:20:32.589 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
16:20:32.590 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
16:20:34.144 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
...
...
16:20:48.267 [main] INFO  o.s.c.n.e.s.EurekaServiceRegistry - Registering application COURSE-SERVICE with eureka with status UP
16:20:48.314 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8083"]
16:20:48.328 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8083 (http) with context path ''
16:20:48.329 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8083
16:20:48.339 [main] INFO  o.j.e.c.c.CourseServiceApplication - Started CourseServiceApplication in 32.719 seconds (JVM running for 34.499)
```

- **Output**:
</p>
</details>

#### API
**Any changes to `course` will be notified to `enrolment-service` through `courseChangeTopic` Kafka topic**
+ Create a new course: `http://localhost:8072/course-service/v1/course/`
![image](https://user-images.githubusercontent.com/89120031/172954328-26328033-3d65-46eb-b7f6-9d798de210f9.png)

+ Edit a course: `http://localhost:8072/course-service/v1/course/1`
![image](https://user-images.githubusercontent.com/89120031/172955131-4d05acc9-85dd-482b-970b-0a8e0bc744a5.png)

+ Get a course by Id: `http://localhost:8072/course-service/v1/course/1`
![image](https://user-images.githubusercontent.com/89120031/172955207-014c6a29-e41c-4311-a517-4afec20334fd.png)

+ Delete a course: `http://localhost:8072/course-service/v1/course/2`
![image](https://user-images.githubusercontent.com/89120031/172955378-15748c14-f65d-4185-9a5a-7242be304354.png)

+ List all course: `http://localhost:8072/course-service/v1/course/`
![image](https://user-images.githubusercontent.com/89120031/172955296-adab8945-f16a-4b42-9fcd-e754c9e14c57.png)

+ List all course by type **compulsorymodule** or **electivemodule**: `http://localhost:8072/course-service/v1/course/compulsorymodule`
![image](https://user-images.githubusercontent.com/89120031/172958289-d2ad1700-b1d3-40ad-b26c-07e8c3f1d966.png)

+ List all **compulsorymodule** course: `http://localhost:8072/course-service/v1/course/compulsorymodules`
![image](https://user-images.githubusercontent.com/89120031/172958791-038c3047-6d74-472a-b861-f068402525c7.png)

+ List all **compulsorymodule** by Id: `http://localhost:8072/course-service/v1/course/compulsorymodule/4`
![image](https://user-images.githubusercontent.com/89120031/172958890-4b4a1df4-f274-4191-b9f0-21f742c0f54a.png)

+ List all **electivemodule** course: `http://localhost:8072/course-service/v1/course/electivemodules`
![image](https://user-images.githubusercontent.com/89120031/172958995-b83cb3d1-5435-47e9-88aa-64d0115ad746.png)

+ List all **electivemodule** by Id:`http://localhost:8072/course-service/v1/course/electivemodule/3`
![image](https://user-images.githubusercontent.com/89120031/172959036-273644a3-586c-4337-b6be-0c7d47d925b8.png)

### Service: Student
- Description: This service manages all information about `student`, provides data for `enrolment-service`
- Requires the database schema named `student`.
- Run by commandline:
```
cd ../courseman/msa/modules/services/student-service
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.student.StudentServiceApplication`

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

15:27:04.559 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
15:27:05.912 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=student-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
15:27:05.913 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/student-service.properties'}]
15:27:05.949 [main] INFO  o.j.e.c.s.StudentServiceApplication - The following profiles are active: dev
...
...
...
15:27:08.259 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8080 (http)
15:27:08.267 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8080"]
15:27:08.268 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
15:27:08.268 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
15:27:08.363 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
...
...
15:27:15.044 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
15:27:15.092 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http) with context path ''
15:27:15.093 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8080
15:27:15.105 [main] INFO  o.j.e.c.s.StudentServiceApplication - Started StudentServiceApplication in 11.353 seconds (JVM running for 11.847)
```
</p>
</details>

#### API
**Any changes to `student` will be notified to `enrolment-service*` and `academic-service` through `studentChangeTopic` Kafka topic**
+ Create a new student: `http://localhost:8072/student-service/v1/student/`
![image](https://user-images.githubusercontent.com/89120031/173244278-1d81a361-9a5b-4f17-828a-076ae1c88318.png)

+ Edit a student: `http://localhost:8072/student-service/v1/student/KT20`
![image](https://user-images.githubusercontent.com/89120031/173245588-e802a32f-409b-4b8b-a773-50e417acadf0.png)

+ Get a student by Id: `http://localhost:8072/student-service/v1/student/KT20`
![image](https://user-images.githubusercontent.com/89120031/173245846-d30af20a-e92c-43bc-86b1-0a1aa94e1dd6.png)

+ Delete a student: `http://localhost:8072/student-service/v1/student/0`
![image](https://user-images.githubusercontent.com/89120031/173245711-0ebc0b87-a6dc-48ea-badd-39c62b36cd57.png)

+ List all student: `http://localhost:8072/student-service/v1/student/`
![image](https://user-images.githubusercontent.com/89120031/173245691-4f09743e-ac36-466e-843c-44188274f0af.png)

### Service: Enrolment (1)
- Description: This service manages all information about `enrolment` (Students registers Coursemodules). In **usecase1**, we can update student marks driectly in the `enrolment-service`.
- Requires the database schema named `enrolment`. 

- Run by commandline
```
cd ../courseman/msa/modules/services/service1/enrolment-service1
mvn spring-boot:run
```
Or Run by class `org.jda.example.coursemanmsa.enrolment.EnrolmentServiceApplication`
- **Output**:
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

16:22:27.260 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
16:22:28.004 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=enrolment-service, profiles=[dev], label=null, version=2cd5b0c3f54de5db631958a8c947d99d5a9fbc49, state=null
16:22:28.005 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-configClient'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/enrolment-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-https://github.com/haworker25/microservice-configs.git/enrolment-service.properties'}]
16:22:28.039 [main] INFO  o.j.e.c.e.EnrolmentServiceApplication - The following profiles are active: dev
...
...
...
16:22:31.391 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8084 (http)
16:22:31.399 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8084"]
16:22:31.399 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
16:22:31.399 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
16:22:31.650 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
...
...
16:22:46.890 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8084"]
16:22:46.915 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8084 (http) with context path ''
16:22:46.915 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8084
16:22:46.926 [main] INFO  o.j.e.c.e.EnrolmentServiceApplication - Started EnrolmentServiceApplication in 21.446 seconds (JVM running for 23.104)

```
</p>
</details>

#### API
+ Create a new enrolment: `http://localhost:8072/enrolment-service1/v1/enrolment/`
![image](https://user-images.githubusercontent.com/89120031/173248067-09d76aa7-f6c1-48f1-9d27-0cac62c5aa69.png)

+ Edit a enrolment: `http://localhost:8072/enrolment-service1/v1/enrolment/1`
![image](https://user-images.githubusercontent.com/89120031/173248137-0a5d6ac2-e8e9-4a60-9ae7-bc9eccad5e87.png)

+ Get a enrolment by Id: `http://localhost:8072/enrolment-service1/v1/enrolment/1`
![image](https://user-images.githubusercontent.com/89120031/173248154-02e67061-02e2-47dd-a644-35f7973dbf47.png)

+ Get enrolments by coursemoduleId: `http://localhost:8072/enrolment-service1/v1/enrolment/coursemodule/4`
![image](https://user-images.githubusercontent.com/89120031/173248249-b8208973-a591-48e0-a5d8-76cc67c1ee51.png)

+ Delete a enrolment: `http://localhost:8072/enrolment-service1/v1/enrolment/0`
![image](https://user-images.githubusercontent.com/89120031/173248192-b7435223-3d4a-48ec-b289-492539027930.png)

+ List all enrolment: `http://localhost:8072/enrolment-service1/v1/enrolment/`
![image](https://user-images.githubusercontent.com/89120031/173248173-503a5fd5-4691-4464-8af7-e64ebb1b0aca.png)

### Service: Enrolment (2)
- Description: This service manages all information about `enrolment` (Students registers Coursemodules). In usecase2, we can't update student marks driectly in the `enrolment-service2`. When an enrolment will create/update/delete, it will create/update/delete in the `academic-service`
- Run by commandline
```
cd ../courseman/msa/modules/services/service2/enrolment-service2
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.academic.EnrolmentServiceApplication`

- **Output**:
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

12:06:00.907 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
12:06:02.083 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=enrolment-service2, profiles=[dev], label=null, version=null, state=null
12:06:02.084 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-classpath:/config/enrolment-service2-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-classpath:/config/enrolment-service2.properties'}]
12:06:02.127 [main] INFO  o.j.e.c.e.EnrolmentServiceApplication - The following profiles are active: dev
...
...
...
12:06:04.840 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8086 (http)
12:06:04.849 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8086"]
12:06:04.850 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
12:06:04.850 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
12:06:04.998 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
12:06:15.586 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8086 (http) with context path ''
12:06:15.587 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8086
12:06:16.588 [main] INFO  o.s.cloud.commons.util.InetUtils - Cannot determine local hostname
12:06:16.590 [main] INFO  o.j.e.c.e.EnrolmentServiceApplication - Started EnrolmentServiceApplication in 18.484 seconds (JVM running for 19.154)
```
</p>
</details>

#### API
**Any changes to `enrolment` will be notified to `academic-service` through `enrolmentChangeTopic` Kafka topic**
- Functions:
+ Create a new enrolment: `http://localhost:8072/enrolment-service2/v1/enrolment/`
![image](https://user-images.githubusercontent.com/89120031/174207757-fab374b3-b063-4aab-83a4-e7a0826b9fbb.png)

+ Edit a enrolment: `http://localhost:8072/enrolment-service2/v1/enrolment/1`
![image](https://user-images.githubusercontent.com/89120031/174205834-c2dd9efa-c605-40f9-9cf5-d7fff4f8e082.png)

+ Get a enrolment by Id: `http://localhost:8072/enrolment-service2/v1/enrolment/1`
![image](https://user-images.githubusercontent.com/89120031/174206834-e20a3201-ea26-4c43-85e9-a482a4469c2a.png)

+ Get enrolments by coursemoduleId: `http://localhost:8072/enrolment-service2/v1/enrolment/coursemodule/4`
![image](https://user-images.githubusercontent.com/89120031/174206884-ea158f19-6474-4795-ae23-59e12f3a99d8.png)

+ Delete a enrolment: `http://localhost:8072/enrolment-service2/v1/enrolment/1`
![image](https://user-images.githubusercontent.com/89120031/174207115-562da6ac-4874-493a-81e8-661c419bda6e.png)

+ List all enrolment: `http://localhost:8072/enrolment-service2/v1/enrolment/`
![image](https://user-images.githubusercontent.com/89120031/174207032-67f47272-9679-4839-a98b-b404f4e491f9.png)

### Service: Academic
- Description: This service manages all information about `academic` (Students registers Coursemodules and their marks). In usecase2, we only update student marks directly in the `academic-service`.
- Requires the database schema named `academic`.

- Run by commandline
```
cd ../courseman/msa/modules/services/service2/academic-service2
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.academic.AcademicServiceApplication`
- **Output**:
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

12:22:21.304 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Fetching config from server at : http://localhost:8071
12:22:22.458 [main] INFO  o.s.c.c.c.ConfigServicePropertySourceLocator - Located environment: name=academic-service, profiles=[dev], label=null, version=null, state=null
12:22:22.459 [main] INFO  o.s.c.b.c.PropertySourceBootstrapConfiguration - Located property source: [BootstrapPropertySource {name='bootstrapProperties-classpath:/config/academic-service-dev.properties'}, BootstrapPropertySource {name='bootstrapProperties-classpath:/config/academic-service.properties'}]
12:22:22.506 [main] INFO  o.j.e.c.a.AcademicServiceApplication - The following profiles are active: dev
...
...
...
12:22:24.913 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8085 (http)
12:22:24.922 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8085"]
12:22:24.923 [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
12:22:24.923 [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.33]
12:22:25.015 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
...
...
...
12:22:35.706 [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8085"]
16:31:57.499 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8085 (http) with context path ''
16:31:57.500 [main] INFO  o.s.c.n.e.s.EurekaAutoServiceRegistration - Updating port to 8085
16:31:57.518 [main] INFO  o.j.e.c.a.AcademicServiceApplication - Started AcademicServiceApplication in 18.257 seconds (JVM running for 19.827)
```
</p>
</details>

#### API
+ List all academic: `http://localhost:8072/academic-service/academic/`
![image](https://user-images.githubusercontent.com/89120031/174212734-7a1e9af8-b82d-4411-9a44-30164cfc22ac.png)

+ Get a academic by Id: `http://localhost:8072/academic-service/academic/4`
![image](https://user-images.githubusercontent.com/89120031/174212813-cc1f5fa1-4d6d-4c15-bd42-43b47eef2ab1.png)
	
+ Get academic by coursemoduleId: `http://localhost:8072/academic-service/academic/coursemodule/1`
![image](https://user-images.githubusercontent.com/89120031/174214544-245b3b17-c11d-4780-a8d2-53ed9a6ef413.png)

+ Edit a academic: `http://localhost:8072/academic-service/academic/4` (Only for editing mark)
![image](https://user-images.githubusercontent.com/89120031/174214664-e901cca7-fa2b-4435-a017-f32ea3882620.png)