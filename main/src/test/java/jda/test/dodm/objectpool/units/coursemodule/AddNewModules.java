package jda.test.dodm.objectpool.units.coursemodule;

import java.util.Collection;

import org.junit.Test;

import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.Module;

public class AddNewModules extends CourseManBasicTester {  
  
//  @BeforeClass
//  public static void data() throws DBException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    CourseManBasicTester me = (CourseManBasicTester)instance;
    
    Collection<Module> col = me.addCourseModules();
    
    System.out.println("Added...");
    for (Module m : col) {
      System.out.printf("   %s%n", m);
    }
  }
}
