package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
		  property = "id", scope = ElectiveModule.class)
public class ElectiveModule {
	
	@Id
	private Integer id;

	private String deptname;

	@OneToOne 
	@JoinColumn(name="id")
	@MapsId
	private CourseModule coursemodule;

}
