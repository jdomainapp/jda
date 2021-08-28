package org.jda.example.courseman.bspacegen.input;

import org.jda.example.courseman.bspacegen.input.Student;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 * A domain class representing simple addresses that contain just the city names. This class is used as 
 * the <code>allowedValues</code> of the domain attributes of 
 * other domain classes (e.g. Student.address).  
 * 
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to 
 * return the string representation of a city name which is expected by 
 * the application. 
 * 
 * @author dmle
 * @version 1.0
 */
@DClass(schema="courseman")
public class Address {
  /*** STATE SPACE **/

  @DAttr(name="id",id=true,auto=true,length=3,mutable=false,optional=false,type=Type.Integer)
  private int id;
  
  @DAttr(name="cityName",type=Type.String,length=20,optional=false)
  private String cityName;
  
  @DAttr(name="student",type=Type.Domain,optional=true,serialisable=false)
  @DAssoc(ascName="student-has-address",role="address",
    ascType=AssocType.One2One, endType=AssocEndType.One,
    associate=@Associate(type=Student.class,cardMin=1,cardMax=1,determinant=true))
  private Student student;
  
  /*** BEHAVIOUR SPACE **/
}
