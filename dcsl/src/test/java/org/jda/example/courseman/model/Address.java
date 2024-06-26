package org.jda.example.courseman.model;

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

import java.util.*;

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

    @DOpt(type = DOpt.Type.ObjectFormConstructor)
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public Address(String cityName) {
        this(null, cityName);
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor)
    public Address(Integer id, String cityName) {
        this.id = nextId(id);
        this.cityName = cityName;
    }

    @DOpt(type = DOpt.Type.AutoAttributeValueGen)
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
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
    @DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
    public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal) throws ConstraintViolationException {
        if (minVal != null && maxVal != null) {
            //TODO: update this for the correct attribute if there are more than one auto attributes of this class 
            int maxIdVal = (Integer) maxVal;
            if (maxIdVal > idCounter)
                idCounter = maxIdVal;
        }
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "id")
    public int getId() {
        return id;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "cityName")
    public String getCityName() {
        return cityName;
    }

    @DOpt(type = DOpt.Type.Getter)
    @AttrRef(value = "student")
    public Student getStudent() {
        return student;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "cityName")
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @DOpt(type = DOpt.Type.Setter)
    @AttrRef(value = "student")
    public void setStudent(Student student) {
        this.student = student;
    }

    @DOpt(type = DOpt.Type.LinkAdderNew)
    @AttrRef(value = "student")
    public void setNewStudent(Student student) {
        // do other updates here (if needed)
        setStudent(student);
    }

    @Override
    public String toString() {
        return cityName;
    }
}
