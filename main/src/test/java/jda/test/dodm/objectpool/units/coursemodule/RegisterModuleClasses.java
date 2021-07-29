package jda.test.dodm.objectpool.units.coursemodule;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.CompulsoryModule;
import jda.test.model.basic.ElectiveModule;
import jda.test.model.basic.Module;


public class RegisterModuleClasses extends DODMEnhancedTester {  
  
  @Test
  public void doTest() throws DataSourceException { 
    instance.registerClass(Module.class);
    instance.registerClass(CompulsoryModule.class);
    instance.registerClass(ElectiveModule.class);
  }
}
