package org.jda.example.coursemansw.services.student.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview Represents the gender of a person.
 *
 * @author Duc Minh Le (ducmle)
 */
public enum Gender {
  Male,
  Female,
  //Others
  ;
  
  @DAttr(name="name", type=Type.String, id=true, length=10)
  public String getName() {
    return name();
  }
}
