package jda.test.model.examples.courseman;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

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
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 * 
 * @author dmle
 * @version 1.0
 */
@DClass(schema="courseman")
public class Student implements Serializable {
  static final long serialVersionUID = 2012L;

  // attributes of students
  @DAttr(name = "id", id = true, type = Type.String, auto = true, length = 6, 
      mutable = false, optional = false)
  private String id;
  //static variable to keep track of student id
  private static int idCounter = 0;
 
  @DAttr(name = "name", type = Type.String, length = 30, optional = false)
  private String name;
  @DAttr(name = "dob", type = Type.String, length = 15, optional = false)
  private String dob;
  
  @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
  @DAssoc(ascName="student-has-city",role="student",
  ascType=AssocType.One2One, endType=AssocEndType.One,
  associate=@Associate(type=City.class,cardMin=1,cardMax=1))
  private City address;

  @DAttr(name = "email", type = Type.String, length = 30, optional = false)
  private String email;

  @DAttr(name="sclass",type=Type.Domain,length = 6)
  @DAssoc(ascName="class-has-student",role="student",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=SClass.class,cardMin=1,cardMax=1))
  private SClass sclass;

  @DAttr(name="enrolments",type=Type.Collection,optional = false,
      serialisable=false,filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="student-has-enrolments",role="student",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=25))
  private Collection<Enrolment> enrolments;  

  // derived
  private int enrolmentCount;

  // v2.6.4b: derived: average of the final mark of all enrolments
  private double averageMark;
  
  // constructor methods
  // for creating in the application
  // without SClass
  public Student(String name, String dob, City address, String email) {
    this(null, name, dob, address, email, null);
  }
  
  public Student(String name, String dob, City address, String email, SClass sclass) {
    this(null, name, dob, address, email, sclass, null);
  }
  
  public Student(String name, String dob, City address, String email, SClass sclass, Collection<Enrolment> enrolments) {
    this(null, name, dob, address, email, sclass, enrolments);
  }

  // the next two constructors are used for reading objects from the database.
  // this is needed because enrolments is not serialisable
  public Student(String id, String name, String dob, City address, String email, SClass sclass) {
    this(id, name, dob, address, email, sclass, null);
  }

  public Student(String id, String name, String dob, City address, String email, SClass sclass, Collection<Enrolment> enrolments) 
  throws ConstraintViolationException {
    // generate an id
    this.id = nextID(id);

    // assign other values
    this.name = name;
    this.dob = dob;
    this.address = address;
    this.email = email;
    this.sclass = sclass;
    if (enrolments != null) {
      this.enrolments = enrolments;
      enrolmentCount = enrolments.size();
      computeAverageMark();
    } else {
      this.enrolments = new ArrayList();
      enrolmentCount = 0;
      averageMark = 0D;
    }
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
    enrolmentCount++;
    
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

  public Collection<Enrolment> getEnrolments() {
    return enrolments;
  }
  
  public void setEnrolments(Collection<Enrolment> en) {
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

  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getEnrolmentsCount() {
    return enrolmentCount;
    //return enrolments.size();
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setEnrolmentsCount(int count) {
    enrolmentCount = count;
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

  public SClass getSclass() {
    return sclass;
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
          + email + ((sclass != null) ? "," + sclass.getName() : "") + ")";
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
            ConstraintViolationException.Code.INVALID_VALUE, e,
            "Lỗi giá trị thuộc tính ID: {0}", id);
      }
      
      if (num > idCounter) {
        idCounter = num;
      }
      
      return id;
    }
  }

//  /**
//   * This method is required for loading this class metadata from storage 
//   * 
//   * @requires 
//   *  id != null
//   * @effects 
//   *  update <tt>idCounter</tt> from the value of <tt>id</tt>
//   */
//  public static void setCurrNum(String id) throws ConstraintViolationException {
//    if (id != null) {
//      try {
//        int num = Integer.parseInt(id.substring(1));
//        
//        if (num > idCounter) // extra check
//          idCounter = num;
//        
//      } catch (RuntimeException e) {
//        throw new ConstraintViolationException(
//            ConstraintViolationException.Code.INVALID_VALUE, e, 
//            "Lỗi giá trị thuộc tính ID: {0}", id);
//      }
//    }
//  }
  
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
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Lỗi giá trị thuộc tính ID: {0}", maxId);
      }
    }
  }
  
  // overrides default serializable/deserializable methods to handle static
  // constant
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeInt(this.idCounter);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException,
      ClassNotFoundException {
    in.defaultReadObject();
    int n = in.readInt();
    this.idCounter = n;
  }
}
