package org.examples.jda.sccl.courseman.coursemodule.model;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;

/**
 * Represents an elective module (a subclass of Module)
 * @author dmle
 *
 */
@DClass(schema="test_basic")
public class ElectiveModule extends CourseModule {
  public ElectiveModule() {}
  // extra attribute of elective module
  @DAttr(name="deptName",type=Type.String,length=50,optional=false)
  private String deptName;
  
  // constructor method
  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  public ElectiveModule(String name, int semester, int credits, String deptName) {
    this(null, null, name, semester, credits, deptName);
  }
  
  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  public ElectiveModule(String name, Integer semester, Integer credits,String deptName) {
    this(null, null, name, semester, credits,deptName);
  }
  
  public ElectiveModule(Integer id, String code, String name, Integer semester, Integer credits,String deptName) {
    super(id, code,name,semester,credits);
    this.deptName = deptName;
  }
  
  // setter method 
  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }
  
  // getter method
  public String getDeptName() {
    return deptName;
  }  
}
