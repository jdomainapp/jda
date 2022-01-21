Example: `jda-eg-coursemankafka`
===============================

Usage instructions.

## 1. Start Apache Kafka and register topics
URL: https://kafka.apache.org/quickstart

```
# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
bin/zookeeper-server-start.sh config/zookeeper.properties
```

```
# Start the Kafka broker service
bin/kafka-server-start.sh config/server.properties
```

```
# create topic: "streams-courseman-coursemodules"
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic streams-courseman-coursemodules --bootstrap-server localhost:9092
```

## 2. Run Producer service

Run class: `org.jda.example.coursemankafka.services.coursemodule2.CourseModuleService2.java` 

Console output (sample):

```
 .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)
11:55:38.271 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Starting CourseModuleService2 using Java 13.0.2 on voyager with PID 245111 (/data/projects/jda/examples/courseman/kafka/target/classes started by ducmle in /data/projects/jda/examples/courseman/kafka)
...
11:55:41.321 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Started CourseModuleService2 in 3.403 seconds (JVM running for 3.864)
11:55:41.323 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - (SpringBoot) CommandLineRunner.run: is executing...
11:55:41.324 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - startStreaming()...
11:55:41.326 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - createOutputStream()...
11:55:41.351 [main] INFO  o.a.k.c.producer.ProducerConfig - ProducerConfig values: 
...
o.a.k.c.p.i.TransactionManager - [Producer clientId=producer-transaction_streams-courseman-coursemodules, transactionalId=transaction_streams-courseman-coursemodules] Discovered transaction coordinator localhost:9092 (id: 0 rack: null)
11:55:41.872 [kafka-producer-network-thread | producer-transaction_streams-courseman-coursemodules] INFO  o.a.k.c.p.i.TransactionManager - [Producer clientId=producer-transaction_streams-courseman-coursemodules, transactionalId=transaction_streams-courseman-coursemodules] ProducerId set to 0 with epoch 10
11:55:44.874 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Producer: CompulsoryModule(M800,Module:7430)
11:55:47.958 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Producer: CompulsoryModule(M200,Module:3757)
11:55:50.962 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Producer: CompulsoryModule(M801,Module:5714)
11:55:53.967 [main] INFO  o.j.e.c.s.c.CourseModuleService2 - Producer: CompulsoryModule(M100,Module:7753)
... 

```

## 3. Run Consumer service
Run class: `org.jda.example.coursemankafka.services.coursemodule2.CourseModuleService2.java` 

Console output (sample):

```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.4.2)

11:57:26.495 [background-preinit] INFO  o.h.validator.internal.util.Version - HV000001: Hibernate Validator 6.1.7.Final
11:57:26.528 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - Starting StudentRegistService2 using Java 13.0.2 on voyager with PID 245197 (/data/projects/jda/examples/courseman/kafka/target/classes started by ducmle in /data/projects/jda/examples/courseman/kafka)
...
11:57:29.347 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - Started StudentRegistService2 in 3.352 seconds (JVM running for 4.005)
11:57:29.350 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - SpringBoot.CommandLineRunner.run()...
11:57:29.350 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - startStreaming()...
11:57:29.382 [main] INFO  o.a.k.c.consumer.ConsumerConfig - ConsumerConfig values: 
...
*** received : CompulsoryModule(M804,Module:252)
11:57:30.143 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - doService()...
11:57:30.143 [main] INFO  o.j.e.c.s.s.StudentRegistService2 -    StudentRegist(Student:5698 CompulsoryModule(M804,Module:252))

*** received : CompulsoryModule(M800,Module:7430)
11:57:30.144 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - doService()...
11:57:30.144 [main] INFO  o.j.e.c.s.s.StudentRegistService2 -    StudentRegist(Student:7328 CompulsoryModule(M800,Module:7430))

*** received : CompulsoryModule(M200,Module:3757)
11:57:30.144 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - doService()...
11:57:30.144 [main] INFO  o.j.e.c.s.s.StudentRegistService2 -    StudentRegist(Student:24 CompulsoryModule(M200,Module:3757))

*** received : CompulsoryModule(M801,Module:5714)
11:57:30.144 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - doService()...
11:57:30.145 [main] INFO  o.j.e.c.s.s.StudentRegistService2 -    StudentRegist(Student:9773 CompulsoryModule(M801,Module:5714))

*** received : CompulsoryModule(M100,Module:7753)
11:57:30.145 [main] INFO  o.j.e.c.s.s.StudentRegistService2 - doService()...
11:57:30.145 [main] INFO  o.j.e.c.s.s.StudentRegistService2 -    StudentRegist(Student:5215 CompulsoryModule(M100,Module:7753))
...

```

## (Optional) Other Apache Kafka commands

```
# write to the topic
bin/kafka-console-producer.sh --topic streams-courseman-coursemodules --bootstrap-server localhost:9092
```

```
# read from the topic
bin/kafka-console-consumer.sh --topic streams-courseman-coursemodules --from-beginning --bootstrap-server localhost:9092
```

```
# view topic info
bin/kafka-topics.sh --describe --topic streams-courseman-coursemodules --bootstrap-server localhost:9092
```

## References
1. [Kafka quickstart](https://kafka.apache.org/quickstart): commands to run Kafka
2. Kafka data serialisation: 
	- [Custom serialiser](https://kafka.apache.org/10/documentation/streams/developer-guide/datatypes#implementing-custom-serdes)
	- [Custom serialiser (2)](https://www.baeldung.com/kafka-custom-serializer)
3. [Kafka Connects](https://kafka.apache.org/documentation/#connect)
4. ksqlDB (Kafka & SQL): 
	- [API](https://www.baeldung.com/ksqldb)
	- [with microservice](https://docs.ksqldb.io/en/latest/tutorials/event-driven-microservice/)