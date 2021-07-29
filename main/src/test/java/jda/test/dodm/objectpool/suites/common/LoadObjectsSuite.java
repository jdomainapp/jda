package jda.test.dodm.objectpool.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.dodm.objectpool.units.LoadSClass;
import jda.test.dodm.objectpool.units.LoadStudents;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadStudents.class,
        LoadSClass.class
        })
public class LoadObjectsSuite {

}
