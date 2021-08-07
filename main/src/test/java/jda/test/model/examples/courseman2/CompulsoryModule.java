package jda.test.model.examples.courseman2;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DClass;

/**
 * Represents a compulsory module (a subclass of Module)
 * 
 * @author dmle
 * @version 1.0
 * 
 */
@DClass(schema=Constants.SCHEMA_NAME)
public class CompulsoryModule extends CourseModule {

  // constructor method
  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  public CompulsoryModule(String name, int semester, int credits) {
    this(null, null, name, semester, credits);
  }

  // the order of the arguments must be this: 
  // - super-class arguments first, then sub-class
  public CompulsoryModule(String name, Integer semester, Integer credits) {
    this(null, null, name, semester, credits);
  }

  public CompulsoryModule(Integer id, String code, String name, Integer semester, Integer credits) 
    throws ConstraintViolationException {
    super(id, code, name, semester, credits);
  }

}
