package org.jda.example.courseman.bspacegen.output;

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
    @DOpt(type = DOpt.Type.Getter, effects = "result = deptName")
    @AttrRef(value = "deptName")
    public String getDeptName() {
        return this.deptName;
    }

    @DOpt(type = DOpt.Type.Setter, requires = "deptName <> null and deptName.size() <= 30", effects = "self.deptName = deptName")
    @AttrRef(value = "deptName")
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "deptName <> null and deptName.size() <= 30", effects = "self.deptName = deptName")
    public ElectiveModule(@AttrRef(value = "id") Integer id, @AttrRef(value = "code") String code, @AttrRef(value = "name") String name, @AttrRef(value = "semester") Integer semester, @AttrRef(value = "credits") Integer credits, @AttrRef(value = "deptName") String deptName) throws ConstraintViolationException {
        super(id, code, name, semester, credits);
        this.deptName = deptName;
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "deptName <> null and deptName.size() <= 30", effects = "self.deptName = deptName")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public ElectiveModule(@AttrRef(value = "name") String name, @AttrRef(value = "semester") Integer semester, @AttrRef(value = "credits") Integer credits, @AttrRef(value = "deptName") String deptName) throws ConstraintViolationException {
        super(name, semester, credits);
        this.deptName = deptName;
    }
}
