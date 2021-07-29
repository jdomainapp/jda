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


public class SetAssociationLinkCount extends CourseManExtendedTester {
  
  @Test
  public void doTest() throws Exception {
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c1 = Student.class;
    DAttr attrib1 = schema.getDomainConstraint(c1, "enrolments");
    Class c2 = Enrolment.class;
    DAttr attrib2 = schema.getDomainConstraint(c2, "student");
    
    // get an object for testing
    Tuple2<Oid,Object> t = getObject(c1, new Object[] {"S2014"});
    Student s = (Student) t.getSecond();
    Oid sid = t.getFirst();
    
    System.out.printf("%s%n", s);
    
    // print current link count of the object
    int linkCount = getAssociationLinkCount(c1, attrib1, s);
    System.out.printf("Current link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCount);

    // load and update link count into the object
    linkCount = dom.loadAssociationLinkCount(c2, attrib2, s, sid); 
    System.out.printf("Loaded link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCount);
    
    setAssociationLinkCount(c1, attrib1, s, linkCount);
    
    // validate link count
    int linkCountUpdated = getAssociationLinkCount(c1, attrib1, s);
    System.out.printf("Updated link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCountUpdated);
    
    assert (linkCountUpdated == linkCount);
  }  
}
