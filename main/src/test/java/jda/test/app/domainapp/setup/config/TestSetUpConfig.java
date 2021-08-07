package jda.test.app.domainapp.setup.config;

import java.io.IOException;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.view.ExclusionMap;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.app.domainapp.setup.DomainAppSetUp;

/**
 * @overview
 *  The main test driver for VendingMachine application example
 * 
 * @author dmle
 *
 */
public class TestSetUpConfig extends CourseManBasicTester {
  
  private SetUpBasic su;
  
  public void initClasses() throws NotPossibleException {
    method("initClasses()");

    //domainClasses = SetUpConfiguration.cfgClasses;
    // no domain classes here because we will use SetUpConfiguration class
    domainClasses = getConfigurationSchema(); 
    
    DODMBasic schema = getDODM(); 
    
    String dbName = getDataSourceName();
    
    su = new DomainAppSetUp(dbName);
  }
  
  protected SetUpBasic getSetUp() {
    return su;
  }
  
  public void registerConfigurationSchema() throws DataSourceException, IOException {
    SetUpBasic su = ((TestSetUpConfig) instance).getSetUp();

    Cmd cmd = Cmd.RegisterConfigurationSchema;
    
    su.run(cmd, null);
  }

  public void loadConfiguration() throws DataSourceException, IOException {
    SetUpBasic su = ((TestSetUpConfig) instance).getSetUp();

    Cmd cmd = Cmd.LoadConfiguration;
    
    su.run(cmd, null);
  }

  public Class[] loadRegions() throws DataSourceException {
    Class[] classes = {
        Label.class, 
        jda.modules.setup.init.lang.vi.Label.class,
        jda.modules.setup.init.lang.en.Label.class, //
        Style.class, //
        Region.class, RegionGui.class, RegionToolMenuItem.class,
        RegionDataField.class, //
        RegionLinking.class, // v2.6.4.b
        RegionMap.class, //
        ExclusionMap.class, //
    };
    
    for (Class c : classes) {
      loadObjects(c);
    }
    
    return classes;
  }

  public void loadAssociatedObjects(Object o) throws NotFoundException, NotPossibleException {
    DODMBasic schema = instance.getDODM();
    
    schema.getDom().retrieveAssociatedObjects(o);
  }
}
