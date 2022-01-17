package vn.com.courseman.it1.model;

import jda.basics.exceptions.ConstraintViolationException;
import jda.basics.model.meta.AttrRef;
import jda.basics.model.meta.DAssoc;
import jda.basics.model.meta.DAssoc.AssocEndType;
import jda.basics.model.meta.DAssoc.AssocType;
import jda.basics.model.meta.DAssoc.Associate;
import jda.basics.model.meta.DAttr;
import jda.basics.model.meta.DAttr.Type;
import jda.basics.model.meta.DClass;
import jda.basics.model.meta.DOpt;
import jda.basics.util.Tuple;

/**
 * A domain class whose objects are city names. This class is used as 
 * the <code>allowedValues</code> of the domain attributes of 
 * other domain classes (e.g. Student.address).  
 * 
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to 
 * return the string representation of a city name which is expected by 
 * the application. 
 * 
 * @author dmle
 *
 */
@DClass(schema="courseman")
public class City {
  
  @DAttr(name="id",id=true,auto=true,length=3,mutable=false,optional=false,type=Type.Integer)
  private int id;
  private static int idCounter;
  
  @DAttr(name="name",type=Type.String,length=20,optional=false)
  private String name;
  
  @DAttr(name="student",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="student-has-city",role="city",
  ascType=AssocType.One2One, endType=AssocEndType.One,
  associate=@Associate(type=Student.class,cardMin=1,cardMax=1,determinant=true))
  private Student student;

  // from object form: Student is not included 
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  @DOpt(type=DOpt.Type.RequiredConstructor)
  public City(@AttrRef("name") String cityName) {
    this(null, cityName, null);
  }

  // from object form: Student is included
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public City(@AttrRef("name") String cityName, @AttrRef("student") Student student) {
    this(null, cityName, student);
  }

  // from data source
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public City(@AttrRef("id") Integer id, @AttrRef("name") String cityName) {
    this(id, cityName, null);
  }
  
  // based constructor (used by others)
  private City(Integer id, String cityName, Student student) {
    this.id = nextId(id);
    this.name = cityName;
    this.student = student;
  }
  
  private static int nextId(Integer currID) {
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
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public Student getStudent() {
    return student;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
  public void setNewStudent(Student student) {
    this.student = student;
    // do other updates here (if needed)
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  @Override
  public String toString() {
    return name;
  }
}
