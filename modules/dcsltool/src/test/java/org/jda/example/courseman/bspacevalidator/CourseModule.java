package org.jda.example.courseman.bspacevalidator;

import java.util.Collection;
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

    @DOpt(type = DOpt.Type.Getter, effects = "result = id")
    @AttrRef(value = "id")
    public int getId() {
        return this.id;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen, effects = "if id = null then idCounter = idCounter + 1 and result = idCounter else if id > idCounter then idCounter = id and result = id else result = id endif endif")
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

    @DOpt(type = DOpt.Type.Getter, effects = "result = code")
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

    @DOpt(type = DOpt.Type.Getter, effects = "result = name")
    @AttrRef(value = "name")
    public String getName() {
        return this.name;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "name <> null and name.size() <= 30", effects = "self.name = name")
    @AttrRef(value = "name")
    public void setName(String name) {
        this.name = name;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = semester")
    @AttrRef(value = "semester")
    public int getSemester() {
        return this.semester;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "semester >= 1 and semester <= 10", effects = "self.semester = semester")
    @AttrRef(value = "semester")
    public void setSemester(int semester) {
        this.semester = semester;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = credits")
    @AttrRef(value = "credits")
    public int getCredits() {
        return this.credits;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "credits >= 1 and credits <= 5", effects = "self.credits = credits")
    @AttrRef(value = "credits")
    public void setCredits(int credits) {
        this.credits = credits;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = enrolments")
    @AttrRef(value = "enrolments")
    public Collection<Enrolment> getEnrolments() {
        return this.enrolments;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.enrolments = enrolments")
    @AttrRef(value = "enrolments")
    public void setEnrolments(Collection<Enrolment> enrolments) {
        this.enrolments = enrolments;
    }

    private int enrolmentsCount;

    @DOpt(type = DOpt.Type.LinkAdder, effects = "enrolments->forAll(o | enrolments@pre->includes(o) or obj = o) and enrolmentsCount = enrolmentsCount@pre + (enrolments->size() - enrolments@pre->size())")
    @AttrRef(value = "enrolments")
    public boolean addEnrolments(Enrolment obj) {
        if (!enrolments.contains(obj)) {
            enrolments.add(obj);
            enrolmentsCount++;
        }
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder, effects = "enrolments->forAll(o | enrolments@pre->includes(o) or obj->includes(o)) and enrolmentsCount = enrolmentsCount@pre + (enrolments->size() - enrolments@pre->size())")
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

    @DOpt(type = DOpt.Type.LinkAdderNew, effects = "enrolments->forAll(o | enrolments@pre->includes(o) or obj = o) and enrolmentsCount = enrolmentsCount@pre + (enrolments->size() - enrolments@pre->size())")
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

    @DOpt(type = DOpt.Type.LinkRemover, requires = "if enrolments->includes(obj) then enrolmentsCount - 1 >= 0 else true endif", effects = "enrolments->forAll(o | enrolments@pre->includes(o) and obj <> o) and enrolmentsCount = enrolmentsCount@pre - (enrolments@pre->size() - enrolments->size())")
    @AttrRef(value = "enrolments")
    public boolean onRemoveEnrolments(Enrolment obj) {
        boolean removed = enrolments.remove(obj);
        if (removed)
            enrolmentsCount--;
        return removed;
    }

    @DOpt(type = DOpt.Type.LinkRemover, requires = "enrolmentsCount - obj->select(o | enrolments->includes(o))->size() >= 0", effects = "enrolments->forAll(o | enrolments@pre->includes(o) and obj->excludes(o)) and enrolmentsCount = enrolmentsCount@pre - (enrolments@pre->size() - enrolments->size())")
    @AttrRef(value = "enrolments")
    public boolean onRemoveEnrolments(Collection<Enrolment> obj) {
        for (Enrolment o : obj) {
            boolean removed = enrolments.remove(o);
            if (removed)
                enrolmentsCount--;
        }
        return false;
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

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "id >= 1 and code <> null and code.size() <= 6 and name <> null and name.size() <= 30 and semester >= 1 and semester <= 10 and credits >= 1 and credits <= 5", effects = "self.id = genId(id) and self.code = genCode(code,semester) and self.name = name and self.semester = semester and self.credits = credits")
    public CourseModule(Integer id, String code, String name, Integer semester, Integer credits) throws ConstraintViolationException {
        this.id = genId(id);
        this.code = genCode(code, semester);
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "name <> null and name.size() <= 30 and semester >= 1 and semester <= 10 and credits >= 1 and credits <= 5", effects = "self.name = name and self.semester = semester and self.credits = credits and self.id = genId(null) and self.code = genCode(null,semester)")
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
