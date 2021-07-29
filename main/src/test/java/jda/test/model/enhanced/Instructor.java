package jda.test.model.enhanced;

import jda.modules.dcsl.syntax.DClass;
import jda.test.model.basic.City;
@DClass(schema="test_enhanced")
public class Instructor extends Staff {
  
  public Instructor(String id,String name, String dob, City address,String joinDate,String deptName) {
    super(id, name,dob,address,joinDate,deptName);
  }
  public Instructor(String name, String dob, City address,String joinDate,String deptName) {
    this(null, name, dob, address, joinDate, deptName);
  }

  /**version 2: two-way association with Student*/
//  @DomainConstraint(name = "assistant", type = Type.Domain, length =6)
//  private Student assistant;
//
//  public Instructor(String id,String name, String dob, City address,String joinDate,String deptName, Student assistant) {
//    super(id, name,dob,address,joinDate,deptName);
//    this.assistant = assistant;
//  }
//  public Instructor(String name, String dob, City address,String joinDate,String deptName, Student assistant) {
//    this(null, name, dob, address, joinDate, deptName, assistant);
//  }
//  
//  public void setAssistant(Student a) {
//    this.assistant = a;
//  }
//  
//  public Student getAssistant() {
//    return this.assistant;
//  }
}
