package jda.test.app.courseman.basic.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.CreateObjectsWithoutCity;
import jda.test.app.courseman.basic.units.common.PrintDataDB;
import jda.test.app.courseman.basic.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        CreateObjectsWithoutCity.class,  // without City
        PrintDataDB.class,
        })
public class CreateObjectsWithoutCitySuite {
  // define a test suite
}
