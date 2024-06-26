package org.jda.example.coursemanmsa.academic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name="academic")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Academic extends RepresentationModel<Academic> {

	@Id
	@Column(name = "id", nullable = false)
	private int id;
	@Column(name = "student_id", nullable = false)
	private String studentId;
	@Column(name = "coursemodule_id")
	private int coursemoduleId;
	@Column(name = "internalmark")
	private double internalmark;
	@Column(name="exammark")
	private double exammark;
	@Column(name = "finalgrade")
	private String finalgrade;
	
	@Transient
	private Coursemodule coursemodule;
	
	@Transient
	private Student student;

}