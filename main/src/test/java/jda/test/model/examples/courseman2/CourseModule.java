package jda.test.model.examples.courseman2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 * Represents a course module. The module id is auto-incremented from a base
 * calculated by "M" + semester-value * 100.
 * 
 * @author dmle
 * @version 
 * - 2.0: support reflexive associations: one-many
 */
@DClass(schema=Constants.SCHEMA_NAME)
public abstract class CourseModule {

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

  /** v2.0: reflexive association (one-many): <br>
   *  - a course module may have one or more be other course modules as a pre-requisite <br> 
   *  - a course module may be a pre-requisite for at most one other course module
   *  
   * <p>This attribute realises the role of a course module (that has prerequisites). 
   */
  @DAttr(name="prerequisites",type=Type.Collection,optional = false,
      serialisable=false,filter=@Select(clazz=CourseModule.class))
  @DAssoc(ascName="coursemodule-has-prerequisites",role="coursemodule",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=CourseModule.class,cardMin=0,cardMax=5))
  private Collection<CourseModule> prerequisites;
  private int prerequisitesCount;
  
  /** v2.0: an attribute needed for the definition of reflexive association {@link #prerequisites} 
   * 
   * <p>This attribute realises the role of a prerequisite in this association. 
   */ 
  @DAttr(name="prerequisiteForCourseModule",type=Type.Domain,optional = false)
  @DAssoc(ascName="coursemodule-has-prerequisites",role="prerequisite",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=CourseModule.class,cardMin=1,cardMax=1),dependsOn=true)
  private CourseModule prerequisiteForCourseModule;
  
  // static variable to keep track of module code
  private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();

  // constructor method: create objects from data source
  protected CourseModule(Integer id, String code, String name, Integer semester, Integer credits)
      throws ConstraintViolationException {
    this.id = nextID(id);
    // automatically generate a code
    this.code = nextCode(code, semester);

    // assign other values
    this.name = name;
    this.semester = semester;
    this.credits = credits;
    
    // v2.0:
    prerequisites = new ArrayList();
  }

  protected CourseModule(String name, int semester, int credits) {
    this(null, null, name, semester, credits);
  }

  // overloading constructor to support object type values
  protected CourseModule(String name, Integer semester, Integer credits) {
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

  /** association: {@link #prerequisites} */
  
  @DOpt(type=DOpt.Type.LinkAdder)
  //@MemberRef(name="prerequisites")  
  public boolean addPrerequisite(CourseModule s) {
    if (!this.prerequisites.contains(s)) {
      prerequisites.add(s);
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewPrerequisite(CourseModule s) {
    prerequisites.add(s);
    prerequisitesCount++;
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addPrerequisite(Collection<CourseModule> prerequisites) {
    for (CourseModule s : prerequisites) {
      if (!this.prerequisites.contains(s)) {
        this.prerequisites.add(s);
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewPrerequisite(Collection<CourseModule> prerequisites) {
    this.prerequisites.addAll(prerequisites);
    prerequisitesCount += prerequisites.size();

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="prerequisites")
  public boolean removePrerequisite(CourseModule s) {
    boolean removed = prerequisites.remove(s);
    
    if (removed) {
      prerequisitesCount--;
    }
    
    // no other attributes changed
    return false; 
  }
  
  public void setPrerequisites(Collection<CourseModule> prerequisites) {
    this.prerequisites = prerequisites;
    
    prerequisitesCount = prerequisites.size();
  }
  
  public Collection<CourseModule> getPrerequisites() {
    return prerequisites;
  }
  
  /**
   * @effects 
   *  return <tt>prerequisitesCount</tt>
   */
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getPrerequisitesCount() {
    return prerequisitesCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setPrerequisitesCount(int count) {
    prerequisitesCount = count;
  }
  
  public CourseModule getPrerequisiteForCourseModule() {
    return prerequisiteForCourseModule;
  }

  public void setPrerequisiteForCourseModule(CourseModule prerequisite) {
    this.prerequisiteForCourseModule = prerequisite;
  }

  /** END association: {@link #prerequisites} */

  
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
              ConstraintViolationException.Code.INVALID_VALUE, e, 
              "Lỗi giá trị thuộc tính ID: {0}", maxCode);
        }
      }
    }    
  }
}
