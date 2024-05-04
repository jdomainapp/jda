--
-- PostgreSQL database dump
--

-- Dumped from database version 14.9 (Ubuntu 14.9-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.9 (Ubuntu 14.9-0ubuntu0.22.04.1)

-- Started on 2023-10-26 17:29:01 +07

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
-- TOC entry 8 (class 2615 OID 16511)
-- Name: address; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA address;


ALTER SCHEMA address OWNER TO admin;

--
-- TOC entry 6 (class 2615 OID 16388)
-- Name: assessmenthub; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA assessmenthub;


ALTER SCHEMA assessmenthub OWNER TO admin;

--
-- TOC entry 9 (class 2615 OID 33363)
-- Name: course; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA course;


ALTER SCHEMA course OWNER TO admin;

--
-- TOC entry 4 (class 2615 OID 16440)
-- Name: coursemgnt; Type: SCHEMA; Schema: -; Owner: admin
--

CREATE SCHEMA coursemgnt;


ALTER SCHEMA coursemgnt OWNER TO admin;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 228 (class 1259 OID 16513)
-- Name: address; Type: TABLE; Schema: address; Owner: admin
--

CREATE TABLE address.address (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE address.address OWNER TO admin;

--
-- TOC entry 227 (class 1259 OID 16512)
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
-- TOC entry 3545 (class 0 OID 0)
-- Dependencies: 227
-- Name: address_id_seq; Type: SEQUENCE OWNED BY; Schema: address; Owner: admin
--

ALTER SEQUENCE address.address_id_seq OWNED BY address.address.id;


--
-- TOC entry 217 (class 1259 OID 16412)
-- Name: coursemodule; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer,
    coursemoduletype character varying(30),
    deptname character varying(30),
    teacher_id integer
);


ALTER TABLE assessmenthub.coursemodule OWNER TO admin;

--
-- TOC entry 216 (class 1259 OID 16411)
-- Name: coursemodule_id_seq; Type: SEQUENCE; Schema: assessmenthub; Owner: admin
--

CREATE SEQUENCE assessmenthub.coursemodule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE assessmenthub.coursemodule_id_seq OWNER TO admin;

--
-- TOC entry 3546 (class 0 OID 0)
-- Dependencies: 216
-- Name: coursemodule_id_seq; Type: SEQUENCE OWNED BY; Schema: assessmenthub; Owner: admin
--

ALTER SEQUENCE assessmenthub.coursemodule_id_seq OWNED BY assessmenthub.coursemodule.id;


--
-- TOC entry 219 (class 1259 OID 16424)
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
-- TOC entry 218 (class 1259 OID 16423)
-- Name: enrolment_id_seq; Type: SEQUENCE; Schema: assessmenthub; Owner: admin
--

CREATE SEQUENCE assessmenthub.enrolment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE assessmenthub.enrolment_id_seq OWNER TO admin;

--
-- TOC entry 3547 (class 0 OID 0)
-- Dependencies: 218
-- Name: enrolment_id_seq; Type: SEQUENCE OWNED BY; Schema: assessmenthub; Owner: admin
--

ALTER SEQUENCE assessmenthub.enrolment_id_seq OWNED BY assessmenthub.enrolment.id;


--
-- TOC entry 213 (class 1259 OID 16399)
-- Name: student; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.student (
    id character varying(6) NOT NULL,
    name character varying(30),
    gender_name character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    studentclass_id integer,
    deptname character varying(30)
);


ALTER TABLE assessmenthub.student OWNER TO admin;

--
-- TOC entry 215 (class 1259 OID 16405)
-- Name: teacher; Type: TABLE; Schema: assessmenthub; Owner: admin
--

CREATE TABLE assessmenthub.teacher (
    id integer NOT NULL,
    name character varying(30),
    gender_name character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    deptname character varying(30)
);


ALTER TABLE assessmenthub.teacher OWNER TO admin;

--
-- TOC entry 214 (class 1259 OID 16404)
-- Name: teacher_id_seq; Type: SEQUENCE; Schema: assessmenthub; Owner: admin
--

CREATE SEQUENCE assessmenthub.teacher_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE assessmenthub.teacher_id_seq OWNER TO admin;

--
-- TOC entry 3548 (class 0 OID 0)
-- Dependencies: 214
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: assessmenthub; Owner: admin
--

ALTER SEQUENCE assessmenthub.teacher_id_seq OWNED BY assessmenthub.teacher.id;


--
-- TOC entry 241 (class 1259 OID 33371)
-- Name: compulsorymodule; Type: TABLE; Schema: course; Owner: admin
--

CREATE TABLE course.compulsorymodule (
    id integer NOT NULL
);


ALTER TABLE course.compulsorymodule OWNER TO admin;

--
-- TOC entry 240 (class 1259 OID 33365)
-- Name: coursemodule; Type: TABLE; Schema: course; Owner: admin
--

CREATE TABLE course.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer
);


