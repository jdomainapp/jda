package jda.test.app.courseman.extended.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.extended.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        })
public class RegisterClassSuite {
  // define a test suite
}
