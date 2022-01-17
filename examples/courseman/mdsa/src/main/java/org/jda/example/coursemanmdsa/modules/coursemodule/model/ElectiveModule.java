package org.jda.example.coursemanmdsa.modules.coursemodule.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;

/**
 * Represents an elective module (a subclass of Module)
 * @author dmle
 *
 */
@JsonTypeName("elective")   // ducmle: feature#55
@DClass(schema="courseman")
public class ElectiveModule extends CourseModule {
  // extra attribute of elective module
  @DAttr(name="deptName",type=Type.String,length=50,optional=false)
  private String deptName;

  // constructor method
  // the order of the arguments must be this:
  // - super-class arguments first, then sub-class
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @JsonCreator
  public ElectiveModule(
      @AttrRef("deptName")@JsonProperty("deptName") String deptName,
      @AttrRef("name")@JsonProperty("name") String name,
      @AttrRef("semester")@JsonProperty("semester")  Integer semester, 
      @AttrRef("credits")@JsonProperty("credits")  Integer credits
      ) {
    this(null, null, name, semester, credits, deptName);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
//  @JsonCreator
  public ElectiveModule(
      @JsonProperty("id") Integer id, 
      @JsonProperty("code") String code, 
      @JsonProperty("name") String name, 
      @JsonProperty("semester") Integer semester, 
      @JsonProperty("credits") Integer credits, 
      @JsonProperty("deptName") String deptName) {
    super(id, code,name,semester,credits);
    this.deptName = deptName;
  }

  private ElectiveModule() { }

  // setter method
  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  // getter method
  public String getDeptName() {
    return deptName;
  }
}
