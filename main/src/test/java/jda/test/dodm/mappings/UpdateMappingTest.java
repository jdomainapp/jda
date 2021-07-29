package jda.test.dodm.mappings;

import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.model.Mapping;
import jda.test.dodm.DODMCustomTester;
import jda.test.model.basic.Student;

public class UpdateMappingTest extends DODMCustomTester {
  
  // v2.8: not yet tested
//  @Test
//  public void doTest() throws Exception {
//    // register Mapping class
//    Class[] enumClasses = {DomainConstraint.Type.class};
//    
//    instance.registerEnumClasses(enumClasses);
//    instance.registerClass(Mapping.class);
//    
//    // register a test domain class (e.g. Student) 
//    Class[] domainClasses = {
//		//City.class, 
////		Module.class,
////		CompulsoryModule.class,
////		ElectiveModule.class,
////		Enrolment.class,
//      Student.class,
////		SClass.class
//    };
//    Class[] changes = {Student.class};
//
//    instance.registerClasses(domainClasses);
//    
//    Class c = domainClasses[0];
//    
//    // generate Mapping objects from the domain class
//    DODMBasic dodm = instance.getDomainSchema();
//    DSM dsm = (DSM) dodm.getDsm();
//    DOM dom = (DOM) dodm.getDom();
//    
//    // update mappings (to reflect the changes above)
//    dom.updateMappings(c);
//    
//    // print the result in the db
//    Class[] toPrint = {Mapping.class}; 
//    printDataDB(toPrint);
//    
//    printDataDB(changes);
//  }
}
