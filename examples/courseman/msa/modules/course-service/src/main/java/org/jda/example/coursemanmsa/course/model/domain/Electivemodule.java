package org.jda.example.coursemanmsa.course.model.domain;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
@Entity
@Table(name = "electivemodule")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = Electivemodule.class)
public class Electivemodule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String deptname;

	//bi-directional one-to-one association to Coursemodule
	@OneToOne 
	@JoinColumn(name="id")
	@MapsId
	private CourseModule coursemodule;
}