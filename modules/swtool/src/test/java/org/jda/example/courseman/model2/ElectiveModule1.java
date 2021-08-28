package org.jda.example.courseman.model2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DClass;
/**
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class ElectiveModule1 extends CourseModule1 {
    /*** STATE SPACE **/
    @DAttr(name = "deptName", type = Type.String, length = 30, optional = false)
    private String deptName;
    /** END state space */
    
    /** Behaviour space */
    
}
