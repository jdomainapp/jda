package com.coursemanmsa.servicescourseman.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "enrolment")
public class Erolment implements Serializable {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idenrolment")
    private Integer idenrolment;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idstudent")
//    private Student student;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idcourse")
//    private Course course;
    @Column(name = "idstudent")
    private Integer idstudent;
    @Column(name = "idcourse")
    private Integer idcourse;
    @Column(name = "internalmark")
    private Float internalmark;
    @Column(name = "finalmark")
    private Float finalmark;
    @Column(name = "result",length = 12)
    private String result;

    public Erolment() {super();
    }

    public Erolment(Integer idenrolment, Integer idstudent, Integer idcourse, Float internalmark, Float finalmark, String result) {
        this.idenrolment = idenrolment;
        this.idstudent = idstudent;
        this.idcourse = idcourse;
        this.internalmark = internalmark;
        this.finalmark = finalmark;
        this.result = result;
    }

    public Integer getIdenrolment() {
        return idenrolment;
    }

    public void setIdenrolment(Integer idenrolment) {
        this.idenrolment = idenrolment;
    }

    public Integer getIdstudent() {
        return idstudent;
    }

    public void setIdstudent(Integer idstudent) {
        this.idstudent = idstudent;
    }

    public Integer getIdcourse() {
        return idcourse;
    }

    public void setIdcourse(Integer idcourse) {
        this.idcourse = idcourse;
    }

    public Float getInternalmark() {
        return internalmark;
    }

    public void setInternalmark(Float internalmark) {
        this.internalmark = internalmark;
    }

    public Float getFinalmark() {
        return finalmark;
    }

    public void setFinalmark(Float finalmark) {
        this.finalmark = finalmark;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
