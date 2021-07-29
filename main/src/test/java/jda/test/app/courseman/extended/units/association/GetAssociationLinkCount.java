package jda.test.app.courseman.extended.units.association;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Student;


public class GetAssociationLinkCount extends CourseManExtendedTester {
  
  @Test
  public void doTest() throws Exception {
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c1 = Student.class;
    DAttr attrib1 = schema.getDomainConstraint(c1, "enrolments");
    
    // get an object for testing
    Tuple2<Oid,Object> t = getObject(c1, new Object[] {"S2015"});
    Student s = (Student) t.getSecond();
    Oid sid = t.getFirst();
    
    System.out.printf("%s%n", s);
    
    // get and update the association link count into object
    
    int linkCount = getAssociationLinkCount(c1, attrib1, s);

    System.out.printf("Current link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCount);
    
  }  
}
