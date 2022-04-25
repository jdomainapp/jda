package com.coursemanmsa.servicescourseman.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name = "student")
public class Student implements Serializable {
    @Id
    //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idstudent")
    private Integer idstudent;
    @Column(name = "namestudent",length = 30)
    private String namestudent;
    @Column(name = "dobstudent",length = 12)
    private String dobstudent;
    @Column(name = "address",length = 50)
    private String address;
    @Column(name = "email",length = 50)
    private String email;
    @Column(name = "idsclass")
    private Integer idsclass;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "idsclass")
//    private Sclass sclass;

    public Student() {
        super();
    }

    public Student(Integer idstudent, String namestudent, String dobstudent, String address, String email, Integer idsclass) {
        this.idstudent = idstudent;
        this.namestudent = namestudent;
        this.dobstudent = dobstudent;
        this.address = address;
        this.email = email;
        this.idsclass = idsclass;
    }

    public Integer getIdstudent() {
        return idstudent;
    }

    public void setIdstudent(Integer idstudent) {
        this.idstudent = idstudent;
    }

    public String getNamestudent() {
        return namestudent;
    }

    public void setNamestudent(String namestudent) {
        this.namestudent = namestudent;
    }

    public String getDobstudent() {
        return dobstudent;
    }

    public void setDobstudent(String dobstudent) {
        this.dobstudent = dobstudent;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIdsclass() {
        return idsclass;
    }

    public void setIdsclass(Integer idsclass) {
        this.idsclass = idsclass;
    }

}
