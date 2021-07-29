package jda.test.model.extended;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * Represents a student class.
 * 
 * @author dmle
 *
 */
@DClass(schema="test_extended")
public class SClass {
  @DAttr(name="id",id=true,auto=true,length=6,mutable=false,type=Type.Integer)
  private int id;
  private static int idCounter;
  
  @DAttr(name="name",length=20,type=Type.String)
  private String name;
  
  @DAttr(name="createdDate",type=Type.Date)
  private Date createdDate;
  
  @DAttr(name="students",type=Type.Collection,
      serialisable=false,optional=false,
      filter=@Select(clazz=Student.class))
  @DAssoc(ascName="class-has-student",role="class",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=Student.class,
      cardMin=1,cardMax=25))  
  private List<Student> students;
  
  // derived attributes
  private int studentsCount;
  
  public SClass(String name) {
    this(null, name, null);
  }

  public SClass(String name, Date createdDate) {
    this(null, name, createdDate);
  }

  public SClass(Integer id, String name) {
    this(id, name, null);
  }
  
  public SClass(Integer id, String name, Date createdDate) {
//    this.id = nextID(id);
//    this.name = name;
//    this.createdDate = createdDate;
//    
//    students = new ArrayList();
//    studentsCount = 0;
    this(id, name, createdDate, null);
  }

  // constructor to create objects from data source
  public SClass(Integer id, String name, Date createdDate, List<Student> students) {
    this.id = nextID(id);
    this.name = name;
    this.createdDate = createdDate;
    
    if (students != null) {
      this.students = students;
      studentsCount = students.size();
    } else {
      this.students = new ArrayList<>();
      studentsCount = 0;
    }
  }

  public void setName(String name) {
    this.name = name;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="students")  
  public boolean addStudent(Student s) {
    if (!this.students.contains(s)) {
      students.add(s);
      studentsCount++;
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  //only need to do this for reflexive association: @MemberRef(name="students")  
  public boolean addStudent(List<Student> students) {
    for (Student s : students) {
      if (!this.students.contains(s)) {
        this.students.add(s);
        studentsCount++;
      }
    }
    
    // no other attributes changed
    return false; 
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  //only need to do this for reflexive association: @MemberRef(name="students")
  public boolean removeStudent(Student s) {
    students.remove(s);
    
    studentsCount--;
    
    // no other attributes changed
    return false; 
  }
  
  public void setStudents(List<Student> students) {
    this.students = students;
    
    studentsCount = students.size();
  }
    
  public String getName() {
    return name;
  }
  
  public List<Student> getStudents() {
    return students;
  }
  
  /**
   * @effects 
   *  return <tt>studentsCount</tt>
   */
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getStudentsCount() {
    return studentsCount;
  }

  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setStudentsCount(int count) {
    studentsCount = count;
  }

  public int getId() {
    return id;
  }
  
  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public String toString() {
    return "Class("+getId()+","+getName()+")";
  }
  
//  public boolean equals(Object o) {
//    if (o ==null || (!(o instanceof SClass))) {
//      return false;
//    }
//    
//    return ((SClass)o).id == this.id;
//  }

  
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
    SClass other = (SClass) obj;
    if (id != other.id)
      return false;
    return true;
  }

  private static int nextID(Integer currID) {
    if (currID == null) {
      idCounter++;
      return idCounter;
    } else {
      int num = currID.intValue();
      if (num > idCounter)
        idCounter = num;
      //setIdCounter(currID);
      
      return currID;
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

      int maxIdVal = (Integer) maxVal;
      if (maxIdVal > idCounter)  
        idCounter = maxIdVal;
    }
  }
  
//  private static int nextID(Integer currID) {
//    if (currID == null) {
//      idCounter++;
//      return idCounter;
//    } else {
////      int num = currID.intValue();
////      if (num > idCounter)
////        idCounter = num;
//      setIdCounter(currID);
//      
//      return currID;
//    }
//  }
//  
//  /**
//   * This method is required for loading this class metadata from storage 
//   * 
//   * @requires 
//   *  id != null
//   * @effects 
//   *  update <tt>idCounter</tt> from the value of <tt>id</tt>
//   */
//  public static void setIdCounter(Integer id) {
//    if (id != null) {
//      int num = id.intValue();
//      if (num > idCounter)  // extra check
//        idCounter = num;
//    }
//  }
}
