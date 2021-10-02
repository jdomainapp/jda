package org.jda.example.courseman.modules.sclassregist.model;

import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.control.MgEnrolmentProcessing;
import org.jda.example.courseman.modules.sclass.model.SClass;
import org.jda.example.courseman.modules.student.model.Student;

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
 * @overview 
 *  Represents a student-sclass registration.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass()
public class SClassRegistration {
  public static final String A_student = "student";
  
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,optional=false,mutable=false,min=1)
  private int id;
  private static int idCounter = 0;

  @DAttr(name = A_student, type = Type.Domain, optional = false)
  @DAssoc(ascName = "M1-assoc-I", role = "r3", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private Student student;

  @DAttr(name = "sClass", type = Type.Domain, optional = false)
  @DAssoc(ascName = "M2-assoc-I", role = "r3", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = SClass.class, cardMin = 1, cardMax = 1), dependsOn = true)
  private SClass sClass;
  
  // virtual link to EnrolmentMgmt (sequential)
  @DAttr(name="enrolmentMgmt1",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.sequential.model.EnrolmentMgmt enrolmentMgmt1;
  
  // virtual link to EnrolmentMgmt (decisional)
  @DAttr(name="enrolmentMgmt2",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.EnrolmentMgmt enrolmentMgmt2;

  // virtual link to EnrolmentMgmt (forked)
  @DAttr(name="enrolmentMgmt3",type=Type.Domain,serialisable=false,virtual=true)
  private org.jda.example.courseman.modules.enrolmentmgmt.forkedandjoined.model.EnrolmentMgmt enrolmentMgmt3;

  // virtual link to MgEnrolmentProcessing
  @DAttr(name="enrolmentProc",type=Type.Domain,serialisable=false,virtual=true)
  private MgEnrolmentProcessing enrolmentProc;
 
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public SClassRegistration(Student student, SClass sClass) {
    this(null, student, sClass);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public SClassRegistration(Integer id, Student student, SClass sClass) {
    this.id = nextID(id);
    this.student = student;
    this.sClass = sClass;
  }

  // setter methods
  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value = A_student)
  public void setStudent(Student student) {
      this.student = student;
  }

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = A_student)
  public Student getStudent() {
      return student;
  }

  @DOpt(type = DOpt.Type.Setter)
  @AttrRef(value = "sClass")
  public void setSClass(SClass sClass) {
      this.sClass = sClass;
  }

  @DOpt(type = DOpt.Type.Getter)
  @AttrRef(value = "sClass")
  public SClass getSClass() {
      return sClass;
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

  /**
   * @effects return id
   */
  public int getId() {
    return id;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "SClassRegistration (" + id + ", " + student + ", " + sClass + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SClassRegistration other = (SClassRegistration) obj;
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
