//Serialisable = true
package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name = "coursemodule")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = CourseModule.class)
public class CourseModule{

	@Id
	@Column(name = "id", nullable = false)
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
	
	@OneToMany(mappedBy="coursemodule")
	private List<Enrolment> enrolments;
}