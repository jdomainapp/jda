package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "compulsorymodule", schema="coursemodule")
@PrimaryKeyJoinColumn(name = "id")
@JsonTypeName("compulsory") 
public class CompulsoryModule extends CourseModule{
	
}
