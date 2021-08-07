package jda.test.app.domainapp.setup.config;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionGui;


public class LoadConfigurationExclusions extends TestSetUpConfig {  
  
  @Test
  public void doTest() throws DataSourceException, IOException {
    System.out.println(this.getClass().getSimpleName());
    
    TestSetUpConfig testMain = ((TestSetUpConfig) instance); 
    testMain.registerConfigurationSchema();
    testMain.loadRegions();

    DODMBasic schema = instance.getDODM();
    
    // load the ConfigurationModule object 
    Class<ApplicationModule> c = ApplicationModule.class;
    String name = "ModuleDomainApp";
    String attrib = "name";
    ApplicationModule moduleCfg = schema.getDom().retrieveObject(c, attrib, Op.EQ, name);

    if (moduleCfg == null) {
      System.err.printf("Module not found: %s%n", name);
      return;
    }
    
    System.out.printf("Module: %s%n", moduleCfg.getName());

    RegionGui viewCfg = moduleCfg.getViewCfg();

    System.out.printf("View config: %s%n", viewCfg);
    
    testMain.loadAssociatedObjects(viewCfg);
    
    Collection<Region> parents = viewCfg.getParentRegions();
    System.out.printf("%n>>>Parent regions%n");
    printObjects(Region.class, parents);
    
    Collection<Region> children = viewCfg.getChildRegions();
    System.out.printf("%n>>>Child regions%n");
    printObjects(Region.class, children);

    Collection<Region> exclusion = viewCfg.getExcludedRegions();
    System.out.printf("%n>>>Excluded regions%n");
    printObjects(Region.class, exclusion);
  }
}
