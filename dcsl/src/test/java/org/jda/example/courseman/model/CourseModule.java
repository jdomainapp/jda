package org.jda.example.courseman.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
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
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public class CourseModule {
  /*** STATE SPACE **/
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,mutable=false,optional=false,min=1)
  private int id;
  
  @DAttr(name="code",type=Type.String,length=6,auto=true,mutable=false,optional=false,
      derivedFrom={"semester"})
  private String code;
  
  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  
  @DAttr(name="semester",type=Type.Integer,optional=false,min=1,max=10)
  private int semester;
  
  @DAttr(name="credits",type=Type.Integer,optional=false,min=1,max=5)
  private int credits;

  // v2.6.4b: added support for this association
  @DAttr(name="enrolments",type=Type.Collection,optional=false,serialisable=false,
		  filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="mod-has-enrols",role="module",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private Collection<Enrolment> enrolments;
  
  /*** BEHAVIOUR SPACE **/
  private static int idCounter;

  // derived
  private int enrolmentCount;
  
  // static variable to keep track of module code
  private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();

  // constructor method
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public CourseModule(Integer id, String code, String name, Integer semester, Integer credits) {
    
    this.id = nextID(id);
    // automatically generate a code
    this.code = nextCode(code, semester);

    // assign other values
    this.name = name;
    this.semester = semester;
    this.credits = credits;
    
    this.enrolments = new ArrayList();
    enrolmentCount = 0;
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public CourseModule(String name, Integer semester, Integer credits) {
    this(null, null, name, semester, credits);
  }

  @DOpt(type=DOpt.Type.AutoAttributeValueGen)
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
  
  //automatically generate the next module code
  @DOpt(type=DOpt.Type.AutoAttributeValueGen)
  private String nextCode(String currCode, int semester) {
		Tuple derivingVal = Tuple.newInstance(semester);
		if (currCode == null) { // generate one
			Integer currNum = currNums.get(derivingVal); // currNums[semester -
															// 1];
			if (currNum == null) {
				currNum = semester * 100;
			} else {
				currNum++;
			}
			// currNums[semester - 1] = currNum;
			currNums.put(derivingVal, currNum);
			return "M" + currNum;
		} else { // update
			int num;
			try {
				num = Integer.parseInt(currCode.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(
						ConstraintViolationException.Code.INVALID_VALUE, e, new String[] {currCode});
			}

			Integer currMaxVal = currNums.get(derivingVal); // currNums[semester
															// - 1];
			if (currMaxVal == null || num > currMaxVal) {
				// currNums[semester - 1] = num;
				currNums.put(derivingVal, num);
			}

			return currCode;
		}
  }

  // setter methods
  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="name")
  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="semester")
  public void setSemester(int semester){
    this.semester = semester;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="credits")
  public void setCredits(int credits) {
    this.credits = credits;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addEnrolment(Enrolment e) {
    if (!enrolments.contains(e)) {
      enrolments.add(e);
      
      enrolmentCount++;
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addEnrolment(Collection<Enrolment> enrols) {
    for (Enrolment e : enrols) {
      if (!enrolments.contains(e)) {
        enrolments.add(e);
        
        enrolmentCount++;
      }    
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Enrolment e) {
    enrolments.add(e);
    
    enrolmentCount++;
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  public boolean removeEnrolment(Enrolment e) {
    boolean removed = enrolments.remove(e);
    
    if (removed)
      enrolmentCount--;
    
    // no other attributes changed
    return false; 
  }
  
  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="enrolments")
  public void setEnrolments(List<Enrolment> en) {
    this.enrolments = en;
    enrolmentCount = en.size();
  }
  
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getEnrolmentsCount() {
    return enrolmentCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setEnrolmentsCount(int count) {
    enrolmentCount = count;
  }
  
  // getter methods
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="id")
  public int getId() {
    return id;
  }
  
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="code")
  public String getCode() {
    return code;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="name")
  public String getName() {
    return name;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="semester")
  public int getSemester() {
    return semester;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="credits")
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
      // update this for the correct attribute if there are more than one auto attributes of this class 
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
        
      } else if (attrib.name().equals("code")) {
        String maxCode = (String) maxVal;
        
        try {
          int maxCodeNum = Integer.parseInt(maxCode.substring(1));
          
          // current max num for the semester
          Integer currNum = currNums.get(derivingValue); //currNums[semester - 1];
          
          if (currNum == null || maxCodeNum > currNum) {
            //currNums[semester - 1] = maxCodeNum;
            currNums.put(derivingValue, maxCodeNum);
          }
          
        } catch (RuntimeException e) {
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE, e, new String[] {maxCode});
        }
      }
    }
  }

}
