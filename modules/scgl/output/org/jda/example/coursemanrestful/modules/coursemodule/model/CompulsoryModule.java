package org.jda.example.coursemanrestful.modules.coursemodule.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;

/**
 * Represents a compulsory module (a subclass of Module)
 *
 * @author dmle
 *
 */
@JsonTypeName("compulsory")    // ducmle: feature#55
@DClass(schema="courseman")
public class CompulsoryModule extends CourseModule {

  // constructor method
  // the order of the arguments must be this:
  // - super-class arguments first, then sub-class
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public CompulsoryModule(
      @AttrRef("name")@JsonProperty("name")  String name,
      @AttrRef("description")@JsonProperty("description") String description,
      @AttrRef("semester")@JsonProperty("semester")  Integer semester, 
      @AttrRef("credits")@JsonProperty("credits")  Integer credits,
      @AttrRef("rating")@JsonProperty("rating") Integer rating,
      @AttrRef("cost")@JsonProperty("cost") Double cost
  ) {
    this(null, null, name, description, semester, credits, rating, cost);
  }

  @DOpt(type = DOpt.Type.RequiredConstructor)
  public CompulsoryModule(@AttrRef("name") String name,
                         @AttrRef("semester") Integer semester,
                           @AttrRef("credits") Integer credits) {
    super(name, semester, credits);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  @JsonCreator
  public CompulsoryModule(@JsonProperty("id") Integer id, 
      @JsonProperty("code") String code, 
      @JsonProperty("name") String name,
      @JsonProperty("description") String description,
      @JsonProperty("semester") Integer semester, 
      @JsonProperty("credits") Integer credits,
      @JsonProperty("rating") Integer rating,
      @JsonProperty("cost") Double cost
      )
    throws ConstraintViolationException {
    super(id, code, name, description, semester, credits, rating, cost);
  }

  private CompulsoryModule() { }
}
