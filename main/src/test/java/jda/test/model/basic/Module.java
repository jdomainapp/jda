package jda.test.model.basic;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a course module. The module id is auto-incremented from a base
 * calculated by "M" + semester-value * 100.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="test_basic")
public abstract class Module implements Serializable {
  static final long serialVersionUID = 2013L;

  @DAttr(name="id",id=true,auto=true,length=3,mutable=false,optional=false,type=Type.Integer)
  private int id;
  private static int idCounter;
  
  // attributes
  @DAttr(name = "code",type = Type.String, length = 6, auto = true, 
      mutable = false, optional = false, derivedFrom={"semester"})
  private String code;
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  @DAttr(name = "semester", type = Type.Integer, length = 2, optional = false, min = 1)
  private int semester;
  @DAttr(name = "credits", type = Type.Integer, length = 2, optional = false, min = 1)
  private int credits;

  // static variable to keep track of module code
  //private static final int[] SEMESTERS = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
  private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();

  // constructor method: create objects from data source
  protected Module(Integer id, String code, String name, Integer semester, Integer credits)
      throws ConstraintViolationException {
    this.id = nextID(id);
    // automatically generate a code
    this.code = nextCode(code, semester);

    // assign other values
    this.name = name;
    this.semester = semester;
    this.credits = credits;
  }

  protected Module(String name, int semester, int credits) {
    this(null, null, name, semester, credits);
  }

  // overloading constructor to support object type values
  // @version 2.0
  protected Module(String name, Integer semester, Integer credits) {
    this(null, null, name, semester, credits);
  }

  private static int nextID(Integer currID) {
    if (currID == null) {
      idCounter++;
      return idCounter;
    } else {
      int num = currID.intValue();
      if (num > idCounter)
        idCounter = num;
      
      return currID;
    }
  }
  
  public int getId() {
    return id;
  }
  
  // setter methods
  public void setName(String name) {
    this.name = name;
  }

  public void setSemester(int semester) {
    this.semester = semester;
    
    // v2.7.3: update code
    updateModuleCode();
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }

  // getter methods
  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public int getSemester() {
    return semester;
  }

  public int getCredits() {
    return credits;
  }

  // override toString
  public String toString() {
    return this.getClass().getSimpleName() + "(" + getCode() + "," + getName()
        + ")";
  }

  public boolean equals(Object o) {
    if (o == null)
      return false;

    if (!(o instanceof Module))
      return false;

    Module m = (Module) o;

    return (m.code.equals(this.code));
  }

  /** this annotation is not applied here because code depends on only one attribute (semester)
   * and therefore should be updated directly in setSemester 
  @Metadata(type=Metadata.Type.MethodUpdateDerivingValue)
  @MemberRef(name="code") */
  public void updateModuleCode() {
    // compute a new code (this occurs after updating semester)
    this.code = nextCode(null, semester);
  }
  
  // automatically generate the next module code
  private String nextCode(String currCode, int semester) {
    Tuple derivingVal = Tuple.newInstance(semester); 
    if (currCode == null) { // generate one
      Integer currNum = currNums.get(derivingVal); //currNums[semester - 1];
      if (currNum == null) {
        currNum = semester * 100;
      } else {
        currNum++;
      }
      //currNums[semester - 1] = currNum;
      currNums.put(derivingVal, currNum);
      return "M" + currNum;
    } else { // update
      int num;
      try {
        num = Integer.parseInt(currCode.substring(1));
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e,
            "Lỗi giá trị thuộc tính ID: {0}", currCode);
      }

      Integer currMaxVal = currNums.get(derivingVal); //currNums[semester - 1];
      if (currMaxVal == null || num > currMaxVal) {
        //currNums[semester - 1] = num;
        currNums.put(derivingVal, num);
      }

      return currCode;
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
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
        
      } else if (attrib.name().equals("code")) {
        String maxCode = (String) maxVal;
        
        try {
          int maxCodeNum = Integer.parseInt(maxCode.substring(1));
          
          // current max num for the semester
          Integer currNum = currNums.get(derivingValue);
          
          if (currNum == null || maxCodeNum > currNum) {
            currNums.put(derivingValue, maxCodeNum);
          }
          
        } catch (RuntimeException e) {
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE, e, 
              "Lỗi giá trị thuộc tính ID: {0}", maxCode);
        }
      }
    }
  }
  
  // overrides default serializable/deserializable methods to handle static
  // constant
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
//    for (int i = 0; i < currNums.length; i++) {
//      out.writeInt(this.currNums[i]);
//    }
    out.writeObject(currNums);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
//    int n;
//    for (int i = 0; i < SEMESTERS.length; i++) {
//      n = in.readInt();
//      currNums[i] = n;
//    }
    Object o = in.readObject();
    currNums = (Map<Tuple,Integer>) o;
  }
}
