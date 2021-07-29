package jda.test.model.enhanced;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.test.model.basic.City;
@DClass(schema="test_enhanced")
public class Staff extends Person {
  @DAttr(name="joinDate",type=Type.String,length=20,optional=false)
  private String joinDate;
  @DAttr(name="deptName",type=Type.String,length=50,optional=false)
  private String deptName;
  
  public Staff(String id, String name, String dob, City address,String joinDate,String deptName) {
    super(id,name,dob,address);
    this.joinDate=joinDate;
    this.deptName=deptName;
  }
  
  public Staff(String name, String dob, City address,String joinDate,String deptName) {
    this(null, name, dob, address, joinDate, deptName);
  }
  
  public void setJoinDate(String joinDate) {
    this.joinDate=joinDate;
  }
  
  public String getJoinDate() {
    return this.joinDate;
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
