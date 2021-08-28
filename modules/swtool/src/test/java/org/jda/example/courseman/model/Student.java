package org.jda.example.courseman.model;

import java.util.Collection;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.Select;

/**
 * Represents a student.
 * 
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class Student {

    /*** STATE SPACE **/
    @DAttr(name = "id", type = Type.Integer, id = true, auto = true, mutable = false, optional = false, min = 1.0)
    private int id;

    @DAttr(name = "name", type = Type.String, length = 30, optional = false)
    private String name;

    @DAttr(name = "address", type = Type.Domain, length = 20, optional = true)
    @DAssoc(ascName = "student-has-address", role = "student", ascType = AssocType.One2One, endType = AssocEndType.One, associate = @Associate(type = Address.class, cardMin = 1, cardMax = 1))
    private Address address;

    @DAttr(name = "enrolments", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Enrolment.class))
    @DAssoc(ascName = "std-has-enrols", role = "student", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Enrolment.class, cardMin = 0, cardMax = 30))
    private Collection<Enrolment> enrolments;

    /** END state space */
    
    /** Behaviour space */
    
}
