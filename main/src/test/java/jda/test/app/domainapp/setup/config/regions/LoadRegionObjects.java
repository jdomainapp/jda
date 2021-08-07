package jda.test.app.domainapp.setup.config.regions;

import java.io.IOException;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.mccl.conceptmodel.view.ExclusionMap;
import jda.test.app.domainapp.setup.config.TestSetUpConfig;


public class LoadRegionObjects extends TestSetUpConfig {  
  
  @Test
  public void doTest() throws DataSourceException, IOException {
    System.out.println(this.getClass().getSimpleName());

    ((TestSetUpConfig) instance).registerConfigurationSchema();

    ((TestSetUpConfig) instance).loadRegions();
    
    Class c = ExclusionMap.class;
    
    printDataMemory(c);
    
  }
}
