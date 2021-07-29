package jda.test.app.courseman.enhanced.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.suites.common.LoadData;
import jda.test.app.courseman.enhanced.units.DeleteObject;
import jda.test.app.courseman.enhanced.units.common.PrintData;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadData.class,
        DeleteObject.class,
        PrintData.class,
        })
public class UsageDeleteObject {
  // define a test suite
}
