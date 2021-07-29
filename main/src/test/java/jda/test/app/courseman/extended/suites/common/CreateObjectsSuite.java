package jda.test.app.courseman.extended.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.PrintDataDB;
import jda.test.app.courseman.extended.units.common.CreateObjects;
import jda.test.app.courseman.extended.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  RegisterClass.class,
  CreateObjects.class,
  PrintDataDB.class,
        })
public class CreateObjectsSuite {
  // define a test suite
}
