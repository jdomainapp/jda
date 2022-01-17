package org.jda.example.courseman.services.student.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.jda.example.courseman.exceptions.DExCode;
import org.jda.example.courseman.services.enrolment.model.Enrolment;
import org.jda.example.courseman.services.sclass.model.SClass;
import org.jda.example.courseman.services.student.reports.StudentsByCityJoinReport;
import org.jda.example.courseman.services.student.reports.StudentsByNameReport;
import org.jda.example.courseman.utils.DToolkit;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public class Student {
  public static final String A_name = "name";
  public static final String A_gender = "gender";
  public static final String A_id = "id";
  public static final String A_dob = "dob";
  public static final String A_address = "address";
  public static final String A_email = "email";
  public static final String A_sclass = "sclass";
  public static final String A_rptStudentByName = "rptStudentByName";
  public static final String A_rptStudentByCity = "rptStudentByCity";

  // attributes of students
  @DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6, 
      mutable = false, optional = false)
  private String id;
  //static variable to keep track of student id
  private static int idCounter = 0;
 
  @DAttr(name = A_name, type = Type.String, length = 30, optional = false, cid=true)
  private String name;

  @DAttr(name = A_gender, type = Type.Domain, length = 10, optional = false)
  private Gender gender;

  @DAttr(name = A_dob, type = Type.Date, length = 15, optional = false
      ,format=Format.Date
      )
  private Date dob;
  
  @DAttr(name = A_address, type = Type.Domain, length = 20, optional = true)
  @DAssoc(ascName="student-has-city",role="student",
      ascType=AssocType.One2One, endType=AssocEndType.One,
  associate=@Associate(type=City.class,cardMin=1,cardMax=1))
  private City address;

  @DAttr(name = A_email, type = Type.String, length = 30, optional = false)
  private String email;

  @DAttr(name=A_sclass,type=Type.Domain,length = 6)
  @DAssoc(ascName="class-has-student",role="student",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=SClass.class,cardMin=1,cardMax=1))
  private SClass sclass;

  @DAttr(name="enrolments",type=Type.Collection,optional = false,
      serialisable=false,
      filter=@Select(clazz=Enrolment.class,
      attributes={"id", "student", "module"}))
  @DAssoc(ascName="student-has-enrolments",role="student",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=30))
  private Collection<Enrolment> enrolments;  

  // derived
  private int enrolmentCount;

  // v2.6.4b: derived: average of the final mark of all enrolments
  private double averageMark;
  
  // v5.3: to realise link to report
  @DAttr(name=A_rptStudentByName,type=Type.Domain, serialisable=false, 
      // IMPORTANT: set virtual=true to exclude this attribute from the object state
      // (avoiding the view having to load this attribute's value from data source)
      virtual=true)
  private StudentsByNameReport rptStudentByName;
  
  // v5.0: to realise link to report
  @DAttr(name=A_rptStudentByCity,type=Type.Domain, serialisable=false, 
      // IMPORTANT: set virtual=true to exclude this attribute from the object state
      // (avoiding the view having to load this attribute's value from data source)
      virtual=true)
  private StudentsByCityJoinReport rptStudentByCity;
  
  // constructor methods
  // for creating in the application
  // without SClass
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Student(@AttrRef("name") String name, 
      @AttrRef("gender") Gender gender,
      @AttrRef("dob") Date dob, 
      @AttrRef("address") City address, 
      @AttrRef("email") String email) {
    this(null, name, gender, dob, address, email, null);
  }
  
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Student(@AttrRef("name") String name, 
      @AttrRef("gender") Gender gender,
      @AttrRef("dob") Date dob, 
      @AttrRef("address") City address, 
      @AttrRef("email") String email, 
      @AttrRef("sclass") SClass sclass) {
    this(null, name, gender, dob, address, email, sclass);
  }
  
  // a shared constructor that is invoked by other constructors
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Student(@AttrRef("id") String id, 
      @AttrRef("dob") String name, @AttrRef("gender") Gender gender,
      @AttrRef("dob") Date dob, @AttrRef("address") City address, 
      @AttrRef("email") String email, @AttrRef("sclass") SClass sclass) 
  throws ConstraintViolationException {
    // generate an id
    this.id = nextID(id);

    // assign other values
    this.name = name;
    this.gender = gender;
    this.dob = dob;
    this.address = address;
    this.email = email;
    this.sclass = sclass;
    
    enrolments = new ArrayList<>();
    enrolmentCount = 0;
    averageMark = 0D;
  }

  // setter methods
  public void setName(String name) {
    this.name = name;
  }

  public void setDob(Date dob) throws ConstraintViolationException {
    // additional validation on dob
    if (dob.before(DToolkit.MIN_DOB)) {
      throw new ConstraintViolationException(DExCode.INVALID_DOB, dob);
    }
    
    this.dob = dob;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public void setAddress(City address) {
    this.address = address;
  }

  // v2.7.3
  public void setNewAddress(City address) {
    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
    setAddress(address);
  }
  
  public void setEmail(String email) throws ConstraintViolationException {
    if (email.indexOf("@") < 0) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          new Object[] {"'" + email + "' (does not have '@') "});
    }
    this.email = email;
  }

  public void setSclass(SClass cls) {
    this.sclass = cls;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="enrolments")
  public boolean addEnrolment(Enrolment e) {
    if (!enrolments.contains(e))
      enrolments.add(e);
    
    // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
    // otherwise computeAverageMark (below) can not be performed correctly
    // WHY? average mark is not serialisable
//    enrolmentCount++;
//    
//    // v2.6.4.b
//    computeAverageMark();
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Enrolment e) {
    enrolments.add(e);
    
    enrolmentCount++;
    
    // v2.6.4.b
    computeAverageMark();
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  //@MemberRef(name="enrolments")
  public boolean addEnrolment(Collection<Enrolment> enrols) {
    boolean added = false;
    for (Enrolment e : enrols) {
      if (!enrolments.contains(e)) {
        if (!added) added = true;
        enrolments.add(e);
      }
    }
    // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
    // otherwise computeAverageMark (below) can not be performed correctly
    // WHY? average mark is not serialisable
//    enrolmentCount += enrols.size();

//    if (added) {
//      // avg mark is not serialisable so we need to compute it here
//      computeAverageMark();
//    }

    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Collection<Enrolment> enrols) {
    enrolments.addAll(enrols);
    enrolmentCount+=enrols.size();
    
    // v2.6.4.b
    computeAverageMark();

    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  //@MemberRef(name="enrolments")
  public boolean removeEnrolment(Enrolment e) {
    boolean removed = enrolments.remove(e);
    
    if (removed) {
      enrolmentCount--;
      
      // v2.6.4.b
      computeAverageMark();
    }
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkUpdater)
  //@MemberRef(name="enrolments")
  public boolean updateEnrolment(Enrolment e)  throws IllegalStateException {
    // recompute using just the affected enrolment
    double totalMark = averageMark * enrolmentCount;
    
    int oldFinalMark = e.getFinalMark(true);
    
    int diff = e.getFinalMark() - oldFinalMark;
    
    // TODO: cache totalMark if needed 
    
    totalMark += diff;
    
    averageMark = totalMark / enrolmentCount;
    
    // no other attributes changed
    return true; 
  }

  public void setEnrolments(Collection<Enrolment> en) {
    this.enrolments = en;
    enrolmentCount = en.size();
    
    // v2.6.4.b
    computeAverageMark();
  }
  
  // v2.6.4.b
  /**
   * @effects 
   *  computes {@link #averageMark} of all the {@link Enrolment#getFinalMark()}s 
   *  (in {@link #enrolments}.  
   */
  private void computeAverageMark() {
    if (enrolmentCount > 0) {
      double totalMark = 0d;
      for (Enrolment e : enrolments) {
        totalMark += e.getFinalMark();
      }
      
      averageMark = totalMark / enrolmentCount;
    } else {
      averageMark = 0;
    }
  }
  
  // v2.6.4.b
  public double getAverageMark() {
    return averageMark;
  }
  
  // getter methods
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Gender getGender() {
    return gender;
  }
  
  public Date getDob() {
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
  
  public Collection<Enrolment> getEnrolments() {
    return enrolments;
  }

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getEnrolmentsCount() {
    return enrolmentCount;
    //return enrolments.size();
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setEnrolmentsCount(int count) {
    enrolmentCount = count;
  }

  /**
   * @effects return rptStudentByName
   */
  public StudentsByNameReport getRptStudentByName() {
    return rptStudentByName;
  }

  /**
   * @effects return rptStudentByCity
   */
  public StudentsByCityJoinReport getRptStudentByCity() {
    return rptStudentByCity;
  }
  
  // override toString
  /**
   * @effects returns <code>this.id</code>
   */
  @Override
  public String toString() {
    return toString(true);
  }

  /**
   * @effects returns <code>Student(id,name,dob,address,email)</code>.
   */
  public String toString(boolean full) {
    if (full)
      return "Student(" + id + "," + name + "," + gender + ", " + dob + "," + address + ","
          + email + ((sclass != null) ? "," + sclass.getName() : "") + ")";
    else
      return "Student(" + id + ")";
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    Student other = (Student) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
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
            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
      }
      
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
            ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
      }
    }
  }
}
