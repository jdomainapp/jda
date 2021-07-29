package jda.test.app.courseman.basic.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.CreateStudentObjects;
import jda.test.app.courseman.basic.units.common.PrintData;
import jda.test.app.courseman.basic.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        CreateStudentObjects.class,
        PrintData.class,
        })
public class CreateStudentObjectsSuite {
  // define a test suite
}
