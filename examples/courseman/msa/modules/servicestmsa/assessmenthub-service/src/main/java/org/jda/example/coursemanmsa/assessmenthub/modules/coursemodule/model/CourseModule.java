//Serialisable = true
package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "coursemodule")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = CourseModule.class)
public class CourseModule {

	@Id
	@Column(name = "id", nullable = false)
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;
	
	@PrimaryKeyJoinColumn
	@OneToOne(mappedBy="coursemodule",cascade = CascadeType.ALL)
	private CompulsoryModule compulsorymodule;

	@PrimaryKeyJoinColumn
	@OneToOne(mappedBy="coursemodule",cascade = CascadeType.ALL)
	private ElectiveModule electivemodule;
	
	@ManyToOne
    @JoinColumn(name="teacher_id", nullable=false)
	private Teacher teacher;
	
}