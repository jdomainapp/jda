package org.jda.example.courseman.bspacegen.output;

import java.util.*;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;

/**
 * Represents a course module.
 * @author dmle
 * @version 2.0
 */
@DClass(schema = "courseman")
public class CompulsoryModule extends CourseModule {

    @DOpt(type = DOpt.Type.DataSourceConstructor, requires = "", effects = "")
    public CompulsoryModule(@AttrRef(value = "id") Integer id, @AttrRef(value = "code") String code, @AttrRef(value = "name") String name, @AttrRef(value = "semester") Integer semester, @AttrRef(value = "credits") Integer credits) throws ConstraintViolationException {
        super(id, code, name, semester, credits);
    }

    @DOpt(type = DOpt.Type.ObjectFormConstructor, requires = "", effects = "")
    @DOpt(type = DOpt.Type.RequiredConstructor)
    public CompulsoryModule(@AttrRef(value = "name") String name, @AttrRef(value = "semester") Integer semester, @AttrRef(value = "credits") Integer credits) throws ConstraintViolationException {
        super(name, semester, credits);
    }
}