ALTER TABLE course.coursemodule OWNER TO admin;

--
-- TOC entry 239 (class 1259 OID 33364)
-- Name: coursemodule_id_seq; Type: SEQUENCE; Schema: course; Owner: admin
--

CREATE SEQUENCE course.coursemodule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE course.coursemodule_id_seq OWNER TO admin;

--
-- TOC entry 3549 (class 0 OID 0)
-- Dependencies: 239
-- Name: coursemodule_id_seq; Type: SEQUENCE OWNED BY; Schema: course; Owner: admin
--

ALTER SEQUENCE course.coursemodule_id_seq OWNED BY course.coursemodule.id;


--
-- TOC entry 242 (class 1259 OID 33381)
-- Name: electivemodule; Type: TABLE; Schema: course; Owner: admin
--

CREATE TABLE course.electivemodule (
    id integer NOT NULL,
    deptname character varying(50)
);


ALTER TABLE course.electivemodule OWNER TO admin;

--
-- TOC entry 224 (class 1259 OID 16483)
-- Name: coursemodule; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.coursemodule (
    id integer NOT NULL,
    code character varying(12),
    name character varying(30),
    semester integer,
    credits integer,
    coursemoduletype character varying(30),
    deptname character varying(30),
    teacher_id integer
);


ALTER TABLE coursemgnt.coursemodule OWNER TO admin;

--
-- TOC entry 223 (class 1259 OID 16482)
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
-- TOC entry 3550 (class 0 OID 0)
-- Dependencies: 223
-- Name: coursemodule_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.coursemodule_id_seq OWNED BY coursemgnt.coursemodule.id;


--
-- TOC entry 226 (class 1259 OID 16495)
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
-- TOC entry 225 (class 1259 OID 16494)
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
-- TOC entry 3551 (class 0 OID 0)
-- Dependencies: 225
-- Name: enrolment_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.enrolment_id_seq OWNED BY coursemgnt.enrolment.id;


--
-- TOC entry 220 (class 1259 OID 16441)
-- Name: student; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.student (
    id character varying(6) NOT NULL,
    name character varying(30),
    gender_name character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    studentclass_id integer,
    deptname character varying(30)
);


ALTER TABLE coursemgnt.student OWNER TO admin;

--
-- TOC entry 222 (class 1259 OID 16447)
-- Name: teacher; Type: TABLE; Schema: coursemgnt; Owner: admin
--

CREATE TABLE coursemgnt.teacher (
    id integer NOT NULL,
    name character varying(30),
    gender_name character varying(10),
    dob date,
    address_id integer,
    email character varying(30),
    deptname character varying(30)
);


ALTER TABLE coursemgnt.teacher OWNER TO admin;

--
-- TOC entry 221 (class 1259 OID 16446)
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
-- TOC entry 3552 (class 0 OID 0)
-- Dependencies: 221
-- Name: teacher_id_seq; Type: SEQUENCE OWNED BY; Schema: coursemgnt; Owner: admin
--

ALTER SEQUENCE coursemgnt.teacher_id_seq OWNED BY coursemgnt.teacher.id;


--
-- TOC entry 236 (class 1259 OID 16836)
-- Name: authorisation; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.authorisation (
    id integer NOT NULL,
    student_id integer,
    authordetails character varying(255),
    description character varying(255),
    status_name character varying(20)
);


ALTER TABLE public.authorisation OWNER TO admin;

--
-- TOC entry 231 (class 1259 OID 16809)
-- Name: coursemodule; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.coursemodule (
    id integer NOT NULL,
    code character varying(6),
    name character varying(30),
    semester integer,
    credits integer
);


