package org.jda.example.courseman.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents an enrolment
 * 
 * @author dmle
 * 
 */
@DClass(schema="courseman")
public class Enrolment {
  /*** STATE SPACE **/
  // attributes
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,optional=false,mutable=false,min=1)
  private int id;

  @DAttr(name="student",type=Type.Domain,optional=false)
  @DAssoc(ascName="std-has-enrols",role="enrolment",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Student.class,cardMin=1,cardMax=1),
    dependsOn=true)
  private Student student;
  
  @DAttr(name="module",type=Type.Domain,optional=false)
  @DAssoc(ascName="mod-has-enrols",role="enrolment",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=CourseModule.class,cardMin=1,cardMax=1),
    dependsOn=true)
  private CourseModule module;
  
  @DAttr(name="internalMark",type=Type.Double,optional=true,min=0.0,max=10.0)
  private Double internalMark;
  
  @DAttr(name="examMark",type=Type.Double,optional=true,min=0.0,max=10.0)
  private Double examMark;

  // v2.6.4.b derived from two attributes
  @DAttr(name="finalMark",type=Type.Integer,auto=true,mutable=false,optional=true,
      serialisable=false,
      derivedFrom={"internalMark", "examMark"})
  private Integer finalMark;

  @DAttr(name="finalGrade",type=Type.Char,auto=true,mutable=false,optional=true
      /* Note: no need to do this:
       derivedFrom={"internalMark,examMark"}
       * because finalGrade and finalMark are updated by the same method and this is already specified by finalMark (below)
       */
  )
  private Character finalGrade;

  /*** BEHAVIOUR SPACE **/

  private static final Character CHAR_NULL = '\u0000';
  private static int idCounter = 0;

  // constructor method
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Enrolment(Student s, CourseModule m) 
      throws ConstraintViolationException {
    this(null, s, m, 0.0, 0.0, null);
  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Enrolment(Student s, CourseModule m, Double internalMark, Double examMark) 
      throws ConstraintViolationException {
    this(null, s, m, internalMark, examMark, null);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Enrolment(Integer id, Student s, CourseModule m, Double internalMark,
      Double examMark, 
      // v2.7.3: not used but needed to load data from source
      Character finalGrade
      ) {
    
    this.id = nextID(id);
    this.student = s;
    this.module = m;
    this.internalMark = (internalMark != null) ? internalMark.doubleValue() : null;
    this.examMark = (examMark != null) ? examMark.doubleValue() : null;

    updateFinalMark(); 
  }

  @DOpt(type=DOpt.Type.AutoAttributeValueGen)
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  // setter methods
  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="student")
  public void setStudent(Student s) {
    this.student = s;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="module")
  public void setModule(CourseModule m) {
    this.module = m;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="internalMark")
  public void setInternalMark(Double mark) {
    this.internalMark = mark;
    
    updateFinalMark(); 
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="examMark")
  public void setExamMark(Double mark) {
    this.examMark = mark;
    
    updateFinalMark(); 
  }

  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value="finalMark")
  public void updateFinalMark() {
    // updates both final mark and final grade
    if (internalMark != null && examMark != null) {
      double finalMarkD = 0.4 * internalMark + 0.6 * examMark;
      
      // round the mark to the closest integer value
      finalMark = (int) Math.round(finalMarkD);

      if (finalMark < 5)
        finalGrade = 'F';
      else if (finalMark == 5)
        finalGrade = 'P';
      else if (finalMark <= 7)
        finalGrade = 'G';
      else
        finalGrade = 'E';      
    }
  }

  // getter methods
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="id")
  public int getId() {
    return id;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="student")
  public Student getStudent() {
    return student;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="module")
  public CourseModule getModule() {
    return module;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="internalMark")
  public Double getInternalMark() {
    return internalMark;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="examMark")  
  public Double getExamMark() {
    return examMark;
  }

  // v2.6.4.b
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="finalMark")
  public int getFinalMark() {
    if (finalMark != null)
      return finalMark;
    else
      return 0;
  }
  
  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="finalGrade")
  public char getFinalGrade() {
    return (finalGrade != null) ? finalGrade : CHAR_NULL;
  }

  // override toString
  @Override
  public String toString() {
    return toString(false);
  }

  public String toString(boolean full) {
    if (full)
      return "Enrolment(" + student + "," + module + ")";
    else
      return "Enrolment(" + getId() + "," + student.getId() + ","
          + module.getCode() + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
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
    Enrolment other = (Enrolment) obj;
    if (id != other.id)
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
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      } 
      // TODO add support for other attributes here 
    }
  }
}
