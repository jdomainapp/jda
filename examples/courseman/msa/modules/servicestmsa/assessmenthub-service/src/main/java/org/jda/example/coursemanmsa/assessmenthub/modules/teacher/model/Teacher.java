package org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name="teacher")
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
	@Column(name = "gender_name")
	private String teacherGender;
	@Column(name = "dob")
	private Date teacherDob;
	@Column(name = "address_id", nullable = false)
	private int addressId;
	@Column(name="email")
	private String teacherEmail;
	@Column(name = "deptname", nullable = false)
	private String deptName;

	@OneToMany(mappedBy="teacher")
    private List<Coursemodule> coursemodules;
}