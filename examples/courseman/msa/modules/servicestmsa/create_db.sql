--
-- PostgreSQL database dump
--

-- Dumped from database version 10.23 (Ubuntu 10.23-0ubuntu0.18.04.2)
-- Dumped by pg_dump version 10.23 (Ubuntu 10.23-0ubuntu0.18.04.2)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: domainds; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE domainds WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


ALTER DATABASE domainds OWNER TO postgres;

\connect domainds

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: address; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA address;


ALTER SCHEMA address OWNER TO admin;

--
-- Name: assessmenthub; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA assessmenthub;


ALTER SCHEMA assessmenthub OWNER TO admin;

--
-- Name: class; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA class;


ALTER SCHEMA class OWNER TO admin;

--
-- Name: coursemgnt; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA coursemgnt;


ALTER SCHEMA coursemgnt OWNER TO admin;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: address; Type: TABLE; Schema: address; Owner: admin
--

CREATE TABLE address.address (
    id integer NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE address.address OWNER TO admin;

--
-- Name: address_id_seq; Type: SEQUENCE; Schema: address; Owner: admin
--

CREATE SEQUENCE address.address_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE address.address_id_seq OWNER TO admin;

--
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: address; Owner: admin
--

ALTER SEQUENCE address.address_id_seq OWNED BY address.address.id;


--
-- Name: address; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.address (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE assessmenthub.address OWNER TO admin;

--
-- Name: compulsorymodule; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.compulsorymodule (
    id integer NOT NULL
);


ALTER TABLE assessmenthub.compulsorymodule OWNER TO admin;

--
-- Name: coursemodule; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer,
    teacher_id integer
);


ALTER TABLE assessmenthub.coursemodule OWNER TO admin;

--
-- Name: electivemodule; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.electivemodule (
    id integer NOT NULL,
    deptname character varying(50)
);


ALTER TABLE assessmenthub.electivemodule OWNER TO admin;

--
-- Name: enrolment; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.enrolment (
    id integer NOT NULL,
    student_id character varying(6),
    coursemodule_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1)
);


ALTER TABLE assessmenthub.enrolment OWNER TO admin;

--
-- Name: student; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.student (
    id character varying(6) NOT NULL,
    name character varying(30),
    gender character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    class_id integer,
    deptname character varying(30)
);


ALTER TABLE assessmenthub.student OWNER TO admin;

--
-- Name: studentclass; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.studentclass (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE assessmenthub.studentclass OWNER TO admin;

--
-- Name: teacher; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.teacher (
    id integer NOT NULL,
    name character varying(30),
    gender character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    deptname character varying(30)
);


ALTER TABLE assessmenthub.teacher OWNER TO admin;

--
-- Name: studentclass; Type: TABLE; Schema: class; Owner: admin
--

CREATE TABLE class.studentclass (
    id integer NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE class.studentclass OWNER TO admin;

--
-- Name: studentclass_id_seq; Type: SEQUENCE; Schema: class; Owner: admin
--

CREATE SEQUENCE class.studentclass_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE class.studentclass_id_seq OWNER TO admin;

--
-- Name: studentclass_id_seq; Type: SEQUENCE OWNED BY; Schema: class; Owner: admin
--

ALTER SEQUENCE class.studentclass_id_seq OWNED BY class.studentclass.id;


--
-- Name: address; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.address (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE coursemgnt.address OWNER TO admin;

--
-- Name: compulsorymodule; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.compulsorymodule (
    id integer NOT NULL
);


ALTER TABLE coursemgnt.compulsorymodule OWNER TO admin;

--
-- Name: coursemodule; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer,
    teacher_id integer
);


ALTER TABLE coursemgnt.coursemodule OWNER TO admin;

--
-- Name: coursemodule_id_seq; Type: SEQUENCE; Schema: coursemgnt; Owner: admin
--

CREATE SEQUENCE coursemgnt.coursemodule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coursemgnt.coursemodule_id_seq OWNER TO admin;

--
-- Name: coursemodule_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.coursemodule_id_seq OWNED BY coursemgnt.coursemodule.id;


--
-- Name: electivemodule; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.electivemodule (
    id integer NOT NULL,
    deptname character varying(50)
);


ALTER TABLE coursemgnt.electivemodule OWNER TO admin;

--
-- Name: enrolment; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.enrolment (
    id integer NOT NULL,
    student_id character varying(6),
    coursemodule_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1)
);


ALTER TABLE coursemgnt.enrolment OWNER TO admin;

--
-- Name: enrolment_id_seq; Type: SEQUENCE; Schema: coursemgnt; Owner: admin
--

CREATE SEQUENCE coursemgnt.enrolment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coursemgnt.enrolment_id_seq OWNER TO admin;

--
-- Name: enrolment_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.enrolment_id_seq OWNED BY coursemgnt.enrolment.id;


--
-- Name: student; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.student (
    id character varying(6) NOT NULL,
    name character varying(30),
    gender character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    class_id integer,
    deptname character varying(30)
);


ALTER TABLE coursemgnt.student OWNER TO admin;

--
-- Name: studentclass; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.studentclass (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE coursemgnt.studentclass OWNER TO admin;

--
-- Name: teacher; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.teacher (
    id integer NOT NULL,
    name character varying(30),
    gender character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    deptname character varying(30)
);


ALTER TABLE coursemgnt.teacher OWNER TO admin;

--
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: coursemgnt; Owner: admin
--

CREATE SEQUENCE coursemgnt.teacher_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coursemgnt.teacher_id_seq OWNER TO admin;

--
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.teacher_id_seq OWNED BY coursemgnt.teacher.id;


--
-- Name: address id; Type: DEFAULT; Schema: address; Owner: admin
--

ALTER TABLE ONLY address.address ALTER COLUMN id SET DEFAULT nextval('address.address_id_seq'::regclass);


--
-- Name: studentclass id; Type: DEFAULT; Schema: class; Owner: admin
--

ALTER TABLE ONLY class.studentclass ALTER COLUMN id SET DEFAULT nextval('class.studentclass_id_seq'::regclass);


--
-- Name: coursemodule id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule ALTER COLUMN id SET DEFAULT nextval('coursemgnt.coursemodule_id_seq'::regclass);


--
-- Name: enrolment id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment ALTER COLUMN id SET DEFAULT nextval('coursemgnt.enrolment_id_seq'::regclass);


--
-- Name: teacher id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.teacher ALTER COLUMN id SET DEFAULT nextval('coursemgnt.teacher_id_seq'::regclass);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: address; Owner: admin
--

ALTER TABLE ONLY address.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: studentclass class_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.studentclass
    ADD CONSTRAINT class_pkey PRIMARY KEY (id);


--
-- Name: compulsorymodule course_compulsorymodulepk; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulepk PRIMARY KEY (id);


--
-- Name: electivemodule course_electivemodulepk; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.electivemodule
    ADD CONSTRAINT course_electivemodulepk PRIMARY KEY (id);


--
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- Name: enrolment enrolment_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT enrolment_pkey PRIMARY KEY (id);


--
-- Name: student student_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);


--
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- Name: studentclass studentclass_pkey; Type: CONSTRAINT; Schema: class; Owner: admin
--

ALTER TABLE ONLY class.studentclass
    ADD CONSTRAINT studentclass_pkey PRIMARY KEY (id);


--
-- Name: address address_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- Name: studentclass class_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.studentclass
    ADD CONSTRAINT class_pkey PRIMARY KEY (id);


--
-- Name: compulsorymodule course_compulsorymodulepk; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulepk PRIMARY KEY (id);


--
-- Name: electivemodule course_electivemodulepk; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.electivemodule
    ADD CONSTRAINT course_electivemodulepk PRIMARY KEY (id);


--
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- Name: enrolment enrolment_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT enrolment_pkey PRIMARY KEY (id);


--
-- Name: student student_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);


