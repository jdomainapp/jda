package jda.test.dodm.objectpool.suites.browse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.AddNewStudents;
import jda.test.dodm.objectpool.units.GetStudentOidRange;
import jda.test.dodm.objectpool.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        GetStudentOidRange.class,
        Spacer.class,
        AddNewStudents.class,
        Spacer.class,
        GetStudentOidRange.class,
        Spacer.class,
        BrowseLastToFirstSuite.class,
        })
public class AddStudentsAndBrowseSuite {
  // define a test suite
}
