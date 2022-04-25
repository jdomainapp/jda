package com.coursemanmsa.servicescourseman.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "sclass")
public class Sclass{
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsclass")
    private Integer idsclass;
    @Column(name = "namesclass",length = 50)
    private String namesclass;

    public Sclass() {
        super();
    }

    public Sclass(Integer idsclass, String namesclass) {
        this.idsclass = idsclass;
        this.namesclass = namesclass;
    }

    public int getIdsclass() {
        return idsclass;
    }

    public void setIdsclass(Integer idsclass) {
        this.idsclass = idsclass;
    }

    public String getNamesclass() {
        return namesclass;
    }

    public void setNamesclass(String namesclass) {
        this.namesclass = namesclass;
    }

}
