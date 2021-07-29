package jda.test.app.courseman.basic.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.DeleteObjects;
import jda.test.app.courseman.basic.units.common.PrintDataDB;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadObjectsSuite.class,
        DeleteObjects.class,
        PrintDataDB.class,
        })
public class DeleteObjectsSuite {
  // define a test suite
}
