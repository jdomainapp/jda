package jda.test.modules.help;

import org.junit.Test;

import jda.modules.help.model.AppHelp;
import jda.modules.help.model.HelpContent;
import jda.modules.help.model.HelpItem;
import jda.test.app.courseman.basic.CourseManBasicTester;

public class DeleteHelpClass extends CourseManBasicTester {
	@Test
	public void doTest() throws Exception {
		instance.removeClassAndDeleteFromDB(HelpItem.class);
		instance.removeClassAndDeleteFromDB(HelpContent.class);
		instance.removeClassAndDeleteFromDB(AppHelp.class);
	}
}
