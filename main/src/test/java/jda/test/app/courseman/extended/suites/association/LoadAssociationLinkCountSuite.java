package jda.test.app.courseman.extended.suites.association;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.app.courseman.extended.units.association.LoadAssociationLinkCount;
import jda.test.app.courseman.extended.units.common.LoadObjects;
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
  LoadObjects.class,
  Spacer.class,
  LoadAssociationLinkCount.class,
        })
public class LoadAssociationLinkCountSuite {
  // define a test suite
}
