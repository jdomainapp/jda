package jda.test.app.courseman.enhanced.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.common.AddAndCreate;
import jda.test.app.courseman.enhanced.units.common.CreateObjects;
import jda.test.app.courseman.enhanced.units.common.PrintData;
import jda.test.app.courseman.enhanced.units.common.RegisterClass;
import jda.test.app.courseman.enhanced.units.common.RemoveAndDeleteFromDB;


/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RemoveAndDeleteFromDB.class,
        RegisterClass.class,
        AddAndCreate.class,
        CreateObjects.class,
        PrintData.class,
        })
public class SetUpWithData {
  // define a test suite
}