ALTER TABLE public.coursemodule OWNER TO admin;

--
-- TOC entry 230 (class 1259 OID 16804)
-- Name: enrolment; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.enrolment (
    id integer NOT NULL,
    student_id integer,
    module_id integer,
    internalmark double precision,
    exammark double precision,
    finalgrade character(1)
);


ALTER TABLE public.enrolment OWNER TO admin;

--
-- TOC entry 238 (class 1259 OID 16850)
-- Name: enrolmentclosure; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.enrolmentclosure (
    id integer NOT NULL,
    closuredate date,
    sclassregist_id integer,
    orient_id integer,
    note character varying(255)
);


ALTER TABLE public.enrolmentclosure OWNER TO admin;

--
-- TOC entry 234 (class 1259 OID 16824)
-- Name: helprequest; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.helprequest (
    id integer NOT NULL,
    student_id integer,
    content character varying(255)
);


ALTER TABLE public.helprequest OWNER TO admin;

--
-- TOC entry 237 (class 1259 OID 16843)
-- Name: orientation; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.orientation (
    id integer NOT NULL,
    content character varying(10000)
);


ALTER TABLE public.orientation OWNER TO admin;

--
-- TOC entry 235 (class 1259 OID 16829)
-- Name: payment; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.payment (
    id integer NOT NULL,
    student_id integer,
    paydetails character varying(255),
    description character varying(255),
    status_name character varying(20)
);


ALTER TABLE public.payment OWNER TO admin;

--
-- TOC entry 233 (class 1259 OID 16819)
-- Name: sclass; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.sclass (
    id integer NOT NULL,
    name character varying(20)
);


ALTER TABLE public.sclass OWNER TO admin;

--
-- TOC entry 232 (class 1259 OID 16814)
-- Name: sclassregistration; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.sclassregistration (
    id integer NOT NULL,
    student_id integer,
    sclass_id integer
);


ALTER TABLE public.sclassregistration OWNER TO admin;

--
-- TOC entry 229 (class 1259 OID 16799)
-- Name: student; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.student (
    id integer NOT NULL,
    name character varying(30)
);


ALTER TABLE public.student OWNER TO admin;

--
-- TOC entry 3308 (class 2604 OID 16516)
-- Name: address id; Type: DEFAULT; Schema: address; Owner: admin
--

ALTER TABLE ONLY address.address ALTER COLUMN id SET DEFAULT nextval('address.address_id_seq'::regclass);


--
-- TOC entry 3303 (class 2604 OID 16415)
-- Name: coursemodule id; Type: DEFAULT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.coursemodule ALTER COLUMN id SET DEFAULT nextval('assessmenthub.coursemodule_id_seq'::regclass);


--
-- TOC entry 3304 (class 2604 OID 16427)
-- Name: enrolment id; Type: DEFAULT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment ALTER COLUMN id SET DEFAULT nextval('assessmenthub.enrolment_id_seq'::regclass);


--
-- TOC entry 3302 (class 2604 OID 16408)
-- Name: teacher id; Type: DEFAULT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.teacher ALTER COLUMN id SET DEFAULT nextval('assessmenthub.teacher_id_seq'::regclass);


--
-- TOC entry 3309 (class 2604 OID 33368)
-- Name: coursemodule id; Type: DEFAULT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.coursemodule ALTER COLUMN id SET DEFAULT nextval('course.coursemodule_id_seq'::regclass);


--
-- TOC entry 3306 (class 2604 OID 16486)
-- Name: coursemodule id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule ALTER COLUMN id SET DEFAULT nextval('coursemgnt.coursemodule_id_seq'::regclass);


--
-- TOC entry 3307 (class 2604 OID 16498)
-- Name: enrolment id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment ALTER COLUMN id SET DEFAULT nextval('coursemgnt.enrolment_id_seq'::regclass);


--
-- TOC entry 3305 (class 2604 OID 16450)
-- Name: teacher id; Type: DEFAULT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.teacher ALTER COLUMN id SET DEFAULT nextval('coursemgnt.teacher_id_seq'::regclass);


--
-- TOC entry 3525 (class 0 OID 16513)
-- Dependencies: 228
-- Data for Name: address; Type: TABLE DATA; Schema: address; Owner: admin
--

