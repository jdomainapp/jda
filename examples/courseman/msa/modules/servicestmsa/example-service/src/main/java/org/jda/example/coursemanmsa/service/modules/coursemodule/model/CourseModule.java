//Serialisable = true
package org.jda.example.coursemanmsa.service.modules.coursemodule.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.jda.example.coursemanmsa.service.modules.teacher.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "coursemodule")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = CourseModule.class)
public class CourseModule {

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
	
//	@Column(name = "teacher_id")
//	private int teacherId;
	
	@ManyToOne
    @JoinColumn(name="teacher_id", nullable=false)
	private Teacher teacher;
	
	//TODO: separate coursemoudle-enrolments relationships
//	@OneToMany(mappedBy="coursemodule")
//	private List<Enrolment> enrolments;
}