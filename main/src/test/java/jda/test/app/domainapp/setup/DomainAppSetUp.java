package jda.test.app.domainapp.setup;

import java.util.List;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.lang.ModuleLanguageConfigurator;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.modules.applicationmodule.ModuleApplicationModule;
import jda.test.app.domainapp.setup.modules.ModuleDomainApp;
import jda.util.SwTk;

/**
 * @overview
 *  A <tt>SetUp</tt> for the DomainApp test application. 
 *  
 * @author dmle
 *
 */
public class DomainAppSetUp extends SetUpBasic {

  private String dbName;
  
  /** module settings */
  private static final Class[][] moduleDescriptors = 
    {
      // group 1: system modules
      {
        ModuleLanguageConfigurator.class,
        //ModuleApplicationModule.class
      }, 
      // group 2: domain-specific modules
      {
        ModuleDomainApp.class,
      }
   };
  
  public DomainAppSetUp(String dbName) {
    super();
    this.dbName = dbName;
  }
  
  @Override
  public Class[] getModelClasses() {
    return getModelClasses(moduleDescriptors);
  }

  @Override
  public List<List<Class>> getModuleDescriptors() {
    return getModuleDescriptors(moduleDescriptors);
  }

//  @Override
//  protected String getDBName() {
//    return dbName;
//  }

  @Override
  public Configuration createInitApplicationConfiguration() {
    final String AppName = "DomainAppTest";
    Configuration config = SwTk.createSimpleConfigurationInstance(AppName, dbName);
    
    return config;
  }
  
  @Override
  public void createApplicationConfiguration() throws NotPossibleException,
      NotFoundException {
    // create an application configuration for testing
    config = createInitApplicationConfiguration();
//    final String AppFolder = "/home/dmle/tmp/testApp";
//    final String SetUpFolder = System.getProperty("user.dir");
//    
//    // set language and label constant class for that language
//    final Language Lang =
//        Language.English;
//        //Language.Vietnamese;
//    final String labelConstantClass = null;
//    
//    config = new Configuration(dbName);
//    config.setAppName(AppName);
//    config.setSetUpFolder(SetUpFolder);
//    config.setAppFolder(AppFolder);
//
//    config.setLanguage(Lang);
//    config.setLabelConstantClass(labelConstantClass);

    validate();      
  }
}
