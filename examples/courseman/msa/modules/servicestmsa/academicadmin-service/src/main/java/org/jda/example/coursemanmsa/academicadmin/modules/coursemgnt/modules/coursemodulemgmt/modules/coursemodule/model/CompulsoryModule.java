package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.coursemodule.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Getter @Setter @ToString
@Entity
@Table(name = "compulsorymodule") 
@PrimaryKeyJoinColumn(name = "id")
@JsonTypeName("compulsory") 
public class CompulsoryModule extends CourseModule{
	
}
