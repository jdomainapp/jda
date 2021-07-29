package jda.test.app.courseman.enhanced.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.common.DeleteObjects;
import jda.test.app.courseman.enhanced.units.common.PrintData;


/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadData.class,
        DeleteObjects.class,
        PrintData.class,
        })
public class DeleteData {
  // define a test suite
}
