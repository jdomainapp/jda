package org.jda.example.courseman.bspacegen.input;

import java.util.*;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class ElectiveModule extends CourseModule {

    /*** STATE SPACE **/
    @DAttr(name = "deptName", type = Type.String, length = 30, optional = false)
    private String deptName;

    /** END state space */
}
