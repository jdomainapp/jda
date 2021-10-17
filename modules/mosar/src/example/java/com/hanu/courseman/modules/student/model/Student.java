package com.hanu.courseman.modules.student.model;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hanu.courseman.exceptions.DExCode;
import com.hanu.courseman.modules.address.model.Address;
import com.hanu.courseman.modules.enrolment.model.Enrolment;
import com.hanu.courseman.modules.studentclass.model.StudentClass;
import com.hanu.courseman.utils.DToolkit;
import com.hanu.courseman.utils.Deserializers;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.modules.patterndom.assets.domevents.Subscriber;
import jda.util.events.ChangeEventSource;

/**
 * Represents a student. The student ID is auto-incremented from the current
 * year.
 *
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class Student implements Subscriber, Publisher {
    public static final String A_name = "name";
    public static final String A_gender = "gender";
    public static final String A_id = "id";
    public static final String A_dob = "dob";
    public static final String A_address = "address";
    public static final String A_email = "email";
    public static final String A_rptStudentByName = "rptStudentByName";
    public static final String A_rptStudentByCity = "rptStudentByCity";

    // attributes of students
    @DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6,
            mutable = false, optional = false)
    private String id;
    //static variable to keep track of student id
    private static int idCounter = 0;

    @DAttr(name = A_name, type = Type.String, length = 30, optional = false, cid = true)
    private String name;

    @DAttr(name = A_gender, type = Type.Domain, length = 10, optional = false)
    private Gender gender;

    @DAttr(name = A_dob, type = Type.Date, length = 15, optional = false)
    private Date dob;

    @DAttr(name = A_address, type = Type.Domain, length = 20, optional = true)
    @DAssoc(ascName = "student-has-city", role = "student",
            ascType = AssocType.One2One, endType = AssocEndType.One,
            associate = @Associate(type = Address.class, cardMin = 1, cardMax = 1))
    @JsonIgnoreProperties({"student"})
    @JsonDeserialize(using = Deserializers.AddrDeserializer.class)
    private Address address;

    @DAttr(name = A_email, type = Type.String, length = 30, optional = false)
    private String email;

    @DAttr(name = "studentClass", type = Type.Domain, length = 6)
    @DAssoc(ascName = "class-has-student", role = "student",
            ascType = AssocType.One2Many, endType = AssocEndType.Many,
            associate = @Associate(type = StudentClass.class, cardMin = 1, cardMax = 1))
    @JsonIgnoreProperties({"students"})
    @JsonDeserialize(using = Deserializers.StudClsDeserializer.class)
    private StudentClass studentClass;

    @DAttr(name = "enrolments", type = Type.Collection, optional = false,
            serialisable = false, filter = @Select(clazz = Enrolment.class))
    @DAssoc(ascName = "student-has-enrolments", role = "student",
            ascType = AssocType.One2Many, endType = AssocEndType.One,
            associate = @Associate(type = Enrolment.class, cardMin = 0, cardMax = 30))
    @JsonIgnoreProperties({"student"})
    @JsonDeserialize(using = Deserializers.EnrCollectionDeserializer.class)
    private Collection<Enrolment> enrolments;

    // derived
    private int enrolmentCount;

    // v2.6.4b: derived: average of the final mark of all enrolments
    private double averageMark;

    @JsonIgnore
    private ChangeEventSource eventSource;

    @JsonCreator
    private Student() {
        this(null);
    }

    private Student(String id) {
        this.id = nextID(id);
        this.enrolments = new HashSet<>();
    }

    // constructor methods
    // for creating in the application
    // without SClass
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Student(@AttrRef("name") String name,
                   @AttrRef("gender") Gender gender,
                   @AttrRef("dob") Date dob,
                   @AttrRef("address") Address address,
                   @AttrRef("email") String email) {
        this(null, name, gender, dob, address, email, null);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Student(@AttrRef("name") String name,
                   @AttrRef("gender") Gender gender,
                   @AttrRef("dob") Date dob,
                   @AttrRef("address") Address address,
                   @AttrRef("email") String email,
                   @AttrRef("studentClass") StudentClass studentClass) {
        this(null, name, gender, dob, address, email, studentClass);
    }

    // a shared constructor that is invoked by other constructors
    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public Student(@AttrRef("id") String id,
                   @AttrRef("dob") String name, @AttrRef("gender") Gender gender,
                   @AttrRef("dob") Date dob, @AttrRef("address") Address address,
                   @AttrRef("email") String email, @AttrRef("studentClass") StudentClass studentClass)
            throws ConstraintViolationException {
        // generate an id
        this.id = nextID(id);

        // assign other values
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.studentClass = studentClass;

        enrolments = new HashSet<>();
        enrolmentCount = 0;
        averageMark = 0D;

        // publish/subscribe pattern
        // register student as subscriber for add event
        addSubscriber(studentClass, CMEventType.values());
        addSubscriber(address, CMEventType.values());

        // fire OnCreated event
        notify(CMEventType.OnCreated, getEventSource());
    }

    // setter methods
    public void setName(String name) {
        this.name = name;
    }

    public void setDob(Date dob) throws ConstraintViolationException {
        // additional validation on dob
        if (dob.before(DToolkit.MIN_DOB)) {
            throw new ConstraintViolationException(DExCode.INVALID_DOB, dob);
        }

        this.dob = dob;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setAddress(Address address) {
        if (Objects.equals(this.address, address)) return;
        notify(CMEventType.OnRemoved, getEventSource(), this.address);
        removeSubcriber(this.address, CMEventType.values());
        this.address = address;
        addSubscriber(address, CMEventType.values());
        notify(CMEventType.OnCreated, getEventSource(), this.address);
    }

    // v2.7.3
    public void setNewAddress(Address address) {
        // change this invocation if need to perform other tasks (e.g. updating value of a derived attribtes)
        setAddress(address);
    }

    public void setEmail(String email) throws ConstraintViolationException {
        if (email.indexOf("@") < 0) {
            throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE,
                    new Object[]{"'" + email + "' (does not have '@') "});
        }
        this.email = email;
    }

    public void setStudentClass(StudentClass studentClass) {
        notify(CMEventType.OnRemoved, getEventSource(), this.studentClass);
        removeSubcriber(this.studentClass, CMEventType.values());
        this.studentClass = studentClass;
        addSubscriber(this.studentClass, CMEventType.values());
        notify(CMEventType.OnCreated, getEventSource(), this.studentClass);
    }

    @DOpt(type = DOpt.Type.LinkAdder)
    //only need to do this for reflexive association: @MemberRef(name="enrolments")
    public boolean addEnrolment(Enrolment e) {
        if (!enrolments.contains(e))
            enrolments.add(e);

        // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
        // otherwise computeAverageMark (below) can not be performed correctly
        // WHY? average mark is not serialisable
//    enrolmentCount++;
//
//    // v2.6.4.b
//    computeAverageMark();

        // no other attributes changed
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    public boolean addNewEnrolment(Enrolment e) {
        enrolments.add(e);

        enrolmentCount++;

        // v2.6.4.b
        computeAverageMark();

        // no other attributes changed (average mark is not serialisable!!!)
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdder)
    //@MemberRef(name="enrolments")
    public boolean addEnrolment(Collection<Enrolment> enrols) {
        boolean added = false;
        for (Enrolment e : enrols) {
            if (!enrolments.contains(e)) {
                if (!added) added = true;
                enrolments.add(e);
            }
        }
        // IMPORTANT: enrolment count must be updated separately by invoking setEnrolmentCount
        // otherwise computeAverageMark (below) can not be performed correctly
        // WHY? average mark is not serialisable
//    enrolmentCount += enrols.size();

//    if (added) {
//      // avg mark is not serialisable so we need to compute it here
//      computeAverageMark();
//    }

        // no other attributes changed
        return false;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    public boolean addNewEnrolment(Collection<Enrolment> enrols) {
        enrolments.addAll(enrols);
        enrolmentCount += enrols.size();

        // v2.6.4.b
        computeAverageMark();

        // no other attributes changed (average mark is not serialisable!!!)
        return false;
    }

    @DOpt(type = DOpt.Type.LinkRemover)
    //@MemberRef(name="enrolments")
    public boolean removeEnrolment(Enrolment e) {
        boolean removed = enrolments.remove(e);

        if (removed) {
            enrolmentCount--;

            // v2.6.4.b
            computeAverageMark();
        }
        // no other attributes changed
        return false;
    }

    @DOpt(type = DOpt.Type.LinkUpdater)
    //@MemberRef(name="enrolments")
    public boolean updateEnrolment(Enrolment e) throws IllegalStateException {
        // recompute using just the affected enrolment
        double totalMark = averageMark * enrolmentCount;

        int oldFinalMark = e.getFinalMark(true);

        int diff = e.getFinalMark() - oldFinalMark;

        // TODO: cache totalMark if needed

        totalMark += diff;

        averageMark = totalMark / enrolmentCount;

        // no other attributes changed
        return true;
    }

    public void setEnrolments(Collection<Enrolment> en) {
        this.enrolments = en;
        enrolmentCount = en.size();

        // v2.6.4.b
        computeAverageMark();
    }

    // v2.6.4.b

    /**
     * @effects computes {@link #averageMark} of all the {@link Enrolment#getFinalMark()}s
     * (in {@link #enrolments}.
     */
    private void computeAverageMark() {
        if (enrolmentCount > 0) {
            double totalMark = 0d;
            for (Enrolment e : enrolments) {
                totalMark += e.getFinalMark();
            }

            averageMark = totalMark / enrolmentCount;
        } else {
            averageMark = 0;
        }
    }

    // v2.6.4.b
    public double getAverageMark() {
        return averageMark;
    }

    // getter methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Gender getGender() {
        return gender;
    }

    public Date getDob() {
        return dob;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public StudentClass getStudentClass() {
        return studentClass;
    }

    public Collection<Enrolment> getEnrolments() {
        return enrolments;
    }

    @JsonIgnore
    @DOpt(type = DOpt.Type.LinkCountGetter)
    public Integer getEnrolmentsCount() {
        return enrolmentCount;
        //return enrolments.size();
    }

    @DOpt(type = DOpt.Type.LinkCountSetter)
    public void setEnrolmentsCount(int count) {
        enrolmentCount = count;
    }

    // override toString

    /**
     * @effects returns <code>this.id</code>
     */
    @Override
    public String toString() {
        return toString(true);
    }

    /**
     * @effects returns <code>Student(id,name,dob,address,email)</code>.
     */
    public String toString(boolean full) {
        if (full)
            return "Student(" + id + "," + name + "," + gender + ", " + dob + "," + address + ","
                    + email + ((studentClass != null) ? "," + studentClass.getName() : "") + ")";
        else
            return "Student(" + id + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Student other = (Student) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    // automatically generate the next student id
    private String nextID(String id) throws ConstraintViolationException {
        if (id == null) { // generate a new id
            if (idCounter == 0) {
                idCounter = Calendar.getInstance().get(Calendar.YEAR);
            } else {
                idCounter++;
            }
            return "S" + idCounter;
        } else {
            // update id
            int num;
            try {
                num = Integer.parseInt(id.substring(1));
            } catch (RuntimeException e) {
                throw new ConstraintViolationException(
                        ConstraintViolationException.Code.INVALID_VALUE, e, new Object[]{id});
            }

            if (num > idCounter) {
                idCounter = num;
            }

            return id;
        }
    }

    /**
     * @requires minVal != null /\ maxVal != null
     * @effects update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
     */
    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void updateAutoGeneratedValue(
            DAttr attrib,
            Tuple derivingValue,
            Object minVal,
            Object maxVal) throws ConstraintViolationException {

        if (minVal != null && maxVal != null) {
            //TODO: update this for the correct attribute if there are more than one auto attributes of this class

            String maxId = (String) maxVal;

            try {
                int maxIdNum = Integer.parseInt(maxId.substring(1));

                if (maxIdNum > idCounter) // extra check
                    idCounter = maxIdNum;

            } catch (RuntimeException e) {
                throw new ConstraintViolationException(
                        ConstraintViolationException.Code.INVALID_VALUE, e, new Object[]{maxId});
            }
        }
    }

    /**
     * @effects Handle events fired by {@link vn.com.courseman.model.events.Enrolment}.
     */
    @Override
    public void handleEvent(EventType type, ChangeEventSource source) {
        CMEventType eventType = (CMEventType) type;
        List data = source.getObjects();
        Object srcObj = data.get(0);

//        System.out.println(this + ".handleEvent(" + eventType + ", " + srcObj + ")");
        if (!data.stream().anyMatch(item -> item instanceof Student)) return;

        if (srcObj instanceof Enrolment) {
            Enrolment enrolment = (Enrolment) srcObj;

            switch (eventType) {
                case OnCreated:
                    // update links
                    this.addNewEnrolment(enrolment);
                    break;
                case OnUpdated:
                    break;
                case OnRemoved:
                    // remove link
                    this.removeEnrolment(enrolment);
                    break;
            }
        } else if (srcObj instanceof Address) {
            switch (eventType) {
                case OnCreated:
                    this.setNewAddress((Address) srcObj);
                    break;
                case OnRemoved:
                    removeSubcriber(this.address, CMEventType.values());
                    this.address = null;
                    break;
            }
        }
    }

    @Override
    @JsonIgnore
    public ChangeEventSource getEventSource() {
        if (eventSource == null) {
            eventSource = createEventSource(getClass());
        } else {
            resetEventSource(eventSource);
        }

        return eventSource;
    }

    /**
     * @effects notify register all registered listeners
     */
    @Override
    public void finalize() throws Throwable {
        notify(CMEventType.OnRemoved, getEventSource());
    }
}
