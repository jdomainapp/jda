package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.coursemodulemgmt.modules.teacher.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter @ToString
@Entity
@Table(name="teacher", schema="teacher")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = Teacher.class)
public class Teacher extends RepresentationModel<Teacher> {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "name", nullable = false)
	private String teacherName;
	@Column(name = "gender")
	private String teacherGender;
	@Column(name = "dob")
	private Date teacherDob;
	@Column(name = "address_id", nullable = false)
	private int addressId;
	@Column(name="email")
	private String teacherEmail;
	private String deptname;
	
}