package jda.test.app.courseman.extended.units;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import jda.modules.common.expression.Op;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;
import jda.test.model.extended.Student;


public class LoadObjectWithDependencies extends CourseManExtendedTester {
  
  @Test
  public void doTest2() throws Exception {
    //TestDBExtended me = (TestDBExtended) instance;
    System.out.println("doTest2()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c = Student.class;
    System.out.printf("Domain class: %s%n", c.getSimpleName());

    String sid = "S2014";
    Query q = new Query(new Expression("id", Op.EQ, sid));
    
//    // load all objects first
//    loadObjectsWithOid(c);
//    
//    // get object and its dependencies
//    Collection<Object> students = schema.getObjects(c, q);
    
//    if (students != null) {
    //Student s = (Student) students.iterator().next();
    
    Tuple2<Oid,Object> t = loadObjectWithOid(c, q);
    
    if (t != null) {
      // print the object and its dependencies
      Student s = (Student) t.getSecond();
      
      System.out.printf("Object: %s%n", s);
      
      // find its dependencies
      Collection<Associate> associates = dom.getLinkedAssociates(s, c);
      
      if (associates != null) {
        Class lc;
        Object linkedObj;
        DAttr linkedAttrib;
        Object v;
        AssocType type;
        AssocEndType farEndType;
        int i = 1;
        for (Associate a : associates) {
          System.out.printf("%n  Associate %d:%n",i);
          // print the details of the association link from a
          lc = a.getAssociateClass();
          linkedObj = a.getAssociateObj();
          linkedAttrib = a.getMyEndAttribute();
          type = a.getAssociationType();
          farEndType = a.getFarEndType();
          v = null;
          
          if (a.isAssociationType(AssocType.One2One) || 
              (a.isAssociationType(AssocType.One2Many) && 
                  a.isMyEndType(AssocEndType.One))) {
            // 1-1 or M-1
            // get the attribute value and print it out
            v = schema.getAttributeValue(lc, linkedObj, linkedAttrib);
            
            System.out.printf("  association (my card: %s): %s%n  class: %s%n  linked obj: %s%n  linked attribute: %s%n  linked Value: %s%n",
                farEndType, type, 
                lc.getSimpleName(), linkedObj, linkedAttrib.name(), v);
          } else {
            // 1-M
            Collection col = (Collection) linkedObj;
            int j = 1;
            
            System.out.printf("  association (my card: %s): %s%n  class: %s%n  linked obj: %s%n  linked attribute: %s%n  linked Value: %n", 
                farEndType, type, 
                lc.getSimpleName(), linkedObj, linkedAttrib.name());

            for (Object o : col) {
              System.out.printf("    %d. %s%n", j, o);
              j++;
            }
          }
          
          i++;
        }
      } else {
        System.out.println("  no dependencies loaded");
      }
    } else {
      System.out.printf("No object found");
    }
  }  
  
  // load an object and print it out to see how referenced objects are loaded
  @Ignore
  @Test
  public void doTest1() throws Exception {
    //TestDBExtended me = (TestDBExtended) instance;
    System.out.println("doTest1()");
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c = Student.class;
    System.out.printf("Domain class: %s%n", c.getSimpleName());

    String sid = "S2014";
    Query q = new Query(new Expression("id", Op.EQ, sid));
    
    // load object and its dependencies
    Tuple2<Oid,Object> t = loadObjectWithOid(c, q);
    
    if (t != null) {
      // print the object and its dependencies
      Oid oid = t.getFirst();
      Student s = (Student) t.getSecond();
      
      System.out.printf("Object: %s -> %s%n", oid, s);
      
      // find its dependencies
      Collection<Associate> associates = dom.getLinkedAssociates(s, c);
      if (associates != null) {
        Class lc;
        Object linkedObj;
        DAttr linkedAttrib;
        Object v;
        AssocType type;
        int i = 1;
        for (Associate a : associates) {
          System.out.printf("  Associate %d:%n",i);
          // print the details of the association link from a
          lc = a.getAssociateClass();
          linkedObj = a.getAssociateObj();
          linkedAttrib = a.getMyEndAttribute();
          type = a.getAssociationType();
          v = null;
          
          if (a.isAssociationType(AssocType.One2One) || 
              (a.isAssociationType(AssocType.One2Many) && 
                  a.isMyEndType(AssocEndType.One))) {
            // 1-1 or M-1
            // get the attribute value and print it out
            v = schema.getAttributeValue(lc, linkedObj, linkedAttrib);
            
            System.out.printf("  class: %s%n  linked obj: %s%n  linked attribute: %s%n  linked Value: %s%n", 
                lc.getSimpleName(), linkedObj, linkedAttrib.name(), v);
          } else {
            // 1-M
            Collection col = (Collection) linkedObj;
            int j = 1;
            
            System.out.printf("  class: %s%n  linked obj: %s%n  linked attribute: %s%n  linked Value: %n", 
                lc.getSimpleName(), linkedObj, linkedAttrib.name());

            for (Object o : col) {
              System.out.printf("    %d. %s%n", j, o);
              j++;
            }
          }
          
          i++;
        }
      } else {
        System.out.println("  no dependencies loaded");
      }
    } else {
      System.out.printf("No object found");
    }
  }  
  
  
}
