package org.jda.example.courseman.modules.payment.model;

import java.util.Map;

import org.jda.example.courseman.modules.enrolmentmgmt.forked.model.control.FEnrolmentProcessing;
import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.EnrolmentProcessing;
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
 *  Represents payment for intuition fee. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(mutable=false)
public class Payment {

  /**
   * @overview 
   *
   * @author Duc Minh Le (ducmle)
   *
   * @version 
   */
  public static enum PaymentStatus {
    ACCEPTED,
    REJECTED,
    ;
    
    @DAttr(name = "name", type = Type.String, length=20, id=true,auto=true,mutable=false,optional = false)
    public String getName() {
      return name();
    }
  }

  public static final String A_student = "student";

  public static final String A_status = "status";

  public static final String A_payDetails = "payDetails";

  public static final String A_description = "description";
  
  public static final String A_enrolmentProc = "enrolmentProc";
  
    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = A_student, type = Type.Domain
        //, auto=true
        , mutable=false, optional = false)
    @DAssoc(ascName = "std-has-payment", role = "enr", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Student student;

    @DAttr(name = A_payDetails, type = Type.String, length = 255, auto=true, mutable=false)
    private String payDetails;

    @DAttr(name = A_description, type = Type.String, length = 255, auto=true, mutable=false)
    private String description;

    @DAttr(name = A_status, type = Type.Domain
        // not supported for Domain-typed attribute: auto=true
        , mutable=false
        )
    private PaymentStatus status;

    /***
     * derived from {@link #status}
     */
    @DAttr(name = "statusStr", type = Type.String, length=20, auto=true, mutable=false, serialisable=false)
    private String statusStr;
    
    // virtual link to FEnrolmentProcessing
    @DAttr(name="fEnrolmentProc",type=Type.Domain,serialisable=false)
    private FEnrolmentProcessing fEnrolmentProc;
    
    // virtual link to EnrolmentProcessing
    @DAttr(name=A_enrolmentProc,type=Type.Domain,serialisable=false)
    private EnrolmentProcessing enrolmentProc;
    
    /*** END: state space**/
    
    private static int idCounter;

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "id")
    public int getId() {
        return this.id;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
    @AttrRef(value = "id")
    private static int genId(Integer id) {
        Integer val;
        if (id == null) {
            idCounter++;
            val = idCounter;
        } else {
            if (id > idCounter) {
                idCounter = id;
            }
            val = id;
        }
        return val;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "student")
    public Student getStudent() {
        return this.student;
    }

//    @DOpt(type = DOpt.Type.Setter)
//    @AttrRef(name = "student")
//    public void setStudent(Student student) {
//        this.student = student;
//    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "paymentDetails")
    public String getPayDetails() {
        return this.payDetails;
    }

//    @DOpt(type = DOpt.Type.Setter)
//    @AttrRef(name = "paymentDetails")
//    public void setPayDetails(String paymentDetails) {
//        this.payDetails = paymentDetails;
//    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "description")
    public String getDescription() {
        return this.description;
    }

//    @DOpt(type = DOpt.Type.Setter)
//    @AttrRef(name = "description")
//    public void setDescription(String description) {
//        this.description = description;
//    }

    /**
     * @effects 
     * 
     * @version 
     * 
     */
    @DOpt(type=DOpt.Type.Getter)
    @AttrRef(value="status")
    public PaymentStatus getStatus() {
      return status;
    }

//    @DOpt(type=DOpt.Type.Setter)
//    @AttrRef(name="status")
//    public void setStatus(PaymentStatus status) {
//      this.status = status;
//      
//    }
    
    
    /**
     * @effects return statusStr
     */
    public String getStatusStr() {
      return statusStr;
    }

    
    /**
     * @effects return enrolmentProc
     */
    public EnrolmentProcessing getEnrolmentProc() {
      return enrolmentProc;
    }

    /**
     * @effects set enrolmentProc = enrolmentProc
     */
    public void setEnrolmentProc(EnrolmentProcessing enrolmentProc) {
      this.enrolmentProc = enrolmentProc;
    }
    
    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public Payment(Integer id, Student student, String paymentDetails, String description, PaymentStatus status) throws ConstraintViolationException {
        this.id = genId(id);
        this.student = student;
        this.payDetails = paymentDetails;
        this.description = description;
        this.status = status;
        
        this.statusStr = status.name();
    }

    /**
     * This constructor is exclusively used to execute the Payment process. 
     * All it needs to know is a Student object.
     *  
     * @effects 
     *  initialises this with {@link #student} = student
     *  executes the payment process 
     *  obtain the result and initialise {@link #payDetails}, {@link #description}, {@link #status}.
     */
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    //@DOpt(type = DOpt.Type.RequiredConstructor)
    public Payment(Student student) throws ConstraintViolationException {
      this.id = genId(null);
      this.student = student;
      
      // executes the payment process
      Map<String, Object> result = executeEnrolPayment(student);
      
      // initialise rest of the attributes according to the result obtained
      if (result != null) {
        this.payDetails = (String) result.get(A_payDetails);
        this.description = (String) result.get(A_description);
        this.status = (PaymentStatus) result.get(A_status);
        
        this.statusStr = status.name();
      }
    }
    
    /**
     * This constructor is exclusively used to execute the Payment process. 
     * All it needs to know is either a Student or 
     * an EnrolmentProcessing object for ({@link #enrolmentProc}) that contains a link to a Student.
     *  
     * @effects <pre>
     *  if student is neq null
     *    initialises this with {@link #student} = <tt>student</tt>
     *  else 
     *    initialises this with {@link #student} = <tt>enrolmentProc.student</tt>
     *    
     *  executes the payment process 
     *  obtain the result and initialise {@link #payDetails}, {@link #description}, {@link #status}.
     *  </pre>
     */
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Payment(Student student, // ignored but needs to be present as student is mapped to a data field
        EnrolmentProcessing enrolmentProc) throws ConstraintViolationException {
      this.id = genId(null);
      
      if (student != null) {
        this.student = student;
      } else {
        this.student = enrolmentProc.getStudent();
        this.enrolmentProc = enrolmentProc;
      }
      
      // executes the payment process
      Map<String, Object> result = executeEnrolPayment(this.student);
      
      // initialise rest of the attributes according to the result obtained
      if (result != null) {
        this.payDetails = (String) result.get(A_payDetails);
        this.description = (String) result.get(A_description);
        this.status = (PaymentStatus) result.get(A_status);
        
        this.statusStr = status.name();
      }
    }
    
    /**
     * @effects 
     *  executes the enrolment payment process for <tt>student</tt> 
     *  return result as {@link Map}<String,Object>, whose keys are 
     *    {{@link #A_payDetails}, {@link #A_description}, {@link #A_status}}
     */
    private Map<String, Object> executeEnrolPayment(Student student) {
      return EnrolmentPaymentProcess.getInstance().execute(student);
    }

//    @DOpt(type = DOpt.Type.ObjectFormConstructor)
//    public Payment(Student student, String paymentDetails, String description, PaymentStatus status) throws ConstraintViolationException {
//        this.id = genId(null);
//        this.student = student;
//        this.payDetails = paymentDetails;
//        this.description = description;
//        this.status = status;
//    }

//    @DOpt(type = DOpt.Type.RequiredConstructor)
//    public Payment(Student student, String paymentDetails, PaymentStatus status) throws ConstraintViolationException {
//        this.id = genId(null);
//        this.student = student;
//        this.payDetails = paymentDetails;
//        this.description = null;
//        this.status = status;
//    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals("id")) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
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
      return "Payment (" + id + ", " + student + ", " + payDetails + ")";
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
      Payment other = (Payment) obj;
      if (id != other.id)
        return false;
      return true;
    }
}
