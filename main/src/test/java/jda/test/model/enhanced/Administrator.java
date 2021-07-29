package jda.test.model.enhanced;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.test.model.basic.City;
@DClass(schema="test_enhanced")
public class Administrator extends Staff {
  @DAttr(name="level",type=Type.Integer,optional=false,min=0,max=10)
  private int level;
  
  public Administrator(String id, String name, String dob, City address,String joinDate,String deptName, Integer level) {
    super(id, name,dob,address,joinDate,deptName);
    this.level=level;
  }  
  
  public Administrator(String name, String dob, City address,String joinDate,String deptName, Integer level) {
    this(null, name, dob, address, joinDate, deptName, level);
  }
  
  public void setLevel(int l) {
    level = l;
  }
  
  public int getLevel() {
    return level;
  }
}
