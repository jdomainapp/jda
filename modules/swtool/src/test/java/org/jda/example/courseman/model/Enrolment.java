package org.jda.example.courseman.model;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;

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

    /** END state space */
    
    /** Behaviour space */
    
}
