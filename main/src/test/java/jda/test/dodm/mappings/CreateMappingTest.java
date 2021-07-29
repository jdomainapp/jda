package jda.test.dodm.mappings;

import java.util.Collection;

import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.model.Mapping;
import jda.test.dodm.DODMCustomTester;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;

public class CreateMappingTest extends DODMCustomTester {
	@Test
	public void doTest() throws Exception {

		// register Mapping class
		Class[] enumClasses = { DAttr.Type.class };

		instance.registerEnumClasses(enumClasses);
		instance.registerClass(Mapping.class);

		// register a test domain class (e.g. Student)
    Class[] domainClasses = {
    //City.class, 
//    Module.class,
//    CompulsoryModule.class,
//    ElectiveModule.class,
//    Enrolment.class,
      Student.class,
//    SClass.class
    };
    Class[] toPrint = { Mapping.class, Student.class };
    
		instance.registerClasses(domainClasses);

		// generate Mapping objects from the domain class
		DODMBasic dodm = instance.getDODM();
		DSM dsm = (DSM) dodm.getDsm();
		DOM dom = (DOM) dodm.getDom();

		for (Class c : domainClasses) {
		  // add class
		  instance.addClass(c, true, true);
		  
		  // create mapping
			Collection<Mapping> mappings = dsm.generateMappings(c);

			// store Mapping objects to database
			if (mappings != null) {
				dom.addObjects(mappings);
			}
			
			break;
		}

		// print the result in the db
		printDataDB(toPrint);

	}
}
