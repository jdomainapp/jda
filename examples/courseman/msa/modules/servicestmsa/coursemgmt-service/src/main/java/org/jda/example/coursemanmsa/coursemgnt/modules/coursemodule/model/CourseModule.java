package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "coursemodule")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CourseModule.class)
public class CourseModule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String code;

	private int credits;

	private String name;

	private int semester;

	@PrimaryKeyJoinColumn
	@OneToOne(mappedBy = "coursemodule", cascade = CascadeType.ALL)
	private CompulsoryModule compulsorymodule;

	@OneToOne(mappedBy = "coursemodule", cascade = CascadeType.ALL)
	private ElectiveModule electivemodule;

	@ManyToOne
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

	public void setId(int id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public void setCompulsorymodule(CompulsoryModule compulsorymodule) {
		this.compulsorymodule = compulsorymodule;
		if(compulsorymodule !=null) {
			this.compulsorymodule.setCoursemodule(this);
		}
		
	}

	public void setElectivemodule(ElectiveModule electivemodule) {
		this.electivemodule = electivemodule;
		if (electivemodule != null) {
			this.electivemodule.setCoursemodule(this);
		}
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

}
