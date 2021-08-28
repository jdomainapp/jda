package org.jda.example.courseman.model2;
import java.util.Collection;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.Select;
/**
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class CourseModule1 {
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
    @DAttr(name = "enrolments", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = Enrolment1.class))
    @DAssoc(ascName = "mod-has-enrols", role = "module", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = Enrolment1.class, cardMin = 0, cardMax = DCSLConstants.CARD_MORE))
    private Collection<Enrolment1> enrolments;
    /** END state space */
    
    /** Behaviour space */
    
}
