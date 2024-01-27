//Serialisable = true
package org.coursemanmdsa.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter @Setter @ToString
@Entity
@Table(name="student", schema="student")
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
	@Column(name = "gender")
	private String studentGender;
	@Column(name = "dob")
	private Date studentDob;
	@Column(name = "address_id", nullable = false)
	private int addressId;
	@Column(name="email")
	private String studentEmail;
	@Column(name = "class_id", nullable = false)
	private int studentclassId;

	private String deptname;

}