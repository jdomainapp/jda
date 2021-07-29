package jda.test.app.courseman.extended.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.app.courseman.extended.units.common.DeleteObjects;
import jda.test.app.courseman.extended.units.common.PrintDataDB;
import jda.test.app.courseman.extended.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        DeleteObjects.class,
        Spacer.class,
        PrintDataDB.class
        })
public class DeleteObjectsSuite {
  // define a test suite
}
