package jda.test.dodm.objectpool;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.City;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class ObjectLifeTest extends DODMEnhancedTester {
  @Test
  public void doTest() throws DataSourceException {
    Class<Student> c = Student.class;
    
    DODMBasic dodm = instance.getDODM();
    DSMBasic dsm = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class[] toRegister = {
        City.class,
        SClass.class,
        Student.class
    };
    
    for (Class cls : toRegister)
      dodm.registerClass(cls);
    
    // (1) Create an object browser 
    //TestDBMainObjectPool pool = (TestDBMainObjectPool) instance; 
    DODMTesterWithBrowse<Student> browser = new DODMTesterWithBrowse<>();
    browser.initObjectBuffer(c);
    
    // (2) Browse the first object
    Student s = browser.browseFirst();
    Oid oid1 = dom.lookUpObjectId(c, s);
    
    // (3) Search for certain objects
    Query q = new Query();
    DAttr attrib = dsm.getDomainConstraint(c, "id");
    q.add(new ObjectExpression(
        c, 
        attrib,
        Op.MATCH,
        "%2014%"));
    
    Collection<Oid> oids = dom.retrieveObjectOids(c, q);
    
    // (4) Browse the first object in the search result
    browser = new DODMTesterWithBrowse();
    browser.initObjectBuffer(c, oids);

    Student s2 = browser.browseFirst();
    Oid oid2 = dom.lookUpObjectId(c, s2);
    
    // (5) Compare (2) and (4): both objects must be the same w.r.t the operator ==
    boolean sameObject = s == s2;
    boolean sameOidHash = oid1.hashCode() == oid2.hashCode();
    
    System.out.printf("s1: %s%ns2: %s%n  same object? %b%n", s, s2, sameObject);
    System.out.printf("oid1 = %s%noid2 = %s%n  same oid? %b%n  hash1 = %d%n  hash2 = %d%n  same oid hash? %b%n", 
        oid1, oid2, oid1.equals(oid2),
        oid1.hashCode(), oid2.hashCode(),
        sameOidHash);
    
    assert sameObject : "Objects need be the same";
    assert sameOidHash: "Object hash codes need be the same";
  }
}
