package jda.test.app.courseman.basic.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.PrintData;
import jda.test.app.courseman.basic.units.common.PrintDataDB;
import jda.test.app.courseman.basic.units.common.Spacer;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PrintDataDB.class,
        Spacer.class,
        LoadObjectsSuite.class,
        PrintData.class,
        })
public class UsagePrintDataSuite {
  // define a test suite
}
