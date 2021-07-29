package jda.test.dodm.objectpool.suites.browse.withbuffer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.GetStudentOidRange;
import jda.test.dodm.objectpool.units.browse.withbuffer.BrowseAndRemoveFirst;
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
        BrowseAndRemoveFirst.class,
        Spacer.class,
        GetStudentOidRange.class,
        })
public class BrowseAndRemoveFirstSuite {
  // define a test suite
}
