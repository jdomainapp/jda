package jda.test.app.courseman.enhanced.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.AddStudentObjects;
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
        AddStudentObjects.class,
        PrintData.class,
        })
public class AddStudentsData {
  // define a test suite
}
