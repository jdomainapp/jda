//Serialisable = true
package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "coursemodule")
public class Coursemodule {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;
	
	private String coursemoduletype;
	
	private String deptname;

}