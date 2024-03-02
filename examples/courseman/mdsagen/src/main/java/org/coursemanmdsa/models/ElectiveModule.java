package org.coursemanmdsa.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Getter @Setter @ToString
@Entity
@Table(name = "electivemodule", schema="coursemodule")
@PrimaryKeyJoinColumn(name = "id")
@JsonTypeName("elective") 
public class ElectiveModule extends CourseModule {

	private String deptname;

}
