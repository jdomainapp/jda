package jda.test.model.basic;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 
 * - 2.0 <br>
 * - 3.1: added attribute studentInfo
 * 
 */
@DClass(schema="test_basic")
public class Student implements Serializable {
  static final long serialVersionUID = 2012L;

  private static final int NameLength = 30;
  private static final int DobLength = 10;
  private static final String StudentInfoFormat = "%-"+NameLength+"s | %"+DobLength+"s";

  // attributes of students
  @DAttr(name = "id", id = true, type = Type.String, auto = true, 
      length = 6, mutable = false, optional = false)
  private String id;
  // static variable to keep track of student id
  private static int idCounter = 0;
  
  @DAttr(name = "name", type = Type.String, length = NameLength, optional = false)
  private String name;
  @DAttr(name = "dob", type = Type.String, length = 15, optional = false)
  private String dob;
  @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
  private City address;
  @DAttr(name = "email", type = Type.String, length = 30, optional = false)
  private String email;

  /**derived from {@link #fullName}, {@link #dob}*/
  @DAttr(name="studentInfo",type=Type.String,auto=true,mutable=false,optional=false,serialisable=false,
      derivedFrom={"name", "dob"})
  private String studentInfo;
  
  @DAttr(name="sclass",type=Type.Domain,length = 6)
  @DAssoc(ascName="class-has-student",role="student",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=SClass.class,cardMin=0,cardMax=1))
  private SClass sclass;
  
  // constructor method
  // without class
  public Student(String name, String dob, City address, String email) {
    this(null, name, dob, address, email, null);
  }
  
  public Student(String name, String dob, City address, String email, SClass sclass) {
    this(null, name, dob, address, email, sclass);
  }

  public Student(String id, String name, String dob, City address, String email, SClass sclass)
      throws ConstraintViolationException {
    // generate an id
    this.id = nextID(id);

    // assign other values
    this.name = name;
    this.dob = dob;
    this.address = address;
    this.email = email;
    
    this.sclass = sclass;
    
    updateStudentInfo();
  }

  private void updateStudentInfo() {
    studentInfo = String.format(StudentInfoFormat, getName(), dob);
  }

  public String getStudentInfo() {
    return studentInfo;
  }

  // setter methods
  public void setName(String name) {
    this.name = name;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public void setAddress(City address) {
    this.address = address;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  // getter methods
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDob() {
    return dob;
  }

  public City getAddress() {
    return address;
  }

  public String getEmail() {
    return email;
  }

  public SClass getSclass() {
    return sclass;
  }

  public void setSclass(SClass sclass) {
    this.sclass = sclass;
  }

  // override toString
  /**
   * @effects returns <code>this.id</code>
   */
  public String toString() {
    return toString(true);
  }

  /**
   * @effects returns <code>Student(id,name,dob,address,email)</code>.
   */
  public String toString(boolean full) {
    if (full)
      return "Student(" + id + "," + name + "," + dob + "," + address + ","
          + email + ")";
    else
      return "Student(" + id + ")";
  }

  public boolean equals(Object o) {
    if (o == null)
      return false;

    if (!(o instanceof Student))
      return false;

    Student s = (Student) o;

    return (s.id.equals(this.id));
  }

  // automatically generate the next student id
  private String nextID(String id) throws ConstraintViolationException {
    if (id == null) { // generate a new id
      if (idCounter == 0) {
        idCounter = Calendar.getInstance().get(Calendar.YEAR);
      } else {
        idCounter++;
      }
      return "S" + idCounter;
    } else {
      // update id
      int num;
      try {
        num = Integer.parseInt(id.substring(1));
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e,
            "Lỗi giá trị thuộc tính ID: {0}", id);
      }

      // if (num <= idCounter) {
      // throw new ConstraintViolationException(
      // ConstraintViolationException.Code.INVALID_VALUE,
      // "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + idCounter);
      // }
      if (num > idCounter) {
        idCounter = num;
      }
      return id;
    }
  }

  /**
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {
    
    if (minVal != null && maxVal != null) {
      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

      String maxId = (String) maxVal;
      
      try {
        int maxIdNum = Integer.parseInt(maxId.substring(1));
        
        if (maxIdNum > idCounter) // extra check
          idCounter = maxIdNum;
        
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Lỗi giá trị thuộc tính ID: {0}", maxId);
      }
    }
  }
  
  // overrides default serializable/deserializable methods to handle static
  // constant
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeInt(this.idCounter);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    int n = in.readInt();
    this.idCounter = n;
  }
}
