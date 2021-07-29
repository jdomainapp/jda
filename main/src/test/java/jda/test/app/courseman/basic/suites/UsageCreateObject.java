package jda.test.app.courseman.basic.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.suites.common.LoadObjectsSuite;
import jda.test.app.courseman.basic.units.CreateObject;
import jda.test.app.courseman.basic.units.common.PrintData;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadObjectsSuite.class,
        CreateObject.class,
        PrintData.class,
        })
public class UsageCreateObject {
  // define a test suite
}
