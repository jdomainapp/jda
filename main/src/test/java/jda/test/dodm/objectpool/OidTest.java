package jda.test.dodm.objectpool;

import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Student;

public class OidTest extends DODMEnhancedTester {
  @Test
  public void doTest() {
    Class c = Student.class;
    
    DODMBasic dodm = instance.getDODM();
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    dodm.registerClass(Student.class);
    
    DAttr idAttrib = dsm.getDomainConstraint(c, "id");
    
    Comparable idVal = "S2014";
    
    Oid oid1 = new Oid(c);
    oid1.addIdValue(idAttrib, idVal);
    
    DAttr idAttrib2 = dsm.getDomainConstraint(c, "id");
    Comparable idVal2 = "S" + 2014;
    Oid oid2 = new Oid(c);
    oid2.addIdValue(idAttrib2, idVal2);
    
    System.out.printf("%s.equals(%s) = %b%n", oid1, oid2, oid1.equals(oid2));
  }
}
