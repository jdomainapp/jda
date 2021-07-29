package jda.test.app.courseman.basic.units;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.SClass;

public class CreateSClasses extends CourseManBasicTester {
  
  @BeforeClass
  public static void data() throws DataSourceException { ((CourseManBasicTester)instance).initSClasses(); }
  
  @Test
  public void doTest() throws Exception {
    System.out.println(this.getClass().getSimpleName());
    
    Class c = SClass.class;
    Collection<SClass> sclasses = instance.getData().get(c);
    
    DODMBasic schema = instance.getDODM();
    
    for (SClass o: sclasses) {
      addObject(o);// schema.addObject(o);
      System.out.printf("Created: %s%n", o);
    }
  }
}
