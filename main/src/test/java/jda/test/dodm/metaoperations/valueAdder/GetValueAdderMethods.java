package jda.test.dodm.metaoperations.valueAdder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Student;

public class GetValueAdderMethods extends CourseManExtendedTester {
  @Test
  public void doTest() throws Exception {
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class Student = Student.class;
    
    registerClass(Student, false);

    // a specific method
    Class[] paramTypes = {List.class};
    
    String attribName = "enrolments";
    
    Method m = schema.findAttributeValueAdderMethod(Student, attribName, paramTypes);
    
    System.out.printf("Value adder method with parameter(s): %s%n  %s%n", Arrays.toString(paramTypes), m);
    
//    Class[] a1 = new Class[0], a2 = new Class[0];
//    
//    System.out.printf("Arrays.equals(%s,%s)=%b%n",Arrays.toString(a1), Arrays.toString(a2), Arrays.equals(a1, a2));
  }
}