INSERT INTO address.address VALUES (2, 'HCM');
INSERT INTO address.address VALUES (3, 'Danang');
INSERT INTO address.address VALUES (4, 'Hue');
INSERT INTO address.address VALUES (1, 'Hanoi2');


--
-- TOC entry 3514 (class 0 OID 16412)
-- Dependencies: 217
-- Data for Name: coursemodule; Type: TABLE DATA; Schema: assessmenthub; Owner: admin
--



--
-- TOC entry 3516 (class 0 OID 16424)
-- Dependencies: 219
-- Data for Name: enrolment; Type: TABLE DATA; Schema: assessmenthub; Owner: admin
--



--
-- TOC entry 3510 (class 0 OID 16399)
-- Dependencies: 213
-- Data for Name: student; Type: TABLE DATA; Schema: assessmenthub; Owner: admin
--

INSERT INTO assessmenthub.student VALUES ('2', 'john smith', 'M', '1987-02-02', 2, 'john@gmail.com', 1, 'FIT');
INSERT INTO assessmenthub.student VALUES ('1', 'duc', 'M', '1995-01-01', 1, 'duc@gmail.com', 1, 'FIT');
INSERT INTO assessmenthub.student VALUES ('5', 'van toan', 'M', '1999-12-31', 10, 'van@gmail.com', 1, 'MED');


--
-- TOC entry 3512 (class 0 OID 16405)
-- Dependencies: 215
-- Data for Name: teacher; Type: TABLE DATA; Schema: assessmenthub; Owner: admin
--



--
-- TOC entry 3538 (class 0 OID 33371)
-- Dependencies: 241
-- Data for Name: compulsorymodule; Type: TABLE DATA; Schema: course; Owner: admin
--



--
-- TOC entry 3537 (class 0 OID 33365)
-- Dependencies: 240
-- Data for Name: coursemodule; Type: TABLE DATA; Schema: course; Owner: admin
--

INSERT INTO course.coursemodule VALUES (1, 'M-101', 'IPG', 1, 5);
INSERT INTO course.coursemodule VALUES (2, 'M-102', 'IPG', 1, 5);


--
-- TOC entry 3539 (class 0 OID 33381)
-- Dependencies: 242
-- Data for Name: electivemodule; Type: TABLE DATA; Schema: course; Owner: admin
--



--
-- TOC entry 3521 (class 0 OID 16483)
-- Dependencies: 224
-- Data for Name: coursemodule; Type: TABLE DATA; Schema: coursemgnt; Owner: admin
--

INSERT INTO coursemgnt.coursemodule VALUES (1, 'M101', 'PR1', 1, 5, 'compulsory', 'FIT', 1);
INSERT INTO coursemgnt.coursemodule VALUES (2, 'M201', 'PR2', 2, 5, 'compulsory', 'FIT', 2);


--
-- TOC entry 3523 (class 0 OID 16495)
-- Dependencies: 226
-- Data for Name: enrolment; Type: TABLE DATA; Schema: coursemgnt; Owner: admin
--

INSERT INTO coursemgnt.enrolment VALUES (1, '5', 1, 0, 0, NULL);
INSERT INTO coursemgnt.enrolment VALUES (2, '5', 2, 0, 0, NULL);


--
-- TOC entry 3517 (class 0 OID 16441)
-- Dependencies: 220
-- Data for Name: student; Type: TABLE DATA; Schema: coursemgnt; Owner: admin
--

INSERT INTO coursemgnt.student VALUES ('5', 'van toan', 'M', '2000-01-01', 10, 'van@gmail.com', 1, 'MED');


--
-- TOC entry 3519 (class 0 OID 16447)
-- Dependencies: 222
-- Data for Name: teacher; Type: TABLE DATA; Schema: coursemgnt; Owner: admin
--

INSERT INTO coursemgnt.teacher VALUES (1, 'Alex Mark', 'M', '1980-09-09', 1, 'alex@gmail.com', 'FIT');
INSERT INTO coursemgnt.teacher VALUES (2, 'Duc Le', 'M', '1977-01-01', 2, 'duc@gmail.com', 'FIT');


--
-- TOC entry 3533 (class 0 OID 16836)
-- Dependencies: 236
-- Data for Name: authorisation; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.authorisation VALUES (1, 4, 'Permission granted', 'Enrolment authorisation for Student(4,Nguyen Van E)', 'ACCEPTED');


