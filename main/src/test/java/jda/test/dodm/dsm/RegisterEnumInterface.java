package jda.test.dodm.dsm;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.security.def.LogicalAction;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.util.SwTk;


public class RegisterEnumInterface extends CourseManBasicTester {  
  
  @Test
  public void doTest() throws DataSourceException { 
    //instance.registerClass();
    // initialise a non-db schema
    String name = "Non-db";
    Configuration config = SwTk.createMemoryBasedConfiguration("unamed");
    DODMBasic schema = DODMBasic.getInstance(config);//DODM schema = DODM.getInstance(name, false);
    DSMBasic dsm = schema.getDsm();
    DOMBasic dom = schema.getDom();
    
    System.out.println("Initialised non-serialisable domain schema: " + name);
    
    Class ic = LogicalAction.class; // Resource.Type.class; //
    System.out.println("Registering enum-interface " + ic);
    schema.registerEnumInterface(ic);
    
    System.out.println("Reading definitions from schema...");
    DAttr[] constraints = dsm.getIDAttributeConstraints(ic);
    DAttr idCons = null;
    for (DAttr cons: constraints) {
      System.out.println("Constraint: " + cons.name() + ", id = " + cons.id());
      //if (cons.id())
      idCons = cons;
    }
    
    System.out.println("Reading enum constants (objects) from schema...");
    Collection objects = dom.getObjects(ic);
    Object obj = objects.iterator().next();
    for (Object o: objects) {
      System.out.println(o);
    }
    
    System.out.println("Object: " + obj);
    String[] attribNames = { "name", //"ordinal" 
        };
    
    for (int i = 0; i < attribNames.length; i++) {
      /**must use this method*/
      Object attribVal = dsm.getAttributeValue(obj, attribNames[i]);
      System.out.println("Id attribute value: " + attribVal + " (type: " + attribVal.getClass()+")");
    }
  }
}