--
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- Name: compulsorymodule course_compulsorymodulefk1; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulefk1 FOREIGN KEY (id) REFERENCES assessmenthub.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- Name: electivemodule course_electivemodulefk1; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.electivemodule
    ADD CONSTRAINT course_electivemodulefk1 FOREIGN KEY (id) REFERENCES assessmenthub.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- Name: enrolment coursemodule_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id) REFERENCES assessmenthub.coursemodule(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: student student_address_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.student
    ADD CONSTRAINT student_address_fk FOREIGN KEY (address_id) REFERENCES assessmenthub.address(id) NOT VALID;


--
-- Name: student student_class_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.student
    ADD CONSTRAINT student_class_fk FOREIGN KEY (class_id) REFERENCES assessmenthub.studentclass(id) NOT VALID;


--
-- Name: enrolment student_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT student_fk FOREIGN KEY (student_id) REFERENCES assessmenthub.student(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: teacher teacher_address_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.teacher
    ADD CONSTRAINT teacher_address_fk FOREIGN KEY (address_id) REFERENCES assessmenthub.address(id) NOT VALID;


--
-- Name: coursemodule teacher_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.coursemodule
    ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id) REFERENCES assessmenthub.teacher(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: compulsorymodule course_compulsorymodulefk1; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulefk1 FOREIGN KEY (id) REFERENCES coursemgnt.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- Name: electivemodule course_electivemodulefk1; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.electivemodule
    ADD CONSTRAINT course_electivemodulefk1 FOREIGN KEY (id) REFERENCES coursemgnt.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- Name: enrolment coursemodule_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id) REFERENCES assessmenthub.coursemodule(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: student student_address_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.student
    ADD CONSTRAINT student_address_fk FOREIGN KEY (address_id) REFERENCES coursemgnt.address(id) NOT VALID;


--
-- Name: student student_class_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.student
    ADD CONSTRAINT student_class_fk FOREIGN KEY (class_id) REFERENCES coursemgnt.studentclass(id) NOT VALID;


--
-- Name: enrolment student_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT student_fk FOREIGN KEY (student_id) REFERENCES assessmenthub.student(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: teacher teacher_address_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.teacher
    ADD CONSTRAINT teacher_address_fk FOREIGN KEY (address_id) REFERENCES coursemgnt.address(id) NOT VALID;


--
-- Name: coursemodule teacher_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule
    ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id) REFERENCES assessmenthub.teacher(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