--
-- TOC entry 3528 (class 0 OID 16809)
-- Dependencies: 231
-- Data for Name: coursemodule; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.coursemodule VALUES (1, 'M100', 'Programming 1', 1, 5);
INSERT INTO public.coursemodule VALUES (2, 'M200', 'Programming 2', 2, 5);
INSERT INTO public.coursemodule VALUES (3, 'M300', 'Programming 3', 3, 5);


--
-- TOC entry 3527 (class 0 OID 16804)
-- Dependencies: 230
-- Data for Name: enrolment; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.enrolment VALUES (1, 1, 1, NULL, NULL, '-');
INSERT INTO public.enrolment VALUES (2, 2, 1, NULL, NULL, '-');
INSERT INTO public.enrolment VALUES (3, 3, 3, NULL, NULL, '-');
INSERT INTO public.enrolment VALUES (4, 4, 2, NULL, NULL, '-');
INSERT INTO public.enrolment VALUES (5, 5, 1, NULL, NULL, '-');


--
-- TOC entry 3535 (class 0 OID 16850)
-- Dependencies: 238
-- Data for Name: enrolmentclosure; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- TOC entry 3531 (class 0 OID 16824)
-- Dependencies: 234
-- Data for Name: helprequest; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.helprequest VALUES (1, 3, 'What are the prequisites of Programming 3?');


--
-- TOC entry 3534 (class 0 OID 16843)
-- Dependencies: 237
-- Data for Name: orientation; Type: TABLE DATA; Schema: public; Owner: admin
--



--
-- TOC entry 3532 (class 0 OID 16829)
-- Dependencies: 235
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.payment VALUES (1, 4, 'Bank: Bank of Vietnam
Account no: 123456789', 'Enrolment payment for Student(4,Nguyen Van E)', 'ACCEPTED');


--
-- TOC entry 3530 (class 0 OID 16819)
-- Dependencies: 233
-- Data for Name: sclass; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.sclass VALUES (1, 'Class 1');
INSERT INTO public.sclass VALUES (2, 'Class 2');


--
-- TOC entry 3529 (class 0 OID 16814)
-- Dependencies: 232
-- Data for Name: sclassregistration; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.sclassregistration VALUES (1, 2, 1);


--
-- TOC entry 3526 (class 0 OID 16799)
-- Dependencies: 229
-- Data for Name: student; Type: TABLE DATA; Schema: public; Owner: admin
--

INSERT INTO public.student VALUES (1, 'Nguyen Van Anh');
INSERT INTO public.student VALUES (2, 'Nguyen Van A');
INSERT INTO public.student VALUES (3, 'Nguyen Van B');
INSERT INTO public.student VALUES (4, 'Nguyen Van E');
INSERT INTO public.student VALUES (5, 'x');


--
-- TOC entry 3553 (class 0 OID 0)
-- Dependencies: 227
-- Name: address_id_seq; Type: SEQUENCE SET; Schema: address; Owner: admin
--

SELECT pg_catalog.setval('address.address_id_seq', 4, true);


--
-- TOC entry 3554 (class 0 OID 0)
-- Dependencies: 216
-- Name: coursemodule_id_seq; Type: SEQUENCE SET; Schema: assessmenthub; Owner: admin
--

SELECT pg_catalog.setval('assessmenthub.coursemodule_id_seq', 1, false);


--
-- TOC entry 3555 (class 0 OID 0)
-- Dependencies: 218
-- Name: enrolment_id_seq; Type: SEQUENCE SET; Schema: assessmenthub; Owner: admin
--

SELECT pg_catalog.setval('assessmenthub.enrolment_id_seq', 1, false);


--
-- TOC entry 3556 (class 0 OID 0)
-- Dependencies: 214
-- Name: teacher_id_seq; Type: SEQUENCE SET; Schema: assessmenthub; Owner: admin
--

SELECT pg_catalog.setval('assessmenthub.teacher_id_seq', 1, false);


--
-- TOC entry 3557 (class 0 OID 0)
-- Dependencies: 239
-- Name: coursemodule_id_seq; Type: SEQUENCE SET; Schema: course; Owner: admin
--

SELECT pg_catalog.setval('course.coursemodule_id_seq', 2, true);


--
-- TOC entry 3558 (class 0 OID 0)
-- Dependencies: 223
-- Name: coursemodule_id_seq; Type: SEQUENCE SET; Schema: coursemgnt; Owner: admin
--

SELECT pg_catalog.setval('coursemgnt.coursemodule_id_seq', 2, true);


--
-- TOC entry 3559 (class 0 OID 0)
-- Dependencies: 225
-- Name: enrolment_id_seq; Type: SEQUENCE SET; Schema: coursemgnt; Owner: admin
--

SELECT pg_catalog.setval('coursemgnt.enrolment_id_seq', 2, true);


--
-- TOC entry 3560 (class 0 OID 0)
-- Dependencies: 221
-- Name: teacher_id_seq; Type: SEQUENCE SET; Schema: coursemgnt; Owner: admin
--

SELECT pg_catalog.setval('coursemgnt.teacher_id_seq', 2, true);


--
-- TOC entry 3327 (class 2606 OID 16518)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: address; Owner: admin
--

ALTER TABLE ONLY address.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (id);


--
-- TOC entry 3315 (class 2606 OID 16417)
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- TOC entry 3317 (class 2606 OID 16429)
-- Name: enrolment enrolment_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT enrolment_pkey PRIMARY KEY (id);


--
-- TOC entry 3311 (class 2606 OID 16403)
-- Name: student student_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);


