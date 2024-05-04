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


@Getter @Setter @ToString
@Entity
@Table(name="studentclass", schema="class")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = StudentClass.class)
public class StudentClass extends RepresentationModel<StudentClass> {

	@Id
	@Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;


}