package org.jda.example.courseman.bspacegen.output;

import java.util.*;

import org.jda.example.courseman.bspacegen.output.CourseModule;
import org.jda.example.courseman.bspacegen.output.Student;

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
 * Represents an enrolment
 * 
 * @author dmle
 * 
 */
@DClass(schema = "courseman")
public class Enrolment {

    /*** STATE SPACE **/
    // attributes
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, optional = false, mutable = false, min = 1)
    private int id;

    @DAttr(name = "student", type = Type.Domain, optional = false)
    @DAssoc(ascName = "std-has-enrols", role = "enrolment", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private Student student;

    @DAttr(name = "module", type = Type.Domain, optional = false)
    @DAssoc(ascName = "mod-has-enrols", role = "enrolment", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = CourseModule.class, cardMin = 1, cardMax = 1), dependsOn = true)
    private CourseModule module;

    @DAttr(name = "internalMark", type = Type.Double, optional = true, min = 0.0, max = 10.0)
    private Double internalMark;

    @DAttr(name = "examMark", type = Type.Double, optional = true, min = 0.0, max = 10.0)
    private Double examMark;

    // v2.6.4.b derived from two attributes
    @DAttr(name = "finalMark", type = Type.Integer, auto = true, mutable = false, optional = true, serialisable = false, derivedFrom = { "internalMark", "examMark" })
    private Integer finalMark;

    @DAttr(name = "finalGrade", type = Type.Char, auto = true, mutable = false, optional = true)
    private Character finalGrade;

    /*** BEHAVIOUR SPACE **/
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
    @AttrRef(value = "module")
    public CourseModule getModule() {
        return this.module;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "module")
    public void setModule(CourseModule module) {
        this.module = module;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "internalMark")
    public Double getInternalMark() {
        return this.internalMark;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "internalMark")
    public void setInternalMark(Double internalMark) {
        this.internalMark = internalMark;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "examMark")
    public Double getExamMark() {
        return this.examMark;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "examMark")
    public void setExamMark(Double examMark) {
        this.examMark = examMark;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "finalMark")
    public Integer getFinalMark() {
        return this.finalMark;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
    @AttrRef(value = "finalMark")
    private static Integer genFinalMark(Integer finalMark, Double internalMark, Double examMark) {
        //TODO: implement this 
        return null;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "finalGrade")
    public Character getFinalGrade() {
        return this.finalGrade;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
    @AttrRef(value = "finalGrade")
    private static Character genFinalGrade(Character finalGrade) {
        //TODO: implement this 
        return null;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public Enrolment(Integer id, Student student, CourseModule module, Double internalMark, Double examMark, Character finalGrade) throws ConstraintViolationException {
        this.id = genId(id);
        this.student = student;
        this.module = module;
        this.internalMark = internalMark;
        this.examMark = examMark;
        this.finalMark = genFinalMark(null, internalMark, examMark);
        this.finalGrade = genFinalGrade(finalGrade);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Enrolment(Student student, CourseModule module, Double internalMark, Double examMark) throws ConstraintViolationException {
        this.id = genId(null);
        this.student = student;
        this.module = module;
        this.internalMark = internalMark;
        this.examMark = examMark;
        this.finalMark = genFinalMark(null, internalMark, examMark);
        this.finalGrade = genFinalGrade(null);
    }

    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Enrolment(Student student, CourseModule module) throws ConstraintViolationException {
        this.id = genId(null);
        this.student = student;
        this.module = module;
        this.internalMark = null;
        this.examMark = null;
        this.finalMark = genFinalMark(null, null, null);
        this.finalGrade = genFinalGrade(null);
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals("id")) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        } else if (attribName.equals("finalGrade")) {
        //TODO: implement this 
        }
    }
}