--
-- TOC entry 3313 (class 2606 OID 16410)
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- TOC entry 3351 (class 2606 OID 33375)
-- Name: compulsorymodule course_compulsorymodulepk; Type: CONSTRAINT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulepk PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 33385)
-- Name: electivemodule course_electivemodulepk; Type: CONSTRAINT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.electivemodule
    ADD CONSTRAINT course_electivemodulepk PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 33370)
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- TOC entry 3323 (class 2606 OID 16488)
-- Name: coursemodule coursemodule_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule
    ADD CONSTRAINT coursemodule_pkey PRIMARY KEY (id);


--
-- TOC entry 3325 (class 2606 OID 16500)
-- Name: enrolment enrolment_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT enrolment_pkey PRIMARY KEY (id);


--
-- TOC entry 3319 (class 2606 OID 16445)
-- Name: student student_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id);


--
-- TOC entry 3321 (class 2606 OID 16452)
-- Name: teacher teacher_pkey; Type: CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.teacher
    ADD CONSTRAINT teacher_pkey PRIMARY KEY (id);


--
-- TOC entry 3343 (class 2606 OID 16842)
-- Name: authorisation authorisationpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.authorisation
    ADD CONSTRAINT authorisationpk PRIMARY KEY (id);


--
-- TOC entry 3333 (class 2606 OID 16813)
-- Name: coursemodule coursemodulepk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.coursemodule
    ADD CONSTRAINT coursemodulepk PRIMARY KEY (id);


--
-- TOC entry 3347 (class 2606 OID 16854)
-- Name: enrolmentclosure enrolmentclosurepk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolmentclosure
    ADD CONSTRAINT enrolmentclosurepk PRIMARY KEY (id);


--
-- TOC entry 3331 (class 2606 OID 16808)
-- Name: enrolment enrolmentpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolment
    ADD CONSTRAINT enrolmentpk PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 16828)
-- Name: helprequest helprequestpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.helprequest
    ADD CONSTRAINT helprequestpk PRIMARY KEY (id);


--
-- TOC entry 3345 (class 2606 OID 16849)
-- Name: orientation orientationpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.orientation
    ADD CONSTRAINT orientationpk PRIMARY KEY (id);


--
-- TOC entry 3341 (class 2606 OID 16835)
-- Name: payment paymentpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT paymentpk PRIMARY KEY (id);


--
-- TOC entry 3337 (class 2606 OID 16823)
-- Name: sclass sclasspk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sclass
    ADD CONSTRAINT sclasspk PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 16818)
-- Name: sclassregistration sclassregistrationpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sclassregistration
    ADD CONSTRAINT sclassregistrationpk PRIMARY KEY (id);


--
-- TOC entry 3329 (class 2606 OID 16803)
-- Name: student studentpk; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.student
    ADD CONSTRAINT studentpk PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 16430)
