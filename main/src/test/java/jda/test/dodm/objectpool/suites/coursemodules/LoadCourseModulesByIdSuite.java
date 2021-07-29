package jda.test.dodm.objectpool.suites.coursemodules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.coursemodule.LoadCourseModulesById;
import jda.test.dodm.objectpool.units.coursemodule.RegisterModuleClasses;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterModuleClasses.class,
        Spacer.class,
        LoadCourseModulesById.class,
        })
public class LoadCourseModulesByIdSuite {
  // define a test suite
}
