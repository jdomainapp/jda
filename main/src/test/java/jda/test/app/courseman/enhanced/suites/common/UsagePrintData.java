package jda.test.app.courseman.enhanced.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.common.PrintData;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadData.class,
        PrintData.class,
        })
public class UsagePrintData {
  // define a test suite
}
