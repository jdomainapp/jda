package org.jda.example.coursemansw.services.coursemodule.model;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;

/**
 * Represents a compulsory module (a subclass of Module)
 *
 * @author dmle
 *
 */
@DClass(schema="courseman")
public class CompulsoryModule extends CourseModule {

  // constructor method
  // the order of the arguments must be this:
  // - super-class arguments first, then sub-class
  @DOpt(type=DOpt.Type.ObjectFormConstructor)
  public CompulsoryModule(@AttrRef("name") String name,
      @AttrRef("semester") Integer semester, @AttrRef("credits") Integer credits) {
    this(null, null, name, semester, credits);
  }

  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public CompulsoryModule(Integer id, String code, String name, Integer semester, Integer credits)
    throws ConstraintViolationException {
    super(id, code, name, semester, credits);
  }
}
