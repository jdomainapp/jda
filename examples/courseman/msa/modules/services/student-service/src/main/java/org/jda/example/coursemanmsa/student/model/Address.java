package org.jda.example.coursemanmsa.student.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Address extends RepresentationModel<Address> {

	String id;
    String name;
}
