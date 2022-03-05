package com.coursemanmsa.servicescourseman.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "course")
public class Course implements Serializable {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idcourse")
    private Integer idcourse;
    @Column(name = "namecourse",length = 50)
    private String namecourse;
    @Column(name = "semaster")
    private Integer semaster;
    @Column(name = "credits")
    private Integer credits;

    public Course() {
        super();

    }

    public Course(Integer idcourse, String namecourse, Integer semaster, Integer credits) {
        this.idcourse = idcourse;
        this.namecourse = namecourse;
        this.semaster = semaster;
        this.credits = credits;
    }

    public Integer getIdcourse() {
        return idcourse;
    }

    public void setIdcourse(Integer idcourse) {
        this.idcourse = idcourse;
    }

    public String getNamecourse() {
        return namecourse;
    }

    public void setNamecourse(String namecourse) {
        this.namecourse = namecourse;
    }

    public Integer getSemaster() {
        return semaster;
    }

    public void setSemaster(Integer semaster) {
        this.semaster = semaster;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }
}
