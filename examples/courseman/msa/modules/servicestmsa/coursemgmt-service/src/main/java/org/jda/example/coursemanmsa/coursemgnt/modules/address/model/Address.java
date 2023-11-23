//Serialisable = true
package org.jda.example.coursemanmsa.coursemgnt.modules.address.model;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
@Entity
@Table(name="address", schema="address")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id", scope = Address.class)
public class Address extends RepresentationModel<Address> {

	@Id
	@Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;
}