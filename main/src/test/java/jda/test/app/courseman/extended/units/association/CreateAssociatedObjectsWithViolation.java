package jda.test.app.courseman.extended.units.association;

import org.junit.Test;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Module;
import jda.test.model.extended.Student;


public class CreateAssociatedObjectsWithViolation extends CourseManExtendedTester {
  
  @Test
  public void doTest() throws Exception {
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

    int minCard = getAssociationCardMin(c1, attrib1);
    int maxCard = getAssociationCardMax(c1, attrib1);

    System.out.printf("Association (%s.%s): minCard(%d), maxCard(%d)%n", c1.getSimpleName(), attrib1.name(), minCard, maxCard);

    if (maxCard < linkCount)
      throw new ConstraintViolationException(ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
          new Object[] {"", "", minCard, maxCard, "", linkCount});
//      throw new ConstraintViolationException(ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
//          "maxCard({0}) < linked-objects({1})", maxCard, linkCount);

    if (minCard > linkCount)
      throw new ConstraintViolationException(ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
          new Object[] {"", "", minCard, maxCard, "", linkCount});

    // create some new Enrolments until violate max card
    int invalid = maxCard - linkCount + 1;
    
    Enrolment e;
    Module m = (Module) getObject(Module.class, new Object[] {"M300"}).getSecond();
    double mark1 = 2.0, mark2 = 3.0;
    System.out.printf("Creating %d new %s objects...%n", invalid, c2.getSimpleName());
    for (int i = 0; i < invalid; i++) {
      System.out.print("Validating association constraint on CREATE NEW...");
      // validate constraint first
      try {
        dom.validateCardinalityConstraintOnCreate(c2, attrib2, s, sid, 
            -1   // uses this to fetch the link count directly from the object 
            //linkCountUpdated, // uses this if link count is already fetched
            );
      } catch (Exception ex) {
        System.out.println("ERROR!");
        throw ex;
      }

      // ok create
      System.out.println("ok.\n Creating new object...");
      e = (Enrolment) dom.createObject(c2, new Object[] {s,m,mark1,mark2}).getSecond();
      System.out.println("  " + e);

      /*v2.7.3: 
       dom.updateAssociateLink(s, attrib1.name(), e);
       */
      dom.addAssociateLink(s, attrib1.name(), e);
      
      // check link count
      linkCountUpdated = getAssociationLinkCount(c1, attrib1, s);
      System.out.printf("  Updated link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCountUpdated);
    }
  }  
}
