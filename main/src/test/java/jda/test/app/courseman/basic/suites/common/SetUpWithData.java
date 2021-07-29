package jda.test.app.courseman.basic.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.AddAndCreate;
import jda.test.app.courseman.basic.units.common.CreateObjects;
import jda.test.app.courseman.basic.units.common.PrintData;
import jda.test.app.courseman.basic.units.common.RemoveClassAndObjectsFromDB;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RemoveClassAndObjectsFromDB.class,
        AddAndCreate.class,
        CreateObjects.class,
        PrintData.class,
        })
public class SetUpWithData {
  // define a test suite
}
