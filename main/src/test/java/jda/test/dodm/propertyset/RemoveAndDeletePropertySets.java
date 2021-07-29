package jda.test.dodm.propertyset;

import org.junit.Test;

import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;

public class RemoveAndDeletePropertySets extends CourseManExtendedTester {
  @Test
  public void doTest() throws Exception {
//    Class[] toRegisterEnums = {
//        PropertySetType.class
//    };
    
    Class[] toRegister = {
        PropertySet.class,
        Property.class,
    };

//    registerEnumClasses(toRegisterEnums);
    
    for (Class c: toRegister)
      removeClassAndDeleteFromDB(c);
  }
}
