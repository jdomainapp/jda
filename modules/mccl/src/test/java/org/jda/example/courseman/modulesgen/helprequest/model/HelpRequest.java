package org.jda.example.courseman.modulesgen.helprequest.model;

import org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt;
import org.jda.example.courseman.modulesgen.student.model.Student;

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
 *  Represents help provide to {@link Student}.
 *  
 * @author Duc Minh Le (ducmle)
 */
@DClass()
public class HelpRequest {
    
    public static final String A_student = "student";
    
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1)
    private int id;

    @DAttr(name = A_student, type = Type.Domain, optional = false)
    @DAssoc(ascName = "std-has-help", role = "help", 
      ascType = AssocType.One2Many, endType = AssocEndType.Many, 
      associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Student student;

    @DAttr(name = "content", type = Type.String, length = 255)
    private String content;

    @DAttr(name="enrolmentMgmt",type=Type.Domain,serialisable=false)
    private EnrolmentMgmt enrolmentMgmt;
    
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
    @AttrRef(value = "content")
    public String getContent() {
        return this.content;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "content")
    public void setContent(String content) {
        this.content = content;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public HelpRequest(Integer id, Student student, String content) throws ConstraintViolationException {
        this.id = genId(id);
        this.student = student;
        this.content = content;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public HelpRequest(Student student, String content) throws ConstraintViolationException {
        this.id = genId(null);
        this.student = student;
        this.content = content;
    }

    @DOpt(type = DOpt.Type.RequiredConstructor)
    public HelpRequest(Student student) throws ConstraintViolationException {
        this.id = genId(null);
        this.student = student;
        this.content = null;
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
    ///// MODELGEN output:

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
      HelpRequest other = (HelpRequest) obj;
      if (id != other.id)
        return false;
      return true;
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
      return "HelpRequest (" + id + ", " + student + ")";
    }
}
