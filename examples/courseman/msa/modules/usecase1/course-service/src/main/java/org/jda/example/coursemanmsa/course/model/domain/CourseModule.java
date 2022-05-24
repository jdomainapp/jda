package org.jda.example.coursemanmsa.course.model.domain;

import javax.persistence.*;

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;

	//bi-directional one-to-one association to Compulsorymodule
	@PrimaryKeyJoinColumn
	@OneToOne(mappedBy="coursemodule",cascade = CascadeType.ALL)
	private Compulsorymodule compulsorymodule;

	//bi-directional one-to-one association to Electivemodule
	@OneToOne(mappedBy="coursemodule",cascade = CascadeType.ALL)
	private Electivemodule electivemodule;

}