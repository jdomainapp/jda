package jda.test.dodm.metaoperations.idrange;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.City;
import jda.test.model.enhanced.Instructor;
import jda.test.model.enhanced.Person;
import jda.test.model.enhanced.Staff;
import jda.test.model.enhanced.Student;

public class GetSubTypeOidRange extends DODMEnhancedTester {  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    
    Class[] classes = {
        Person.class,
        Student.class,
        Instructor.class,
        Staff.class,
        City.class
    };
    
    // register class
    me.registerDataSourceSchemas(getDODM(), classes);
    me.addClasses(classes);
    
    Class cls = Student.class;
    
    // do task
    Tuple2<Oid,Oid> idRange = me.getOidRange(cls);
    Oid min = idRange.getFirst();
    Oid max = idRange.getSecond();
    
    System.out.printf("ID range of %s: [%s,%s]%n", cls, min, max);
  }
}
