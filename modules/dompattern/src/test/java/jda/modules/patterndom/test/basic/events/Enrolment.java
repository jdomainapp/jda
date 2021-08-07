package jda.modules.patterndom.test.basic.events;

import java.io.Serializable;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.util.events.ChangeEventSource;

/**
 * Represents an enrolment
 * 
 * @author dmle
 * 
 */
@DClass(schema="test_basic")
public class Enrolment implements Comparable, Serializable, Publisher {
  static final long serialVersionUID = 2014L;
  private static int idCounter = 0;

  // attributes
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  @DAttr(name = "student", type = Type.Domain, length = 5, optional = false)
  private Student student;
  @DAttr(name = "module", type = Type.Domain, length = 5, optional = false)
  private Module module;
  @DAttr(name = "internalMark", type = Type.Double, length = 4, optional = false, min = 0.0)
  private double internalMark;
  @DAttr(name = "examMark", type = Type.Double, length = 4, optional = false, min = 0.0)
  private double examMark;

  @DAttr(name = "finalMark", type = Type.Double, length = 4, optional = false, min = 0)
  private int finalMark;

  @DAttr(name = "finalGrade", type = Type.Char,length = 1,auto = true,mutable = false, optional = false)
  private char finalGrade;
  
  // 
  /**publish/subscribe pattern: event source object that encapsulates this */
  private static ChangeEventSource evtSrc;

  // constructor method
  public Enrolment(Student s, Module m) {
    this(null, s, m, 0.0, 0.0, null);
  }

  public Enrolment(Student s, Module m, Double internalMark, Double examMark) {
    this(null, s, m, internalMark, examMark, null);
  }

  // @version 2.0
  public Enrolment(Integer id, Student s, Module m, Double internalMark,
      Double examMark, 
      // v2.7.3: not used but needed to load data from source
      Character finalGrade) throws ConstraintViolationException {
    this.id = nextID(id);
    this.student = s;
    this.module = m;
    this.internalMark = internalMark.doubleValue();
    this.examMark = examMark.doubleValue();
    this.finalGrade = genGrade(internalMark, examMark);
    
    // publish/subscribe pattern
    // register student as subscriber for add event
    addSubscriber(student, CMEventType.values());
    
    // fire OnCreated event
    notifyStateChanged(CMEventType.OnCreated, getEventSource());
  }

  // setter methods
  public void setStudent(Student s) {
    this.student = s;
  }

  public void setModule(Module m) {
    this.module = m;
  }

  public void setInternalMark(double mark) {
    this.internalMark = mark;
    
    // record old finalMark
    int oldFinalMark = finalMark;

    finalGrade = genGrade(internalMark, examMark);
    
    // publish/subscribe pattern
    notify(CMEventType.OnUpdated, getEventSource(), oldFinalMark);
  }

  public void setExamMark(double mark) {
    this.examMark = mark;

    // record old finalMark
    int oldFinalMark = finalMark;
    
    // generate final grade
    finalGrade = genGrade(internalMark, examMark);

    // publish/subscribe pattern
    notify(CMEventType.OnUpdated, getEventSource(), oldFinalMark);
  }

  // getter methods
  public int getId() {
    return id;
  }

  public Student getStudent() {
    return student;
  }

  public Module getModule() {
    return module;
  }

  public double getInternalMark() {
    return internalMark;
  }

  public double getExamMark() {
    return examMark;
  }

  public char getFinalGrade() {
    return finalGrade;
  }

  /**
   * @effects return finalMark
   */
  public int getFinalMark() {
    return finalMark;
  }

  // override toString
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

  // a method to compute the final grade
  /**
   * @modifies {@link #finalMark}
   * @effects 
   *  update {@link #finalMark} from <tt>internal, exam</tt> marks and 
   *  return a final grade letter suitable for {@link #finalMark}.
   */
  private char genGrade(double internal, double exam) {
    double finalMarkD = 0.4 * internal + 0.6 * exam;
    // round the mark to the closest integer value
    finalMark = (int) Math.round(finalMarkD);

    
    if (finalMark < 5)
      return 'F';
    else if (finalMark == 5)
      return 'P';
    else if (finalMark <= 7)
      return 'G';
    else
      return 'E';
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
//      if (num <= idCounter) {
//        throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
//            "Lỗi giá trị thuộc tính ID: {0}", num + "<=" + idCounter);
//      }
      
      if (num > idCounter) {
        idCounter=num;
      }   
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
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      } 
      // TODO add support for other attributes here 
    }
  }
  
  // implements Comparable interface
  public int compareTo(Object o) {
    if (o == null || (!(o instanceof Enrolment)))
      return -1;

    Enrolment e = (Enrolment) o;

    return this.student.getId().compareTo(e.student.getId());
  }
  

  /* (non-Javadoc)
   * @see domainapp.modules.event.Publishable#notify(domainapp.modules.event.EventType)
   */
//  /**
//   * @effects 
//   * 
//   * @version 
//   */
//  @Override
//  public void notify(EventType type, Object...data) {
//    if (evtSrc == null) evtSrc = getEventSource(Enrolment.class);
//    
//    Publisher.super.notify(type, evtSrc, data);
//  }

  @Override
  public ChangeEventSource getEventSource() {
    if (evtSrc == null) {
      evtSrc = createEventSource(Enrolment.class);
    } else {
      resetEventSource(evtSrc);
    }
    
    return evtSrc;
  }

  /**
   * @effects
   *  notify register all registered listeners 
   */
  @Override
  public void finalize() throws Throwable {
    notifyStateChanged(CMEventType.OnRemoved, getEventSource());
  }
}
