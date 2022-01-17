package org.jda.example.coursemanmdsa.modules.address.model;

import java.util.List;
import java.util.Objects;

import org.jda.example.coursemanmdsa.modules.student.model.Student;
import org.jda.example.coursemanmdsa.utils.Deserializers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.modules.patterndom.assets.domevents.Subscriber;
import jda.util.events.ChangeEventSource;

/**
 * A domain class whose objects are city names. This class is used as
 * the <code>allowedValues</code> of the domain attributes of
 * other domain classes (e.g. Student.address).
 *
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to
 * return the string representation of a city name which is expected by
 * the application.
 *
 * @author dmle
 */
@DClass(schema = "courseman")
public class Address implements Subscriber, Publisher {

    public static final String A_name = "name";

    @DAttr(name = "id", id = true, auto = true, length = 3, mutable = false, optional = false, type = Type.Integer)
    private int id;
    private static int idCounter;

    @DAttr(name = A_name, type = Type.String, length = 20, optional = false)
    private String name;

    @DAttr(name = "student", type = Type.Domain, serialisable = false)
    @DAssoc(ascName = "student-has-city", role = "city",
            ascType = AssocType.One2One, endType = AssocEndType.One,
            associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1, determinant = true))
    @JsonIgnoreProperties({"address"})
    @JsonDeserialize(using = Deserializers.StudDeserializer.class)
    private Student student;

    // from object form: Student is not included
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Address(@AttrRef("name") String name) {
        this(null, name, null);
    }

    // from object form: Student is included
    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    public Address(@AttrRef("name") String name, @AttrRef("student") Student student) {
        this(null, name, student);
    }

    // from data source
    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public Address(@AttrRef("id") Integer id, @AttrRef("name") String name) {
        this(id, name, null);
    }

    @JsonIgnore
    private ChangeEventSource eventSource;

    @JsonCreator
    private Address() {
        this((Integer) null);
    }

    private Address(Integer id) {
        this.id = nextId(id);
    }

    // based constructor (used by others)
    private Address(Integer id, String name, Student student) {
        this.id = nextId(id);
        this.name = name;
        this.student = student;

        addSubscriber(student, CMEventType.values());
        // fire OnCreated event
        notify(CMEventType.OnCreated, getEventSource());
    }

    private static int nextId(Integer currID) {
        if (currID == null) {
            idCounter++;
            return idCounter;
        } else {
            int num = currID.intValue();
            if (num > idCounter)
                idCounter = num;

            return currID;
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
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    public void setNewStudent(Student student) {
        setStudent(student);
        // do other updates here (if needed)
    }

    public void setStudent(Student student) {
        if (Objects.equals(this.student, student)) return;
        notify(CMEventType.OnRemoved, getEventSource(), this.student);
        removeSubcriber(this.student, CMEventType.values());
        this.student = student;
        addSubscriber(this.student, CMEventType.values());
        notify(CMEventType.OnCreated, getEventSource(), this.student);
    }

    @Override
    public String toString() {
        return name;
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

    @Override
    public void handleEvent(EventType type, ChangeEventSource source) {
        CMEventType eventType = (CMEventType) type;
        List data = source.getObjects();
        Object srcObj = data.get(0);
//        System.out.println(this + ".handleEvent(" + eventType + ", " + srcObj + ")");
        if (!data.stream().anyMatch(item -> item instanceof Address)) return;

        if (srcObj instanceof Student) {
            switch (eventType) {
                case OnCreated:
                    this.setNewStudent((Student) srcObj);
                    break;
                case OnRemoved:
                    removeSubcriber(this.student, CMEventType.values());
                    this.student = null;
                    break;
            }
        }
    }
}