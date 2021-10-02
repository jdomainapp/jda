package org.jda.example.courseman.modules.authorisation.model;

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
 *  Represents authorisations for enrolment. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(mutable=false)
public class Authorisation {

  public static final String A_student = "student";

  public static final String A_status = "status";

  public static final String A_authorDetails = "authorDetails";

  public static final String A_description = "description";

  public static final String A_enrolmentProc = "enrolmentProc";

  /**
   * @overview 
   *
   * @author Duc Minh Le (ducmle)
   *
   * @version 
   */
  public static enum AuthorzStatus {
    ACCEPTED,
    REJECTED
    ;
    
    @DAttr(name = "name", type = Type.String, length=20, id=true,auto=true,mutable=false,optional = false)
    public String getName() {
      return name();
    }
  }

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = A_student, type = Type.Domain
        //, auto=true
        , mutable=false, optional = false)
    @DAssoc(ascName = "std-has-authorisation", role = "enr", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Student student;

    @DAttr(name = "authorDetails", type = Type.String, length = 255, auto=true, mutable=false, optional = false)
    private String authorDetails;

    @DAttr(name = "description", type = Type.String, length = 255, auto=true, mutable=false)
    private String description;
    
    @DAttr(name = A_status, type = Type.Domain 
        //not supported: ,auto=true
        ,mutable=false, optional=false)
    private AuthorzStatus status;
    
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

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "student")
    public void setStudent(Student student) {
        this.student = student;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "authorisationDetails")
    public String getAuthorDetails() {
        return this.authorDetails;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "authorisationDetails")
    public void setauthorisationDetails(String authorisationDetails) {
        this.authorDetails = authorisationDetails;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "description")
    public String getDescription() {
        return this.description;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "description")
    public void setDescription(String description) {
        this.description = description;
    }

    @DOpt(type=DOpt.Type.Getter)
    @AttrRef(value="status")
    public AuthorzStatus getStatus() {
      return status;
    }

//    @DOpt(type=DOpt.Type.Setter)
//    @AttrRef(name="status")
//    public void setStatus(AuthorzStatus status) {
//      this.status = status;
//    }
    

    /**
     * @effects return statusStr
     */
    @DOpt(type=DOpt.Type.Getter)
    @AttrRef(value="statusStr")
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
    public Authorisation(Integer id, Student student, String authorisationDetails, String description, AuthorzStatus status) throws ConstraintViolationException {
        this.id = genId(id);
        this.student = student;
        this.authorDetails = authorisationDetails;
        this.description = description;
        this.status = status;
        
        this.statusStr = status.name();
    }


    /**
     * This constructor is exclusively used to execute the Payment authorisation process. 
     * All it needs to know is a Student object.
     *  
     * @effects 
     *  initialises this with {@link #student} = student
     *  executes the authorisation process 
     *  obtain the result and initialise {@link #authorDetails}, {@link #description}, {@link #status}.
     */
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Authorisation(Student student) throws ConstraintViolationException {
      this(student, null);
//        this.id = genId(null);
//        this.student = student;
//        
//        // request authorisation
//        // executes the payment process
//        Map<String, Object> result = executeEnrolAuthorisation(student);
//        
//        // initialise rest of the attributes according to the result obtained
//        if (result != null) {
//          this.authorDetails = (String) result.get(A_authorDetails);
//          this.description = (String) result.get(A_description);
//          this.status = (AuthorzStatus) result.get(A_status);
//          
//          this.statusStr = status.name();
//        }
    }
    
    
    /**
     * This constructor is exclusively used to execute the Authorisation process. 
     * It needs to know either a Student or an EnrolmentProcessing object for ({@link #enrolmentProc}) 
     *    that contains a link to a Student.
     *  
     * @effects <pre>
     *  if student neq null
     *    initialises this with {@link #student} = <tt>student</tt>
     *  else 
     *    initialises this with {@link #student} = <tt>enrolmentProc.student</tt>
     *  
     *  executes the authorisation process 
     *  obtain the result and initialise {@link #authorDetails}, {@link #description}, {@link #status}.
     *  </pre>
     */
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Authorisation(Student student, // ignored but needs to be present as student is mapped to a data field
        EnrolmentProcessing enrolmentProc) throws ConstraintViolationException {
      this.id = genId(null);
      
      if (student != null) {
        this.student = student;
      } else {
        this.student = enrolmentProc.getStudent();
        this.enrolmentProc = enrolmentProc;
      }
      
      // request authorisation
      // executes the payment process
      Map<String, Object> result = executeEnrolAuthorisation(this.student);
      
      // initialise rest of the attributes according to the result obtained
      if (result != null) {
        this.authorDetails = (String) result.get(A_authorDetails);
        this.description = (String) result.get(A_description);
        this.status = (AuthorzStatus) result.get(A_status);
        
        this.statusStr = status.name();
      }
    }
//    @DOpt(type = DOpt.Type.ObjectFormConstructor)
//    public Authorisation(Student student, String authorisationDetails, String description, AuthorzStatus status) throws ConstraintViolationException {
//        this.id = genId(null);
//        this.student = student;
//        this.authorDetails = authorisationDetails;
//        this.description = description;
//        this.status = status;
//    }

//    @DOpt(type = DOpt.Type.RequiredConstructor)
//    public Authorisation(Student student, String authorisationDetails, AuthorzStatus status) throws ConstraintViolationException {
//        this.id = genId(null);
//        this.student = student;
//        this.authorDetails = authorisationDetails;
//        this.description = null;
//        this.status=status;
//    }

    /**
     * @effects 
     *  executes the enrolment authorisation process for <tt>student</tt> 
     *  return result as {@link Map}<String,Object>, whose keys are 
     *    {{@link #A_authorDetails}, {@link #A_description}, {@link #A_status}}
     */
    private Map<String, Object> executeEnrolAuthorisation(Student student) {
      return EnrolmentAuthorisationProcess.getInstance().execute(student);
    }

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
      return "Authorisation (" + id + ", " + student + ", "
          + authorDetails + ")";
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
      Authorisation other = (Authorisation) obj;
      if (id != other.id)
        return false;
      return true;
    }
    
    
}
