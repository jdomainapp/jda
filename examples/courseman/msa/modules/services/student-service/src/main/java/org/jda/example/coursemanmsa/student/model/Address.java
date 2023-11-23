package org.jda.example.coursemanmsa.student.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "address", schema="student")
public class Address {
    @Id
    @Column(name = "id", nullable = false)
    int id;

    @Column(name = "name", nullable = false)
    String name;

}
