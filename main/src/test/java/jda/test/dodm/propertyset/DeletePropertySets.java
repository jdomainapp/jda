package jda.test.dodm.propertyset;

import org.junit.Test;

import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySet.PropertySetType;

public class DeletePropertySets extends CourseManExtendedTester {
  @Test
  public void doTest() throws Exception {
    Class[] toRegisterEnums = {
        PropertySetType.class
    };
    
    Class[] toRegister = {
        PropertySet.class,
        Property.class,
    };

    registerEnumClasses(toRegisterEnums);
    
    registerDataSourceSchemas(instance.getDODM(), toRegister);
    registerClasses(toRegister);
    
    for (Class c: toRegister)
      loadObjects(c);
   
    Class c;
    for (int i = toRegister.length-1; i>=0; i--) {
      c = toRegister[i];
      deleteObjects(c,true);
    }
  }
}
