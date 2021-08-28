package org.jda.example.courseman.bspacegen.output;

import java.util.Collection;

import org.jda.example.courseman.bspacegen.output.Enrolment;

import java.util.*;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class CourseModule {

    /*** STATE SPACE **/
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1)
    private int id;

    @DAttr(name = "code", type = Type.String, length = 6, auto = true, mutable = false, optional = false, derivedFrom = { "semester" })
    private String code;

    @DAttr(name = "name", type = Type.String, length = 30, optional = false)
    private String name;

    @DAttr(name = "semester", type = Type.Integer, optional = false, min = 1, max = 10)
    private int semester;

    @DAttr(name = "credits", type = Type.Integer, optional = false, min = 1, max = 5)
    private int credits;

    // v2.6.4b: added support for this association
    @DAttr(name = "enrolments", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Enrolment.class))
    @DAssoc(ascName = "mod-has-enrols", role = "module", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Enrolment.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<Enrolment> enrolments;

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
    @AttrRef(value = "code")
    public String getCode() {
        return this.code;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
    @AttrRef(value = "code")
    private static String genCode(String code, Integer semester) {
        //TODO: implement this 
        return null;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "name")
    public String getName() {
        return this.name;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "name")
    public void setName(String name) {
        this.name = name;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "semester")
    public int getSemester() {
        return this.semester;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "semester")
    public void setSemester(int semester) {
        this.semester = semester;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "credits")
    public int getCredits() {
        return this.credits;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "credits")
    public void setCredits(int credits) {
        this.credits = credits;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "enrolments")
    public Collection<Enrolment> getEnrolments() {
        return this.enrolments;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "enrolments")
    public void setEnrolments(Collection<Enrolment> enrolments) {
        this.enrolments = enrolments;
    }

    private int enrolmentsCount;

    @DOpt(type = DOpt.Type.LinkAdder)
    @AttrRef(value = "enrolments")
    public boolean addEnrolments(Enrolment obj) {
        if (!enrolments.contains(obj)) {
            enrolments.add(obj);
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder)
    @AttrRef(value = "enrolments")
    public boolean addEnrolments(Collection<Enrolment> obj) {
        for (Enrolment o : obj) {
            if (!enrolments.contains(o)) {
                enrolments.add(o);
                enrolmentsCount++;
            }
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "enrolments")
    public boolean addNewEnrolments(Enrolment obj) {
        enrolments.add(obj);
        enrolmentsCount++;
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    @AttrRef(value = "enrolments")
    public boolean onUpdateEnrolments(Enrolment obj) {
        // TODO: implement this 
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover)
    @AttrRef(value = "enrolments")
    public boolean removeEnrolments(Enrolment obj) {
        boolean removed = enrolments.remove(obj);
        if (removed)
            enrolmentsCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkCountGetter)
    @AttrRef(value = "enrolmentsCount")
    public Integer getEnrolmentsCount() {
        return enrolmentsCount;
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    @AttrRef(value = "enrolmentsCount")
    public void setEnrolmentsCount(int enrolmentsCount) {
        this.enrolmentsCount = enrolmentsCount;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public CourseModule(Integer id, String code, String name, Integer semester, Integer credits) throws ConstraintViolationException {
        this.id = genId(id);
        this.code = genCode(code, semester);
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public CourseModule(String name, Integer semester, Integer credits) throws ConstraintViolationException {
        this.id = genId(null);
        this.code = genCode(null, semester);
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals("id")) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        } else if (attribName.equals("code")) {
        //TODO: implement this 
        }
    }
}
