package jda.test.model.extended;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a course module. The module id is auto-incremented from a base
 * calculated by "M" + semester-value * 100.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="test_extended")
public abstract class Module implements Serializable {
  static final long serialVersionUID = 2013L;

  // attributes
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=3,mutable=false,optional=false)
  private int id;
  private static int idCounter;

  @DAttr(name="code",auto=true,type=Type.String,length=6, 
      mutable=false,optional=false,derivedFrom={"semester"})
  private String code;
  
  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  @DAttr(name="semester",type=Type.Integer,length = 2,optional=false,min = 1)
  private int semester;
  @DAttr(name="credits",type=Type.Integer,length=2,optional=false,min=1)
  private int credits;

  // TODO: this creates a cycle in the dependency graph of the domain schema of this 
  // application which causes an exception to be thrown during set-up  
  @DAttr(name="enrolments",type=Type.Collection,optional = false,
      serialisable=false,
      filter=@Select(clazz=Enrolment.class))
  //v2.6.4.b: @Update(add="addEnrolment",delete="removeEnrolment")
  @DAssoc(ascName="module-has-enrolments",role="module",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private List<Enrolment> enrolments;
  
  // static variable to keep track of module code
//  private static final int[] SEMESTERS = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
//  private static int[] currNums = new int[SEMESTERS.length];
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
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  // only need to do this for reflexive association: @MemberRef(name="enrolments")
  public boolean addEnrolment(Enrolment e) {
    enrolments.add(e);
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="enrolments")
  public boolean addEnrolment(List<Enrolment> enrols) {
    enrolments.addAll(enrols);
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="enrolments")
  public boolean removeEnrolment(Enrolment e) {
    enrolments.remove(e);
    
    // no other attributes changed
    return false; 
  }
  
  public void setEnrolments(List<Enrolment> en) {
    this.enrolments = en;
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

  public List<Enrolment> getEnrolments() {
    return enrolments;
  }

  // override toString
  public String toString() {
    return this.getClass().getSimpleName() + "(" + getCode() + "," + getName()
        + ")";
  }

//  public boolean equals(Object o) {
//    if (o == null)
//      return false;
//
//    if (!(o instanceof Module))
//      return false;
//
//    Module m = (Module) o;
//
//    return (m.code.equals(this.code));
//  }

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
    Module other = (Module) obj;
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
  
//  private String nextCode(String currCode, int semester) throws ConstraintViolationException {
//    if (currCode == null) { // generate one
//      int currNum = currNums[semester - 1];
//      if (currNum == 0) {
//        currNum = semester * 100;
//      } else {
//        currNum++;
//      }
//      currNums[semester - 1] = currNum;
//      return "M" + currNum;
//    } else { // update
//      // update code by semester
//      updateCodeBySemester(semester, currCode);
//
//      return currCode;
//    }
//  }
//
//  /**
//   * This method is required to load metadata of this class from storage
//   * 
//   * @requires 
//   *  codeBySemesters != null
//   * @effects
//   *  update <tt>currNums</tt> from the list of list (pairs) <tt>codeBySemesters</tt>
//   */
//  public static void setCurrNums(List<List> codeBySemesters) 
//  throws ConstraintViolationException {
//    if (codeBySemesters != null) {
//      String code; 
//      Integer semester;
//      for (List t : codeBySemesters) {
//        semester = (Integer)t.get(0);
//        code = (String) t.get(1);
//        updateCodeBySemester(semester,code);
//      }
//    }
//  }
//  
//  private static void updateCodeBySemester(int semester, String currCode) 
//      throws ConstraintViolationException {
//    int num;
//    try {
//      num = Integer.parseInt(currCode.substring(1));
//    } catch (RuntimeException e) {
//      throw new ConstraintViolationException(
//          ConstraintViolationException.Code.INVALID_VALUE, e,
//          "Lỗi giá trị thuộc tính ID: {0}", currCode);
//    }
//
//    int c = currNums[semester - 1];
//    if (num > c) {
//      currNums[semester - 1] = num;
//    }  
//  }
//  
//  // overrides default serializable/deserializable methods to handle static
//  // constant
//  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//    out.defaultWriteObject();
//    for (int i = 0; i < currNums.length; i++) {
//      out.writeInt(this.currNums[i]);
//    }
//  }
//
//  private void readObject(java.io.ObjectInputStream in) throws IOException,
//      ClassNotFoundException {
//    in.defaultReadObject();
//    int n;
//    for (int i = 0; i < SEMESTERS.length; i++) {
//      n = in.readInt();
//      currNums[i] = n;
//    }
//
//  }
}
