package org.jda.example.coursemanrestful.modules.studentclass.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jda.example.coursemanrestful.modules.student.model.Student;
import org.jda.example.coursemanrestful.utils.Deserializers;

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
import jda.modules.dcsl.syntax.Select;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.EventType;
import jda.modules.patterndom.assets.domevents.Subscriber;
import jda.util.events.ChangeEventSource;

/**
 * Represents a student class.
 *
 * @author dmle
 *
 */
@DClass(schema="courseman")
public class StudentClass implements Subscriber {
    @DAttr(name="id",id=true,auto=true,length=6,mutable=false,type=Type.Integer)
    private int id;
    private static int idCounter;

    // candidate identifier
    @DAttr(name="name",length=20,type=Type.String,optional=false, cid=true)
    private String name;

    @DAttr(name="students",type=Type.Collection,
            serialisable=false,optional=true,
            filter=@Select(clazz= Student.class))
    @DAssoc(ascName="class-has-student",role="class",
            ascType=AssocType.One2Many,endType=AssocEndType.One,
            associate=@Associate(type= Student.class,
                    cardMin=1,cardMax=25))
    @JsonIgnoreProperties({"studentClass"})
    @JsonDeserialize(using = Deserializers.StudCollectionDeserializer.class)
    private Collection<Student> students;

    // derived attributes
    private int studentsCount;

    @DOpt(type=DOpt.Type.ObjectFormConstructor)
    @DOpt(type=DOpt.Type.RequiredConstructor)
    public StudentClass(@AttrRef("name") String name) {
        this(null, name);
    }

    // constructor to create objects from data source
    @DOpt(type=DOpt.Type.DataSourceConstructor)
    public StudentClass(@AttrRef("id") Integer id, @AttrRef("name") String name) {
        this.id = nextID(id);
        this.name = name;

        students = new HashSet<>();
        studentsCount = 0;
    }

    @JsonCreator
    private StudentClass() {
        this((Integer) null);
    }

    private StudentClass(Integer id) {
        if (id == null) {
            this.id = nextID(null);
        } else {
            this.id = id;
        }
        this.students = new HashSet<>();
    }

    @DOpt(type=DOpt.Type.Setter)
    public void setName(String name) {
        this.name = name;
    }

    @DOpt(type=DOpt.Type.LinkAdder)
    //only need to do this for reflexive association: @MemberRef(name="students")
    public boolean addStudent(Student s) {
        if (!this.students.contains(s)) {
            students.add(s);
        }

        // no other attributes changed
        return false;
    }

    @DOpt(type=DOpt.Type.LinkAdderNew)
    public boolean addNewStudent(Student s) {
        students.add(s);
        studentsCount++;

        // no other attributes changed
        return false;
    }

    @DOpt(type=DOpt.Type.LinkAdder)
    public boolean addStudent(Collection<Student> students) {
        for (Student s : students) {
            if (!this.students.contains(s)) {
                this.students.add(s);
            }
        }

        // no other attributes changed
        return false;
    }

    @DOpt(type=DOpt.Type.LinkAdderNew)
    public boolean addNewStudent(Collection<Student> students) {
        this.students.addAll(students);
        studentsCount += students.size();

        // no other attributes changed
        return false;
    }

    @DOpt(type=DOpt.Type.LinkRemover)
    //only need to do this for reflexive association: @MemberRef(name="students")
    public boolean removeStudent(Student s) {
        boolean removed = students.remove(s);

        if (removed) {
            studentsCount--;
        }

        // no other attributes changed
        return false;
    }

    @DOpt(type=DOpt.Type.Setter)
    public void setStudents(Collection<Student> students) {
        this.students = students;

        studentsCount = students.size();
    }

    /**
     * @effects
     *  return <tt>studentsCount</tt>
     */
    @JsonIgnore
    @DOpt(type=DOpt.Type.LinkCountGetter)
    public Integer getStudentsCount() {
        return studentsCount;
    }

    @DOpt(type=DOpt.Type.LinkCountSetter)
    public void setStudentsCount(int count) {
        studentsCount = count;
    }

    @DOpt(type=DOpt.Type.Getter)
    public String getName() {
        return name;
    }

    @DOpt(type=DOpt.Type.Getter)
    public Collection<Student> getStudents() {
        return students;
    }

    @DOpt(type=DOpt.Type.Getter)
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SClass("+getId()+","+getName()+")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        StudentClass other = (StudentClass) obj;
        if (id != other.id)
            return false;
        return true;
    }

    private static int nextID(Integer currID) {
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
     * @requires
     *  minVal != null /\ maxVal != null
     * @effects
     *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
     */
    @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
    public static void updateAutoGeneratedValue(
            DAttr attrib,
            Tuple derivingValue,
            Object minVal,
            Object maxVal) throws ConstraintViolationException {

        if (minVal != null && maxVal != null) {
            if (attrib.name().equals("id")) {
                int maxIdVal = (Integer) maxVal;
                if (maxIdVal > idCounter)
                    idCounter = maxIdVal;
            }
        }
    }

    @Override
    public void handleEvent(EventType eventType, ChangeEventSource changeEventSource) {
        CMEventType type = (CMEventType) eventType;
        List data = changeEventSource.getObjects();
        Object srcObj = data.get(0);

//        System.out.println(this + ".handleEvent(" + eventType + ", " + srcObj + ")");

        if (!data.stream().anyMatch(item -> item instanceof StudentClass)) return;

        if (srcObj instanceof Student) {
            Student student = (Student) srcObj;
            switch (type) {
                case OnCreated:
                    // update links
                    this.addNewStudent(student);
                    break;
                case OnUpdated:
                    break;
                case OnRemoved:
                    // remove link
                    this.removeStudent(student);
                    break;
            }
        }
    }
}
