package jda.test.model.enhanced;

import java.util.Calendar;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.test.model.basic.City;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="test_enhanced")
public class Student extends Person {
  @DAttr(name = "email", type = Type.String, length = 30, optional = false)
  private String email;
  
  @DAttr(name = "supervisor", type = Type.Domain, length =6)
  private Instructor supervisor;
    
  private static final int CURRENT_YEAR = Calendar.getInstance().get(
      Calendar.YEAR);

  // students have their own aut-generated ids
  private static int studentCurrNum = 0;

  // constructor methods
  public Student(String id, String name, String dob, City address, String email, Instructor supervisor) 
  throws ConstraintViolationException {
    // generate an id
    super(nextID(id), name, dob, address);
    this.email = email;
    this.supervisor = supervisor;
  }

//  public Student(String id, String name, String dob, City address, String email) 
//  throws ConstraintViolationException {
//    // generate an id
//    this(id, name, dob, address, email, null);
//  }

  public Student(String name, String dob, City address, String email, Instructor supervisor) {
    this(null, name, dob, address, email, supervisor);
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setSupervisor(Instructor supervisor) {
    this.supervisor = supervisor;
  }
  
  public String getEmail() {
    return email;
  }

  public Instructor getSupervisor() {
    return supervisor;
  }
  
  // override toString
  /**
   * @effects returns <code>Student(id,name,dob,address,email)</code>.
   */
  public String toString(boolean full) {
    if (full)
      return "Student(" + getId() + "," + getName() + "," + getDob() + ","
          + getAddress() + "," + email + ")";
    else
      return super.toString(false);
  }

  protected static String nextID(String currId)
      throws ConstraintViolationException {
    if (currId == null) { // generate next id
      if (studentCurrNum == 0)
        studentCurrNum = CURRENT_YEAR;
      else
        studentCurrNum++;

      return "S" + studentCurrNum;
    } else {
      // update currNum
      updateID(currId);
      return currId;
    }
  }

  protected static void updateID(String currID)
      throws ConstraintViolationException {
    int num;
    try {
      num = Integer.parseInt(currID.substring(1));
    } catch (RuntimeException e) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, e, 
          "Lỗi giá trị thuộc tính ID: {0}", currID);
    }
    
//    if (num <= studentCurrNum)
//      throw new ConstraintViolationException(
//          ConstraintViolationException.Code.INVALID_VALUE,
//          "Lỗi giá trị thuộc tính ID: {0}", currID);

    if (num > studentCurrNum)
      studentCurrNum = num;
  }
  
  /**
   * This method is required for loading this class metadata from storage 
   * 
   * @requires 
   *  id != null
   * @effects 
   *  update <tt>currNum</tt> from the value of <tt>id</tt>
   */
  public static void setStudentCurrNum(String id) throws ConstraintViolationException {
    if (id != null) {
      try {
        int num = Integer.parseInt(id.substring(1));
        
        if (num > studentCurrNum) // extra check
          studentCurrNum = num;
        
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Lỗi giá trị thuộc tính ID: {0}", id);
      }
    }
  }
}
