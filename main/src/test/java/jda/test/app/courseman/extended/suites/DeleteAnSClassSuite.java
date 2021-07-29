package jda.test.app.courseman.extended.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.app.courseman.extended.units.DeleteAnSClass;
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
        DeleteAnSClass.class,
        Spacer.class,
        PrintDataDB.class
        })
public class DeleteAnSClassSuite {
  // define a test suite
}
