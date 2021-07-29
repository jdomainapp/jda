package jda.test.app.courseman.extended.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.app.courseman.extended.units.CreateEnrolments;
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
        CreateEnrolments.class,
        Spacer.class,
        PrintDataDB.class
        })
public class CreateEnrolmentsSuite {
  // define a test suite
}
