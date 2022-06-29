//Serialisable = true
package org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name="enrolment")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = Enrolment.class)
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
	
	@ManyToOne
    @JoinColumn(name="coursemodule_id", nullable=false)
	private Coursemodule coursemodule;
	
	@ManyToOne
    @JoinColumn(name="student_id", nullable=false)
	private Student student;

}