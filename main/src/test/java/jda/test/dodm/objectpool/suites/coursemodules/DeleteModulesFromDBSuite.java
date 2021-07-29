package jda.test.dodm.objectpool.suites.coursemodules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.coursemodule.DeleteModulesFromDB;
import jda.test.dodm.objectpool.units.coursemodule.GetModuleOidRange;
import jda.test.dodm.objectpool.units.coursemodule.LoadCourseModules;
import jda.test.dodm.objectpool.units.coursemodule.RegisterModuleClasses;

/**
 * The setup test case
 * @author dmle
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterModuleClasses.class,
        Spacer.class,
        GetModuleOidRange.class,
        Spacer.class,
        LoadCourseModules.class,
        Spacer.class,
        GetModuleOidRange.class,
        Spacer.class,
        DeleteModulesFromDB.class,
        Spacer.class,
        GetModuleOidRange.class,
        })
public class DeleteModulesFromDBSuite {
  // define a test suite
}
