package org.jda.example.coursemanmsa.academic.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Student{

	private String id;
	private String studentName;
	private String studentGender;
	private Date studentDob;
	private int addressId;
	private String studentEmail;
	private int studentclassId;
	private String addressName;
	private String studentClassName;
}
