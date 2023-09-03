package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.teacher.model.Teacher;

import javax.persistence.*;

@Getter @Setter @ToString
@Entity
@Table(name = "coursemodule")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = CompulsoryModule.class, name = "compulsory"),
	@JsonSubTypes.Type(value = ElectiveModule.class, name = "elective")})
public class CourseModule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;
	
	private String type;

	@ManyToOne
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

}
