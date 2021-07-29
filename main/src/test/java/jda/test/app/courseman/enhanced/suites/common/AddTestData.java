package jda.test.app.courseman.enhanced.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.common.CreateObjects;
import jda.test.app.courseman.enhanced.units.common.PrintData;
import jda.test.app.courseman.enhanced.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        CreateObjects.class,
        PrintData.class,
        })
public class AddTestData {
  // define a test suite
}
