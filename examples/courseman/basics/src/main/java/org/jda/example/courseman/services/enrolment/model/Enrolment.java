package org.jda.example.courseman.services.enrolment.model;

import java.util.Date;

import org.jda.example.courseman.services.coursemodule.model.CourseModule;
import org.jda.example.courseman.services.student.model.Student;

import jda.modules.common.cache.StateHistory;
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
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents an enrolment
 * 
 * @author dmle
 * 
 */
@DClass(schema = "courseman")
public class Enrolment implements Comparable {

  private static final String AttributeName_InternalMark = "internalMark";
  private static final String AttributeName_ExamMark = "examMark";
  private static final String AttributeName_FinalMark = "finalMark";
  
  // attributes
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name = "student", type = Type.Domain, length = 5, optional = false)
  @DAssoc(ascName = "student-has-enrolments", role = "enrolment", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Student student;

  @DAttr(name = "module", type = Type.Domain, length = 5, optional = false)
  @DAssoc(ascName = "module-has-enrolments", role = "enrolment", 
    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
    associate = @Associate(type = CourseModule.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private CourseModule module;

  @DAttr(name="registDate", type=Type.Date, 
      optional=true)
  private Date registDate;
  
  @DAttr(name = AttributeName_InternalMark, type = Type.Double, length = 4, optional = true, min = 0.0)
  private Double internalMark;
  
  @DAttr(name = AttributeName_ExamMark, type = Type.Double, length = 4, optional = true, min = 0.0)
  private Double examMark;

  @DAttr(name="finalGrade",auto = true, type = Type.Char, length = 1,mutable = false, optional = true 
      /* Note: no need to do this:
       derivedFrom={"internalMark,examMark"}
       * because finalGrade and finalMark are updated by the same method and this is already specified by finalMark (below)
       */
  )
  private char finalGrade;

  // v2.6.4.b derived from two attributes
  @DAttr(name = AttributeName_FinalMark,type=Type.Integer,auto=true,mutable = false,optional = true,
      serialisable=false,
      derivedFrom={AttributeName_InternalMark, AttributeName_ExamMark})
  private Integer finalMark;

  // v2.6.4.b
  private StateHistory<String, Object> stateHist;

  // constructor method
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Enrolment(@AttrRef("student") Student s, 
      @AttrRef("module") CourseModule m) throws ConstraintViolationException {
    this(null, s, m, null, 0.0, 0.0, null);
  }

//  @DOpt(type=DOpt.Type.ObjectFormConstructor)
//  public Enrolment(@AttrRef("student") Student s, 
//      @AttrRef("module") CourseModule m, 
//      @AttrRef("internalMark") Double internalMark, 
//      @AttrRef("examMark") Double examMark)
//      throws ConstraintViolationException {
//    this(null, s, m,  internalMark, examMark, null);
//  }

  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Enrolment(@AttrRef("student") Student s, 
      @AttrRef("module") CourseModule m, 
      @AttrRef("registDate") Date registDate,
      @AttrRef("internalMark") Double internalMark, 
      @AttrRef("examMark") Double examMark)
      throws ConstraintViolationException {
    this(null, s, m, registDate, internalMark, examMark, null);
  }
  
  // @version 2.0
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Enrolment(Integer id, Student s, CourseModule m, 
      Date registDate,
      Double internalMark,
      Double examMark, 
      // v2.7.3: not used but needed to load data from source
      Character finalGrade) throws ConstraintViolationException {
    this.id = nextID(id);
    this.student = s;
    this.module = m;
    this.registDate = registDate;
    this.internalMark = (internalMark != null) ? internalMark.doubleValue()
        : null;
    this.examMark = (examMark != null) ? examMark.doubleValue() : null;

    // v2.6.4.b
    stateHist = new StateHistory<>();

    updateFinalMark(); 
  }

  // setter methods
  public void setStudent(Student s) {
    this.student = s;
  }

  public void setModule(CourseModule m) {
    this.module = m;
  }

  public void setInternalMark(Double mark) {
    // update final grade = false: to keep the integrity of its cached value 
    setInternalMark(mark, false);
  }

  public void setInternalMark(Double mark, boolean updateFinalGrade) {
    this.internalMark = mark;
    if (updateFinalGrade)
      updateFinalMark(); 
  }

  public void setExamMark(Double mark) {
    // update final grade = false: to keep the integrity of its cached value 
    setExamMark(mark, false);
  }
  
  public void setExamMark(Double mark, boolean updateFinalGrade) {
    this.examMark = mark;
    if (updateFinalGrade)
      updateFinalMark(); 
  }

  @DOpt(type=DOpt.Type.DerivedAttributeUpdater)
  @AttrRef(value=AttributeName_FinalMark)
  public void updateFinalMark() {
    // updates both final mark and final grade
    if (internalMark != null && examMark != null) {
      double finalMarkD = 0.4 * internalMark + 0.6 * examMark;
      
      // v2.6.4b: cache final mark
      stateHist.put(AttributeName_FinalMark, finalMark);

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
  public int getId() {
    return id;
  }

  public Student getStudent() {
    return student;
  }

  public CourseModule getModule() {
    return module;
  }
  
  public Date getRegistDate() {
    return registDate;
  }
  
  public void setRegistDate(Date registDate) {
    this.registDate = registDate;
  }

  public Double getInternalMark() {
    return internalMark;
  }

  public Double getExamMark() {
    return examMark;
  }

  public char getFinalGrade() {
    return finalGrade;
  }

  // v2.6.4.b
  public int getFinalMark() {
    return getFinalMark(false);// finalMark;
  }

  public int getFinalMark(boolean cached) throws IllegalStateException {
    if (cached) {
      Object val = stateHist.get(AttributeName_FinalMark);

      if (val == null)
        throw new IllegalStateException(
            "Enrolment.getFinalMark: cached value is null");

      return (Integer) val;
    } else {
      if (finalMark != null)
        return finalMark;
      else
        return 0;
    }

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
      return "Enrolment(" + getId() + "," + 
            ((student != null) ? student.getId() : "null") + "," +
            ((module != null) ? module.getCode() : "null") + ")";
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

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      // if (num <= idCounter) {
      // throw new
      // ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
      // "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + idCounter);
      // }

      if (num > idCounter) {
        idCounter = num;
      }
      return currID;
    }
  }

  /**
   * @requires minVal != null /\ maxVal != null
   * @effects update the auto-generated value of attribute <tt>attrib</tt>,
   *          specified for <tt>derivingValue</tt>, using
   *          <tt>minVal, maxVal</tt>
   */
  @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(DAttr attrib,
      Tuple derivingValue, Object minVal, Object maxVal)
      throws ConstraintViolationException {
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

  // private static int nextID(Integer currID) {
  // if (currID == null) { // generate one
  // idCounter++;
  // return idCounter;
  // } else { // update
  // // int num = currID.intValue();
  // //
  // // if (num > idCounter)
  // // idCounter=num;
  // setIdCounter(currID);
  //
  // return currID;
  // }
  // }
  //
  // /**
  // * This method is required for loading this class metadata from storage
  // *
  // * @requires
  // * id != null
  // * @effects
  // * update <tt>idCounter</tt> from the value of <tt>id</tt>
  // */
  // public static void setIdCounter(Integer id) {
  // if (id != null) {
  // int num = id.intValue();
  //
  // if (num > idCounter)
  // idCounter=num;
  // }
  // }

  // implements Comparable interface
  public int compareTo(Object o) {
    if (o == null || (!(o instanceof Enrolment)))
      return -1;

    Enrolment e = (Enrolment) o;

    return this.student.getId().compareTo(e.student.getId());
  }
}
