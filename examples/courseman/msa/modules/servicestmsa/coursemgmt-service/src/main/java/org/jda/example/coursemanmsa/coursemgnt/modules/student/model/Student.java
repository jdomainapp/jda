//Serialisable = true
package org.jda.example.coursemanmsa.coursemgnt.modules.student.model;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name="student")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = Student.class)
public class Student extends RepresentationModel<Student> {

	@Id
	@Column(name = "id", nullable = false)
	private String id;
	@Column(name = "name", nullable = false)
	private String studentName;
	@Column(name = "gender_name")
	private String studentGender;
	@Column(name = "dob")
	private Date studentDob;
	@Column(name = "address_id", nullable = false)
	private int addressId;
	@Column(name="email")
	private String studentEmail;
	@Column(name = "studentclass_id", nullable = false)
	private int studentclassId;
	@Column(name = "deptname", nullable = false)
	private String deptName;
	
	@OneToMany(mappedBy="student")
	private List<Enrolment> enrolments;
}