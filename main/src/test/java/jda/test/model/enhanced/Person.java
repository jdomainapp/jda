package jda.test.model.enhanced;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.test.model.basic.City;
@DClass(schema="test_enhanced")
public abstract class Person {
  // attributes of students
  @DAttr(name = "id", id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
  private String id;
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  @DAttr(name = "dob", type = Type.String, length = 15, optional = false)
  private String dob;
  @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
  private City address;

  // static variable to keep track of ids
  protected static int currNum = 0;

  protected Person(String name, String dob, City address) {
    this(null, name, dob, address);
  }
  
  protected Person(String id, String name, String dob, City address) {
    if (id == null)
      this.id = nextID();
    else { 
      // update currNum
      updateID(id);
      
      this.id = id;
    }
    
    this.name = name;
    this.dob = dob;
    this.address = address;
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

  // getter methods
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public City getAddress() {
    return address;
  }

  public String getDob() {
    return dob;
  }

  // automatically generate the next person id
  private static String nextID() {
    currNum++;
    return "P" + currNum;
  }

  private static void updateID(String currID) throws ConstraintViolationException {
    int num;
    try {
      num = Integer.parseInt(currID.substring(1));
    } catch (RuntimeException e) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, e, 
          "Lỗi giá trị thuộc tính ID: {0}", currID);
    }
    
//    if (num <= currNum)
//      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
//          "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + currNum);

    if (num > currNum)
      currNum = num;
  }
  
  public boolean equals(Object o) {
    if (o == null)
      return false;

    if (!(o instanceof Person))
      return false;

    Person s = (Person) o;

    return (s.id.equals(this.id));
  }

  public String toString() {
    return toString(true);
  }
  
  /**
   * @effects returns <code>CLASS_NAME(id,name,dob,address)</code>, where CLASS_NAME is the name of 
   *          a sub-class of this
   */
  public String toString(boolean full) {
    if (full)
      return this.getClass().getSimpleName()+"(" + getId() + "," + getName()+ "," + getDob() + "," + getAddress()+ ")";
    else
      return this.getClass().getSimpleName()+"("+getId()+")";
  }
}
