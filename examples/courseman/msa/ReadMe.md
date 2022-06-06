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
| Academic Administration | Enrolment | uses Student, Course Module
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

## 2. Run common services in order

### Run Config service
Run by commandline
```
cd ../courseman/msa/modules/configserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.configserver.ConfigurationServerApplication`

### Run Discovery Service
Run by commandline
```
cd ../courseman/msa/modules/eurekaserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.eurekaserver.EurekaServerApplication`

### Run Gateway Service
Run by commandline
```
cd ../courseman/msa/modules/gatewayserver
mvn spring-boot:run
```
or Run by class `org.jda.example.coursemanmsa.gatewayserver.ApiGatewayServerApplication`

## 3. Run business services
### Setup
- Each service create a postgresql database with user/password: admin/password

### Run Student Service
#### Create database
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
#### Run 
By commandline
```
cd ../courseman/msa/modules/service1/student-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.student.StudentServiceApplication`

### Run Address Service
#### Create database
```
CREATE TABLE IF NOT EXISTS address.address
(
    id SERIAL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT address_pkey PRIMARY KEY (id)
)
```
#### Run 
By commandline
```
cd ../courseman/msa/modules/service1/address-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.address.AddressServiceApplication`

### Run Class Service
#### Create database
```
CREATE TABLE IF NOT EXISTS class.studentclass
(
    id SERIAL,
    name character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT studentclass_pkey PRIMARY KEY (id)
)
```
#### Run 
By commandline
```
cd ../courseman/msa/modules/service1/class-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.class.ClassServiceApplication`

### Run Course Service
#### Create database
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
#### Run 
By commandline
```
cd ../courseman/msa/modules/service1/course-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.course.CourseServiceApplication`


## Usecase1
### Run Academic Service
#### Create database
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
#### Run 
By commandline
```
cd ../courseman/msa/modules/service1/academic-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.academic.AcademicServiceApplication`

## Usecase2
### Run Enrolment Service
#### Create database
Use `enrolment` database of usecase1
#### Run 
By commandline
```
cd ../courseman/msa/modules/service2/enrolment-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.academic.EnrolmentServiceApplication`

### Run Academic Service
#### Create database
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
#### Run 
By commandline
```
cd ../courseman/msa/modules/service2/academic-service
mvn spring-boot:run
```
By class `org.jda.example.coursemanmsa.academic.AcademicServiceApplication`