-- Name: enrolment coursemodule_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id) REFERENCES assessmenthub.coursemodule(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3356 (class 2606 OID 16435)
-- Name: enrolment student_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.enrolment
    ADD CONSTRAINT student_fk FOREIGN KEY (student_id) REFERENCES assessmenthub.student(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3354 (class 2606 OID 16418)
-- Name: coursemodule teacher_fk; Type: FK CONSTRAINT; Schema: assessmenthub; Owner: admin
--

ALTER TABLE ONLY assessmenthub.coursemodule
    ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id) REFERENCES assessmenthub.teacher(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3369 (class 2606 OID 33376)
-- Name: compulsorymodule course_compulsorymodulefk1; Type: FK CONSTRAINT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.compulsorymodule
    ADD CONSTRAINT course_compulsorymodulefk1 FOREIGN KEY (id) REFERENCES course.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3370 (class 2606 OID 33386)
-- Name: electivemodule course_electivemodulefk1; Type: FK CONSTRAINT; Schema: course; Owner: admin
--

ALTER TABLE ONLY course.electivemodule
    ADD CONSTRAINT course_electivemodulefk1 FOREIGN KEY (id) REFERENCES course.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3358 (class 2606 OID 16501)
-- Name: enrolment coursemodule_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT coursemodule_fk FOREIGN KEY (coursemodule_id) REFERENCES coursemgnt.coursemodule(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3359 (class 2606 OID 16506)
-- Name: enrolment student_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.enrolment
    ADD CONSTRAINT student_fk FOREIGN KEY (student_id) REFERENCES coursemgnt.student(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3357 (class 2606 OID 16489)
-- Name: coursemodule teacher_fk; Type: FK CONSTRAINT; Schema: coursemgnt; Owner: admin
--

ALTER TABLE ONLY coursemgnt.coursemodule
    ADD CONSTRAINT teacher_fk FOREIGN KEY (teacher_id) REFERENCES coursemgnt.teacher(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3366 (class 2606 OID 16885)
-- Name: authorisation authorisationfk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.authorisation
    ADD CONSTRAINT authorisationfk1 FOREIGN KEY (student_id) REFERENCES public.student(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3367 (class 2606 OID 16890)
-- Name: enrolmentclosure enrolmentclosurefk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolmentclosure
    ADD CONSTRAINT enrolmentclosurefk1 FOREIGN KEY (sclassregist_id) REFERENCES public.sclassregistration(id) ON UPDATE RESTRICT;


--
-- TOC entry 3368 (class 2606 OID 16895)
-- Name: enrolmentclosure enrolmentclosurefk2; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolmentclosure
    ADD CONSTRAINT enrolmentclosurefk2 FOREIGN KEY (orient_id) REFERENCES public.orientation(id) ON UPDATE RESTRICT;


--
-- TOC entry 3360 (class 2606 OID 16855)
-- Name: enrolment enrolmentfk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolment
    ADD CONSTRAINT enrolmentfk1 FOREIGN KEY (student_id) REFERENCES public.student(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3361 (class 2606 OID 16860)
-- Name: enrolment enrolmentfk2; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.enrolment
    ADD CONSTRAINT enrolmentfk2 FOREIGN KEY (module_id) REFERENCES public.coursemodule(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3364 (class 2606 OID 16875)
-- Name: helprequest helprequestfk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.helprequest
    ADD CONSTRAINT helprequestfk1 FOREIGN KEY (student_id) REFERENCES public.student(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3365 (class 2606 OID 16880)
-- Name: payment paymentfk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT paymentfk1 FOREIGN KEY (student_id) REFERENCES public.student(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3362 (class 2606 OID 16865)
-- Name: sclassregistration sclassregistrationfk1; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sclassregistration
    ADD CONSTRAINT sclassregistrationfk1 FOREIGN KEY (student_id) REFERENCES public.student(id) ON UPDATE RESTRICT ON DELETE CASCADE;


--
-- TOC entry 3363 (class 2606 OID 16870)
-- Name: sclassregistration sclassregistrationfk2; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.sclassregistration
    ADD CONSTRAINT sclassregistrationfk2 FOREIGN KEY (sclass_id) REFERENCES public.sclass(id) ON UPDATE RESTRICT ON DELETE CASCADE;


-- Completed on 2023-10-26 17:29:01 +07

--
-- PostgreSQL database dump complete
--

