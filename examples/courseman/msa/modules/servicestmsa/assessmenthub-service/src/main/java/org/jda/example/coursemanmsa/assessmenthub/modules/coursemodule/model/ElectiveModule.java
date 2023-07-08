package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "electivemodule") 
@PrimaryKeyJoinColumn(name = "id")
@JsonTypeName("elective") 
public class ElectiveModule extends CourseModule{

	private String deptname;

}
