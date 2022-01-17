package vn.com.courseman.it3.model;

import java.util.Calendar;
import java.util.List;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.util.Tuple;


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
  public static final String A_id = "id";
  public static final String A_dob = "dob";
  public static final String A_address = "address";
  public static final String A_email = "email";

  // attributes of students
  @DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6, 
      mutable = false, optional = false)
  private String id;
  //static variable to keep track of student id
  private static int idCounter = 0;
 
  @DAttr(name = A_name, type = Type.String, length = 30, optional = false)
  private String name;
  
  @DAttr(name = A_dob, type = Type.String, length = 15, optional = false)
  private String dob;
  
  @DAttr(name = A_address, type = Type.Domain, length = 20, optional = true)
  @DAssoc(ascName="student-has-city",role="student",
      ascType=AssocType.One2One, endType=AssocEndType.One,
  associate=@Associate(type=City.class,cardMin=1,cardMax=1))
  private City address;

  @DAttr(name = A_email, type = Type.String, length = 30, optional = false)
  private String email;

  @DAttr(name="enrolments",type=Type.Collection,optional = false,
      serialisable=false,filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="student-has-enrolments",role="student",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=25))
  private List<Enrolment> enrolments;  

  // derived
  private int enrolmentCount;

  // v2.6.4b: derived: average of the final mark of all enrolments
  private double averageMark;
  
  // constructor methods
  // for creating in the application
  // without SClass
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Student(@AttrRef("name") String name, 
      @AttrRef("dob") String dob, 
      @AttrRef("address") City address, 
      @AttrRef("email") String email) throws ConstraintViolationException {
    this(null, name, dob, address, email);
  }

  // a shared constructor that is invoked by other constructors
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Student(String id, String name, String dob, City address, String email) 
  throws ConstraintViolationException {
    // generate an id
    this.id = nextID(id);

    // assign other values
    this.name = name;
    this.dob = dob;
    this.address = address;
    this.email = email;
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

  // v2.7.3
  public void setNewAddress(City address) {
    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
    setAddress(address);
  }
  
  public void setEmail(String email) {
    this.email = email;
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
  public boolean addEnrolment(List<Enrolment> enrols) {
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
  public boolean addNewEnrolment(List<Enrolment> enrols) {
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

  public void setEnrolments(List<Enrolment> en) {
    this.enrolments = en;
    enrolmentCount = en.size();
    
    // v2.6.4.b
    computeAverageMark();
  }
  
  // v2.6.4.b
  private void computeAverageMark() {
    double totalMark = 0d;
    for (Enrolment e : enrolments) {
      totalMark += e.getFinalMark();
    }
    
    averageMark = totalMark / enrolmentCount;
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

  public String getDob() {
    return dob;
  }

  public City getAddress() {
    return address;
  }

  public String getEmail() {
    return email;
  }
  
  public List<Enrolment> getEnrolments() {
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
      return "Student(" + id + "," + name + "," + dob + "," + address + ","
          + email + ")";
    else
      return "Student(" + id + ")";
  }

//  public boolean equals(Object o) {
//    if (o == null)
//      return false;
//
//    if (!(o instanceof Student))
//      return false;
//
//    Student s = (Student) o;
//
//    return (s.id.equals(this.id));
//  }

  
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
