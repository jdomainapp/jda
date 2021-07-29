package jda.test.dodm.mappings;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.model.Mapping;
import jda.test.app.courseman.basic.CourseManBasicTester;

public class DeleteMappings extends CourseManBasicTester {

	@Test
	public void doTest() throws DataSourceException {
		// register Mapping class
		Class[] enumClasses = { DAttr.Type.class };

		instance.registerEnumClasses(enumClasses);

		Class c = Mapping.class;
		instance.registerClass(c);
		instance.loadObjects(c);

		instance.deleteObjects(c, true);

	}
}
