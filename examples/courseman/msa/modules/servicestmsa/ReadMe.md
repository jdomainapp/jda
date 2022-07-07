Example: `jda-eg-coursemanmsa-servicestmsa`
===============================


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
# Create topic: "streams-courseman-coursemodules"
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic courseChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic studentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic addressChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic classChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic enrolmentChangeTopic --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic teacherChangeTopic --bootstrap-server localhost:9092
```
# Run ConfigServer

# Run EurekaServer

# Run GatewayServer

# Create databases

- Each service create a postgresql database `domainds` with user/password: admin/password
- Create schema `assessmenthub` have 4 tables:
```
CREATE TABLE IF NOT EXISTS assessmenthub.coursemodule
(
    id integer NOT NULL DEFAULT nextval('assessmenthub.coursemodule_id_seq'::regclass),
    code character varying(12) COLLATE pg_catalog."default",
    name character varying(30) COLLATE pg_catalog."default",
    semester integer,
    credits integer,
    coursemoduletype character varying(30) COLLATE pg_catalog."default",
    deptname character varying(30) COLLATE pg_catalog."default",
    teacher_id integer,
    CONSTRAINT coursemodule_pkey PRIMARY KEY (id),
    CONSTRAINT teacher_fk FOREIGN KEY (teacher_id)
        REFERENCES assessmenthub.teacher (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
);

CREATE TABLE IF NOT EXISTS assessmenthub.enrolment
(
    id integer NOT NULL DEFAULT nextval('assessmenthub.enrolment_id_seq'::regclass),
    student_id character varying(6) COLLATE pg_catalog."default",
    coursemodule_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1) COLLATE pg_catalog."default",
    CONSTRAINT enrolment_pkey PRIMARY KEY (id),
    CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id)
        REFERENCES assessmenthub.coursemodule (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT student_fk FOREIGN KEY (student_id)
        REFERENCES assessmenthub.student (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
);

CREATE TABLE IF NOT EXISTS assessmenthub.student
(
    id character varying(6) COLLATE pg_catalog."default" NOT NULL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    studentclass_id integer,
    deptname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS assessmenthub.teacher
(
    id integer SERIAL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    deptname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT teacher_pkey PRIMARY KEY (id)
);
```
- Create schema `coursemgnt` have 4 tables:
```
CREATE TABLE IF NOT EXISTS coursemgnt.coursemodule
(
    id integer NOT NULL DEFAULT nextval('assessmenthub.coursemodule_id_seq'::regclass),
    code character varying(12) COLLATE pg_catalog."default",
    name character varying(30) COLLATE pg_catalog."default",
    semester integer,
    credits integer,
    coursemoduletype character varying(30) COLLATE pg_catalog."default",
    deptname character varying(30) COLLATE pg_catalog."default",
    teacher_id integer,
    CONSTRAINT coursemodule_pkey PRIMARY KEY (id),
    CONSTRAINT teacher_fk FOREIGN KEY (teacher_id)
        REFERENCES assessmenthub.teacher (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
);

CREATE TABLE IF NOT EXISTS coursemgnt.enrolment
(
    id integer NOT NULL DEFAULT nextval('assessmenthub.enrolment_id_seq'::regclass),
    student_id character varying(6) COLLATE pg_catalog."default",
    coursemodule_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1) COLLATE pg_catalog."default",
    CONSTRAINT enrolment_pkey PRIMARY KEY (id),
    CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id)
        REFERENCES assessmenthub.coursemodule (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT student_fk FOREIGN KEY (student_id)
        REFERENCES assessmenthub.student (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID
);

CREATE TABLE IF NOT EXISTS coursemgnt.student
(
    id character varying(6) COLLATE pg_catalog."default" NOT NULL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    studentclass_id integer,
    deptname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT student_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS coursemgnt.teacher
(
    id integer SERIAL,
    name character varying(30) COLLATE pg_catalog."default",
    gender_name character varying(10) COLLATE pg_catalog."default",
    dob date,
    address_id integer,
    email character varying(30) COLLATE pg_catalog."default",
    deptname character varying(30) COLLATE pg_catalog."default",
    CONSTRAINT teacher_pkey PRIMARY KEY (id)
);
```

# Run academicadmin-service
# Run assessmenthub-service
# Run coursemgnt-service

# Link patterns 
`http://localhost:8072/academicadmin-service/assessmenthub/[student/teacher/enrolment/coursemodule]/{id}`
`http://localhost:8072/assessmenthub-service/[student/teacher/enrolment/coursemodule]/{id}`

`http://localhost:8072/academicadmin-service/coursemgnt/studentenrolment/[student/enrolment]/{id}`
`http://localhost:8072/academicadmin-service/coursemgnt/coursemodulemgnt/[student/coursemodule]/{id}`
`http://localhost:8072/coursemgnt-service/studentenrolment/[student/enrolment]/{id}`
`http://localhost:8072/coursemgnt-service/coursemodulemgnt/[student/coursemodule]/{id}`



















