package jda.modules.patterndom.test.dom.tgc;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;

/**
 * Represents a course module.
 * @author dmle
 * @version 
 * - support many-many association normaliser
 */
public class CourseModule {
  /*** STATE SPACE **/
  @DAttr(name="code",type=Type.String,length=6,auto=true,mutable=false,optional=false,
      derivedFrom={"semester"})
  private String code;
  
  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  
  @DAttr(name="semester",type=Type.Integer,optional=false,min=1,max=10)
  private int semester;
  
  @DAttr(name="credits",type=Type.Integer,optional=false,min=1,max=5)
  private int credits;

 }
