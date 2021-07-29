package jda.test.dodm.objectpool.standalone;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.City;
import jda.test.model.basic.CompulsoryModule;
import jda.test.model.basic.ElectiveModule;
import jda.test.model.basic.Enrolment;
import jda.test.model.basic.Module;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class BrowseAndDeleteEnrolmentTest extends DODMTesterWithBrowse {
  
  @Test
  public void doTest() throws Exception {
    Class<Enrolment> c = Enrolment.class;
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class[] toRegister = {
        City.class,
        SClass.class,
        Student.class,
        Module.class,
        ElectiveModule.class,
        CompulsoryModule.class,
        Enrolment.class
    };
    
    instance.registerClasses(toRegister);
    
    // (1) Create a memory-based object browser 
    DODMTesterWithBrowse<Enrolment> browser = new DODMTesterWithBrowse<>();
    Collection<Oid> oids = dom.retrieveObjectOids(c, null);
    
    browser.initObjectBuffer(c, oids, true);
    
    // display the Oids
    printObjects(Oid.class, oids);
    
    // (3) repeat: move to a random object and remove it from the buffer
    while (!oids.isEmpty()) {
      Oid midId = getMiddleObject(oids);
  
      System.out.printf("%nRemoving: %s%n", midId);
      
      Enrolment rand = dom.loadObject(c, midId);
  
      oids.remove(midId);
      
      try {
        browser.removeFromBuffer(midId, rand);
      } catch (ObsoleteStateSignal e) {
        // no more objects
        assert (oids.isEmpty()) : "No more object ids in browser while still left with: \n" + oids;
        System.out.println("Browser is empty");
      }
    }
  }
}
