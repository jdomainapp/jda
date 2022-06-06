package org.jda.example.coursemanmsa.academic.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter @Setter @ToString
public class Enrolment{

	private int id;
	private String studentId;
	private int coursemoduleId;
	private double internalmark;
	private double exammark;
	private String finalgrade;
	private Coursemodule coursemodule;
	private Student student;

}