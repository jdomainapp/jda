//Serialisable = true
package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name="enrolment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enrolment extends RepresentationModel<Enrolment> {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
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