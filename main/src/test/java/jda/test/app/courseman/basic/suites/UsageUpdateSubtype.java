package jda.test.app.courseman.basic.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.suites.common.LoadObjectsSuite;
import jda.test.app.courseman.basic.units.UpdateSubtype;
import jda.test.app.courseman.basic.units.common.PrintData;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadObjectsSuite.class,
        UpdateSubtype.class,
        PrintData.class,
        })
public class UsageUpdateSubtype {
  // define a test suite
}
