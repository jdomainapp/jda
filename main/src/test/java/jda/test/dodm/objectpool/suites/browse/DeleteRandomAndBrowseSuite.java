package jda.test.dodm.objectpool.suites.browse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.DeleteRandomStudent;
import jda.test.dodm.objectpool.units.GetStudentOidRange;
import jda.test.dodm.objectpool.units.LoadStudents;
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
        LoadStudents.class,
        Spacer.class,
        DeleteRandomStudent.class,
        Spacer.class,
        GetStudentOidRange.class,
        Spacer.class,
        BrowseFirstToLastSuite.class
        })
public class DeleteRandomAndBrowseSuite {
  // define a test suite
}
