--
-- PostgreSQL database dump
--

-- Dumped from database version 10.22 (Ubuntu 10.22-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.22 (Ubuntu 10.22-0ubuntu0.18.04.1)

-- Started on 2022-10-14 13:56:44 PDT

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
-- TOC entry 14 (class 2615 OID 32865)
-- Name: coursemodulemgnt; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA IF NOT EXISTS coursemodulemgnt;


ALTER SCHEMA coursemodulemgnt OWNER TO admin;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 235 (class 1259 OID 32868)
-- Name: coursemodule; Type: TABLE; Schema: coursemodulemgnt; Owner: admin
--

CREATE TABLE IF NOT EXISTS coursemodulemgnt.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer,
    coursemoduletype character varying(30),
    deptname character varying(30),
    teacher_id integer
);


ALTER TABLE coursemodulemgnt.coursemodule OWNER TO admin;

--
-- TOC entry 234 (class 1259 OID 32866)
-- Name: coursemodule_id_seq; Type: SEQUENCE; Schema: coursemodulemgnt; Owner: admin
--

CREATE SEQUENCE IF NOT EXISTS coursemodulemgnt.coursemodule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coursemodulemgnt.coursemodule_id_seq OWNER TO admin;

--
-- TOC entry 3012 (class 0 OID 0)
-- Dependencies: 234
-- Name: coursemodule_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemodulemgnt; Owner: admin
--

ALTER SEQUENCE coursemodulemgnt.coursemodule_id_seq OWNED BY coursemodulemgnt.coursemodule.id;


--
-- TOC entry 237 (class 1259 OID 32881)
-- Name: teacher; Type: TABLE; Schema: coursemodulemgnt; Owner: admin
--

CREATE TABLE IF NOT EXISTS coursemodulemgnt.teacher (
    id integer NOT NULL,
    name character varying(30),
    gender_name character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    deptname character varying(30)
);


ALTER TABLE coursemodulemgnt.teacher OWNER TO admin;

--
-- TOC entry 236 (class 1259 OID 32879)
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: coursemodulemgnt; Owner: admin
--

CREATE SEQUENCE IF NOT EXISTS coursemodulemgnt.teacher_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coursemodulemgnt.teacher_id_seq OWNER TO admin;

--
-- TOC entry 3013 (class 0 OID 0)
-- Dependencies: 236
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemodulemgnt; Owner: admin
--

ALTER SEQUENCE coursemodulemgnt.teacher_id_seq OWNED BY coursemodulemgnt.teacher.id;


--
-- TOC entry 2879 (class 2604 OID 32871)
-- Name: coursemodule id; Type: DEFAULT; Schema: coursemodulemgnt; Owner: admin
--

ALTER TABLE ONLY coursemodulemgnt.coursemodule ALTER COLUMN id SET DEFAULT nextval('coursemodulemgnt.coursemodule_id_seq'::regclass);


--
-- TOC entry 2880 (class 2604 OID 32884)
-- Name: teacher id; Type: DEFAULT; Schema: coursemodulemgnt; Owner: admin
--

ALTER TABLE ONLY coursemodulemgnt.teacher ALTER COLUMN id SET DEFAULT nextval('coursemodulemgnt.teacher_id_seq'::regclass);


--
-- TOC entry 2882 (class 2606 OID 32873)
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: coursemodulemgnt; Owner: admin
--

ALTER TABLE ONLY coursemodulemgnt.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- TOC entry 2884 (class 2606 OID 32886)
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: coursemodulemgnt; Owner: admin
--

ALTER TABLE ONLY coursemodulemgnt.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- TOC entry 2885 (class 2606 OID 32874)
-- Name: coursemodule teacher_fk; Type: FK CONSTRAINT; Schema: coursemodulemgnt; Owner: admin
--

ALTER TABLE ONLY coursemodulemgnt.coursemodule
    ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id) REFERENCES coursemgnt.teacher(id) ON UPDATE CASCADE ON DELETE CASCADE;


-- Completed on 2022-10-14 13:56:44 PDT

--
-- PostgreSQL database dump complete
--

