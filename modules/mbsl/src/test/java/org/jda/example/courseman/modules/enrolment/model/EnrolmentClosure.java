package org.jda.example.courseman.modules.enrolment.model;

import java.util.Date;

import org.jda.example.courseman.modules.enrolmentmgmt.merged.model.EnrolmentMgmt;
import org.jda.example.courseman.modules.orientation.model.Orientation;
import org.jda.example.courseman.modules.sclassregist.model.SClassRegistration;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represents enrolment closures.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class EnrolmentClosure {

    public static final String A_sclassRegist = "sclassRegist";
    public static final String A_orient = "orient";

    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1)
    private int id;

    @DAttr(name = "closureDate", type = Type.Date, mutable = false, optional = false,
        format=Format.Date, defaultValueFunction=true)
    private Date closureDate;
    
    @DAttr(name = A_sclassRegist, type = Type.Domain)
    private SClassRegistration sclassRegist;

    @DAttr(name = A_orient, type = Type.Domain)
    private Orientation orient;

    @DAttr(name = "note", type = Type.String, length = 255)
    private String note;

    private static int idCounter;

    // virtual link to EnrolmentMgmt (merged)
    @DAttr(name="enrolmentMgmt",type=Type.Domain,serialisable=false)
    private EnrolmentMgmt enrolmentMgmt;

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

    /**
     * @effects return closureDate
     */
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "closureDate")
    public Date getClosureDate() {
      return closureDate;
    }

    /**
     * @effects set closureDate = closureDate
     */
    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "closureDate")
    public void setClosureDate(Date closureDate) {
      this.closureDate = closureDate;
    }

    @DOpt(type = DOpt.Type.DefaultValueFunction)
    @AttrRef(value = "closureDate")
    public static Date getDefaultClosureDate() {
      return Toolkit.getCurrentDateTime();
    }
    
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "sclassRegist")
    public SClassRegistration getSclassRegist() {
        return this.sclassRegist;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "sclassRegist")
    public void setSclassRegist(SClassRegistration sclassRegist) {
        this.sclassRegist = sclassRegist;
    }
    
    /**
     * @effects return orient
     */
    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = A_orient)
    public Orientation getOrient() {
      return orient;
    }

    /**
     * @effects set orient = orient
     */
    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = A_orient)
    public void setOrient(Orientation orient) {
      this.orient = orient;
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
    public EnrolmentClosure(Integer id, 
        Date closureDate,
        SClassRegistration sclassRegist, Orientation orient, String note) throws ConstraintViolationException {
      this.id = genId(id);
      this.closureDate = closureDate;
      this.sclassRegist = sclassRegist;
      this.orient = orient;
      this.note = note;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public EnrolmentClosure(
        Date closureDate,
        SClassRegistration sclassRegist, Orientation orient, String note) throws ConstraintViolationException {
        this(null, closureDate, sclassRegist, orient, note);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public EnrolmentClosure(
        Date closureDate,
        String note) throws ConstraintViolationException {
        this(null, closureDate, null, null, note);
    }

    @DOpt(type = DOpt.Type.RequiredConstructor)
    public EnrolmentClosure(Date closureDate) throws ConstraintViolationException {
        this(null, closureDate, null, null, null);
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
      return "EnrolmentClosure (" + id + ", " + closureDate + ")";
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
      EnrolmentClosure other = (EnrolmentClosure) obj;
      if (id != other.id)
        return false;
      return true;
    }
    
    
}
