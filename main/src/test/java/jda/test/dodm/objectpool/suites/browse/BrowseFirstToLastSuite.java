package jda.test.dodm.objectpool.suites.browse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.browse.BrowseFirstToLast;
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
        BrowseFirstToLast.class
        })
public class BrowseFirstToLastSuite {
  // define a test suite
}
