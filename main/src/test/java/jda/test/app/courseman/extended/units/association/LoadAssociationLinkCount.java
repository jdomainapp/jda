package jda.test.app.courseman.extended.units.association;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Student;


public class LoadAssociationLinkCount extends CourseManExtendedTester {
  
  @Test
  public void doTest() throws DataSourceException {
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c1 = Student.class;
    DAttr attrib1 = schema.getDomainConstraint(c1, "enrolments");
    Class c2 = Enrolment.class;
    DAttr attrib2 = schema.getDomainConstraint(c2, "student");
    
    // get an object for testing
    Tuple2<Oid,Object> t = getObject(c1, new Object[] {"S2015"});
    Student s = (Student) t.getSecond();
    Oid sid = t.getFirst();
    
    System.out.printf("%s%n", s);
    
    int linkCount = dom.loadAssociationLinkCount(c2, attrib2, s, sid); 

    System.out.printf("Loaded link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCount);
  }  
}
