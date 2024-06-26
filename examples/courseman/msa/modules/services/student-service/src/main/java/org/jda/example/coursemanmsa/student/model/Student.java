package org.jda.example.coursemanmsa.student.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name="student", schema="student")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Student extends RepresentationModel<Student> {

	@Id
	@Column(name = "id", nullable = false)
	private String id;
	@Column(name = "name", nullable = false)
	private String studentName;
	@Column(name = "gender_name")
	private String studentGender;
	@Column(name = "dob")
	
	private Date studentDob;
	@Column(name = "address_id", nullable = false)
	private int addressId;
	@Column(name="email")
	private String studentEmail;
	@Column(name = "studentclass_id", nullable = false)
	private int studentclassId;
	
	@Transient
	private String addressName;
	
	@Transient
	private String studentClassName;

}