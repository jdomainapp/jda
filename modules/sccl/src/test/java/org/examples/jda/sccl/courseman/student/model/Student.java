package org.examples.jda.sccl.courseman.student.model;

import java.util.Collection;

import org.examples.jda.sccl.courseman.address.model.Address;
import org.examples.jda.sccl.courseman.enrolment.model.Enrolment;

import java.util.*;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a student.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class Student {

    public Student() {}

    public static final String A_id = "id";

    /*** STATE SPACE **/
    @DAttr(name = A_id, type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1.0)
    private int id;

    @DAttr(name = "name", type = Type.String, length = 30, optional = false)
    private String name;

    @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
    @DAssoc(ascName = "student-has-address", role = "student", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Address.class, cardMin = 1, cardMax = 1))
    private Address address;

    @DAttr(name = "enrolments", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Enrolment.class))
    @DAssoc(ascName = "std-has-enrols", role = "student", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Enrolment.class, cardMin = 0, cardMax = 30))
    private Collection<Enrolment> enrolments;

    /*** BEHAVIOUR SPACE **/
    private static int idCounter;

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = A_id)
    public int getId() {
        return this.id;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
    @AttrRef(value = A_id)
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
    @AttrRef(value = "address")
    public Address getAddress() {
        return this.address;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "address")
    public boolean setNewAddress(Address obj) {
        setAddress(obj);
        return false;
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
    public Student(Integer id, String name, Address address) throws ConstraintViolationException {
        this.id = genId(id);
        this.name = name;
        this.address = address;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Student(String name, Address address) throws ConstraintViolationException {
        this.id = genId(null);
        this.name = name;
        this.address = address;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Student(String name) throws ConstraintViolationException {
        this.id = genId(null);
        this.name = name;
        this.address = null;
        this.enrolments = new ArrayList();
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void synchWithSource(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        String attribName = attrib.name();
        if (attribName.equals(A_id)) {
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
    }
}
