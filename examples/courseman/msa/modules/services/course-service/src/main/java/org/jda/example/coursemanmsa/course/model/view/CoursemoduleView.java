package org.jda.example.coursemanmsa.course.model.view;

import javax.persistence.*;

import org.jda.example.coursemanmsa.course.model.domain.CourseModule;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
public class CoursemoduleView {

	@Id
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;
	
	private String coursemoduletype;
	
	private String deptname;

	public CoursemoduleView(CourseModule coursemodule) {
		super();
		this.id = coursemodule.getId();
		this.code = coursemodule.getCode();
		this.credits = coursemodule.getCredits();
		this.name = coursemodule.getName();
		this.semester = coursemodule.getSemester();
	}
	
	

}