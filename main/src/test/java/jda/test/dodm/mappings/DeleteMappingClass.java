package jda.test.dodm.mappings;

import org.junit.Test;

import jda.modules.dodm.model.Mapping;
import jda.test.app.courseman.basic.CourseManBasicTester;

public class DeleteMappingClass extends CourseManBasicTester {
	@Test
	public void doTest() throws Exception {
		instance.removeClassAndDeleteFromDB(Mapping.class);
	}
}
