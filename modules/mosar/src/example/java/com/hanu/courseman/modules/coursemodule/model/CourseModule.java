package com.hanu.courseman.modules.coursemodule.model;

import java.util.LinkedHashMap;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;

/**
 * Represents a course module. The module id is auto-incremented from a base
 * calculated by "M" + semester-value * 100.
 *
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public abstract class CourseModule {

  // attributes
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=3,mutable=false,optional=false)
  private int id;
  private static int idCounter;

  @DAttr(name="code",auto=true,type=Type.String, length=12,
      mutable=false,optional=false,derivedFrom={"semester"})
  private String code;

  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  @DAttr(name="semester",type=Type.Integer,length = 2,optional=false,min = 1)
  private int semester;
  @DAttr(name="credits",type=Type.Integer,length=2,optional=false,min=1)
  private int credits;


  // static variable to keep track of module code
  private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();

  protected CourseModule() {
    id = nextID(null);
  }

  // constructor method: create objects from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  protected CourseModule(Integer id, String code, String name, Integer semester, Integer credits)
      throws ConstraintViolationException {
    this.id = nextID(id);
    // automatically generate a code
    this.code = nextCode(code, semester);

    // assign other values
    this.name = name;
    this.semester = semester;
    this.credits = credits;
  }

//  @DOpt(type=DOpt.Type.ObjectFormConstructor)
//  protected CourseModule(@AttrRef("name") String name,
//      @AttrRef("semester") int semester, @AttrRef("credits") int credits) {
//    this(null, null, name, semester, credits);
//  }

  // overloading constructor to support object type values
  // @version 2.0
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  protected CourseModule(@AttrRef("name") String name,
      @AttrRef("semester") Integer semester, @AttrRef("credits") Integer credits) {
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
    if (code == null) this.code = nextCode(null, semester);
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
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "(" + getCode() + "," + getName()
        + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CourseModule other = (CourseModule) obj;
    if (code == null) {
      if (other.code != null)
        return false;
    } else if (!code.equals(other.code))
      return false;
    return true;
  }

  // automatically generate a next module code
  private String nextCode(String currCode, int semester) throws ConstraintViolationException {
    Tuple derivingVal = Tuple.newInstance(semester);
    if (currCode == null) { // generate one
      Integer currNum = currNums.get(derivingVal);
      if (currNum == null) {
        currNum = semester * 100;
      } else {
        currNum++;
      }
      currNums.put(derivingVal, currNum);
      return "M" + currNum;
    } else { // update
      int num;
      try {
        num = Integer.parseInt(currCode.substring(1));
      } catch (RuntimeException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e,
            "Lỗi giá trị thuộc tính: {0}", currCode);
      }

      Integer currMaxVal = currNums.get(derivingVal);
      if (currMaxVal == null || num > currMaxVal) {
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
              ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxCode});
        }
      }
    }
  }
}
