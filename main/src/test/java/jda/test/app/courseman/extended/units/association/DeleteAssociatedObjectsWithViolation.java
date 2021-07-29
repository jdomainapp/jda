package jda.test.app.courseman.extended.units.association;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Enrolment;
import jda.test.model.extended.Student;


public class DeleteAssociatedObjectsWithViolation extends CourseManExtendedTester {
  
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
    //      throw new ConstraintViolationException(ConstraintViolationException.Code.CARDINALITY_CONSTRAINT_NOT_SATISFIED,
    //          "linked-objects({0}) < minCard({1})", linkCount, minCard);

    // create some new Enrolments until violate max card
    int invalid = linkCount - minCard + 1;
    
    // get enrolments of s
    Query q = new Query();
    q.add(new Expression("student", Op.EQ, s));
    Collection<Enrolment> enrolments = (Collection<Enrolment>) dom.getObjects(c2, q);
    
    Enrolment e;
    Oid eid;
    System.out.printf("Deleting %d %s objects...%n", invalid, c2.getSimpleName());
    for (int i = 0; i < invalid; i++) {
      System.out.print("Validating association constraint on DELETE...");
      // validate constraint first
      try {
        dom.validateCardinalityConstraintOnDelete(c2, attrib2, s, sid, 
            -1   // uses this to fetch the link count directly from the object 
            //linkCountUpdated, // uses this if link count is already fetched
            );
      } catch (Exception ex) {
        System.out.println("ERROR!");
        throw ex;
      }

      // ok create
      System.out.println("ok.\n Deleting object...");
      
      e = getObject(enrolments, i);
      eid = dom.lookUpObjectId(c2, e);
      dom.deleteObject(e, eid, c2);
      
      System.out.println("  " + e);

      // update student object
      dom.updateAssociateToRemoveLink(s, e, attrib2);
      
      // check link count
      linkCountUpdated = getAssociationLinkCount(c1, attrib1, s);
      System.out.printf("  Updated link count(%s.%s) = %d%n", s.getId(), attrib1.name(), linkCountUpdated);
    }
  }  
}
