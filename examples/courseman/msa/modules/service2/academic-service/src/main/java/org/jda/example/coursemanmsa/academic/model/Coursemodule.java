package org.jda.example.coursemanmsa.academic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class Coursemodule {

	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;
	
	private String coursemoduletype;
	
	private String deptname;

}
