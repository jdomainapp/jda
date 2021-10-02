package org.jda.example.courseman.modules.enrolment.model;

import org.jda.example.courseman.modules.authorisation.model.Authorisation;
import org.jda.example.courseman.modules.authorisation.model.Authorisation.AuthorzStatus;
import org.jda.example.courseman.modules.enrolmentmgmt.joined.model.EnrolmentProcessing;
import org.jda.example.courseman.modules.payment.model.Payment;
import org.jda.example.courseman.modules.payment.model.Payment.PaymentStatus;
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
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@DClass(serialisable=false)
public class EnrolmentApproval {

    public static final String A_student = "student";
    public static final String A_payment = "payment";
    public static final String A_authorisation = "authorisation";
    public static final String A_approved = "approved";

    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1)
    private int id;

    @DAttr(name = A_student, type = Type.Domain, optional = false)
    @DAssoc(ascName = "std-approval", role = "approve", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Student student;

    @DAttr(name = A_payment, type = Type.Domain, optional = false, mutable=false)
    @DAssoc(ascName = "payment-approval", role = "approve", ascType=AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Payment.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Payment payment;
    
    @DAttr(name = A_authorisation, type = Type.Domain, optional = false, mutable=false)
    @DAssoc(ascName = "authorisation-approval", role = "approve", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Authorisation.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Authorisation authorisation;
    
    /**derived from {@link #payment} and {@link #authorisation}*/
    @DAttr(name = A_approved, type = Type.Boolean, mutable=false)
    private boolean approved;

    @DAttr(name = "note", type = Type.String, length = 255)
    private String note;

    private static int idCounter;

    // virtual link to EnrolmentProcessing
    @DAttr(name="enrolmentProc",type=Type.Domain,serialisable=false)
    private EnrolmentProcessing enrolmentProc;

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
    @AttrRef(value = "approved")
    public boolean getApproved() {
        return this.approved;
    }

//    @DOpt(type = DOpt.Type.Setter)
//    @AttrRef(name = "approved")
//    public void setApproved(boolean approved) {
//        this.approved = approved;
//    }

    
    
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "student")
    public Student getStudent() {
        return this.student;
    }

    /**
     * @effects return payment
     */
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "payment")
    public Payment getPayment() {
      return payment;
    }

    /**
     * @effects return authorisation
     */
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "authorisation")
    public Authorisation getAuthorisation() {
      return authorisation;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "student")
    public void setStudent(Student student) {
        this.student = student;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "note")
    public String getNote() {
        return this.note;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "note")
    public void setNote(String note) {
        this.note = note;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public EnrolmentApproval(Integer id, 
        Payment payment, Authorisation authorisation, 
        Student student, Boolean approved, String note) throws ConstraintViolationException {
      this.id = genId(id);
      this.payment = payment;
      this.authorisation = authorisation;
      this.student = student;
      if (approved != null) {
        this.approved = approved;
      } else {
        this.approved = false;
      }
      this.note = note;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public EnrolmentApproval(Payment payment, Authorisation authorisation,
        Student student,Boolean approved, String note) throws ConstraintViolationException {
        this(null, payment, authorisation, student, approved, note);
    }

    @DOpt(type = DOpt.Type.RequiredConstructor)
    public EnrolmentApproval(
        Payment payment, Authorisation authorisation,
        Student student, Boolean approved) throws ConstraintViolationException {
        this(null, payment, authorisation, student, approved, null);
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

    /**
     * @effects 
     * 
     */
    public static Boolean deriveApproved(Payment payment,
        Authorisation authorisation) {
      if (payment == null || authorisation == null) {
        return Boolean.FALSE;
      } else {
        return payment.getStatus().equals(PaymentStatus.ACCEPTED) && 
            authorisation.getStatus().equals(AuthorzStatus.ACCEPTED);
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
      return "EnrolmentApproval (" + id + ", " + approved + ", " + student
          + ")";
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
      EnrolmentApproval other = (EnrolmentApproval) obj;
      if (id != other.id)
        return false;
      return true;
    }
    
    
}
