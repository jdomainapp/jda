package org.jda.example.courseman.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a student.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema="courseman")
public class Student{
  /*** STATE SPACE **/
  @DAttr(name="id",type=Type.Integer,id=true,auto=true,mutable=false,optional=false,min=1.0)
  private int id;
  
  @DAttr(name="name",type=Type.String,length=30,optional=false)
  private String name;
  
  @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
  @DAssoc(ascName="student-has-address",role="student",
    ascType=AssocType.One2One, endType=AssocEndType.One,
    associate=@Associate(type=Address.class,cardMin=1,cardMax=1))
  private Address address;

  @DAttr(name="enrolments",type=Type.Collection,optional=false,serialisable=false,
		  filter=@Select(clazz=Enrolment.class))
  @DAssoc(ascName="std-has-enrols",role="student",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Enrolment.class,cardMin=0,cardMax=30))
  private Collection<Enrolment> enrolments;  

  /*** BEHAVIOUR SPACE **/
  // static variable to keep track of student id
  private static int idCounter = 0;

  // derived
  private int enrolmentCount;
  
  // constructor methods
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public Student(String name, Address address) {
    this(null, name, address);
  }

  @DOpt(type=DOpt.Type.RequiredConstructor)
  public Student(String name) {
    this(null, name, null);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public Student(Integer id, String name, Address address) {
    // generate an id
    this.id = genId(id);
    this.name = name;
    this.address = address;
    
    this.enrolments = new ArrayList();
  }
  
  @DOpt(type=DOpt.Type.AutoAttributeValueGen)
  private static int genId(Integer currID) {
    // automatically generate the next student id
    if (currID == null) { // not specified: generate
      idCounter++;
      return idCounter;
    } else { // to update
      int num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  // setter methods
  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="name")
  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.Setter)@AttrRef(value="address")
  public void setAddress(Address address) throws ConstraintViolationException {
    this.address = address;
  }

  // v2.7.3
  @DOpt(type=DOpt.Type.LinkAdderNew)@AttrRef(value="address")
  public void setNewAddress(Address address) {
    // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
    setAddress(address);
  }
  
  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Enrolment e) {
    enrolments.add(e);
    
    enrolmentCount++;
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
  }
  
  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewEnrolment(Collection<Enrolment> enrols) {
    enrolments.addAll(enrols);
    enrolmentCount+=enrols.size();
    
    // no other attributes changed (average mark is not serialisable!!!)
    return false; 
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

  @DOpt(type=DOpt.Type.LinkRemover)
  public boolean removeEnrolment(Enrolment e) throws ConstraintViolationException {
    boolean removed = enrolments.remove(e);
    
    if (removed) {
      enrolmentCount--;
    }
    
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

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="name")
  public String getName() {
    return name;
  }

  @DOpt(type=DOpt.Type.Getter)@AttrRef(value="address")
  public Address getAddress() {
    return address;
  }

  /**
   * @effects return {@link #toString(boolean)}<tt> -> (true)</tt>
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
      return "Student(" + id + "," + name + "," + address.getCityName() + ")";
    else
      return "Student(" + id + ")";
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
    Student other = (Student) obj;
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
