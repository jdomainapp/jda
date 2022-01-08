package org.jda.example.courseman.bspacegen.output;

import java.util.*;
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
 * A domain class representing simple addresses that contain just the city names. This class is used as 
 * the <code>allowedValues</code> of the domain attributes of 
 * other domain classes (e.g. Student.address).  
 * 
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to 
 * return the string representation of a city name which is expected by 
 * the application. 
 * 
 * @author dmle
 * @version 1.0
 */
@DClass(schema = "courseman")
public class Address {

    /*** STATE SPACE **/
    @DAttr(name = "id", id = true, auto = true, length = 3, mutable = false, optional = false, type = Type.Integer)
    private int id;

    @DAttr(name = "cityName", type = Type.String, length = 20, optional = false)
    private String cityName;

    @DAttr(name = "student", type = Type.Domain, optional = true, serialisable = false)
    @DAssoc(ascName = "student-has-address", role = "address", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Student.class, cardMin = 1, cardMax = 1, determinant = true))
    private Student student;

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

    @DOpt(type = DOpt.Type.Getter, effects = "result = cityName")
    @AttrRef(value = "cityName")
    public String getCityName() {
        return this.cityName;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "cityName <> null and cityName.size() <= 20", effects = "self.cityName = cityName")
    @AttrRef(value = "cityName")
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @DOpt(type = DOpt.Type.Getter, effects = "result = student")
    @AttrRef(value = "student")
    public Student getStudent() {
        return this.student;
    }

    @DOpt(type = DOpt.Type.Setter, effects = "self.student = student")
    @AttrRef(value = "student")
    public void setStudent(Student student) {
        this.student = student;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "student")
    public boolean setNewStudent(Student obj) {
        setStudent(obj);
        return false;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "cityName <> null and cityName.size() <= 20", effects = "self.id = genId(id) and self.cityName = cityName")
    public Address(@AttrRef(value = "id") Integer id, @AttrRef(value = "cityName") String cityName) throws ConstraintViolationException {
        this.id = genId(id);
        this.cityName = cityName;
        this.student = null;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "cityName <> null and cityName.size() <= 20", effects = "self.cityName = cityName and self.student = student and self.id = genId(null)")
    public Address(@AttrRef(value = "cityName") String cityName, @AttrRef(value = "student") Student student) throws ConstraintViolationException {
        this.id = genId(null);
        this.cityName = cityName;
        this.student = student;
    }

    @DOpt(type = DOpt.Type.RequiredConstructor, requires = "cityName <> null and cityName.size() <= 20", effects = "self.cityName = cityName and self.id = genId(null)")
    public Address(@AttrRef(value = "cityName") String cityName) throws ConstraintViolationException {
        this.id = genId(null);
        this.cityName = cityName;
        this.student = null;
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
}
