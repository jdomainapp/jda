package jda.modules.jdatool.setup;

import java.util.List;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.util.SwTk;

/**
 * @overview
 *  A sub-type of {@link SetUpBasic} that represents the application set-up class for domain application tool
 *  
 *  <br><b>Note</b>: The input domain classes must be specified from the command line
 *  
 * @author dmle
 */
public class DomainAppToolSetUp extends SetUpBasic implements DomainAppToolSetUpIntf {

  private static final String DBName = "data"+SEP+"DomainAppTool";

  private DomainAppToolSetUpHelper helper;

  public DomainAppToolSetUp() {
    super();
    helper = new DomainAppToolSetUpHelper(this);
  }
  
  @Override
  public Class[] getInputModelClasses() {
    // return the model classes
    return helper.getInputModelClasses();
  }
  
  @Override
  public Class[] getModelClasses() {
    // return the model classes together with their super classes 
    return helper.getInputModelClasses();
  }

  @Override
  public List<List<Class>> getModuleDescriptors() {
    // return the module descriptors of the model classes (if any)
    //return moduleDescriptors;
    return helper.getModuleDescriptors();
  }

  @Override
  public Configuration createInitApplicationConfiguration() {
    /* v2.7.3
    final String AppName = "DomainAppTool";
    final String AppFolder = getWellKnownAppFolder(AppName); 
    final String SetUpFolder = 
        System.getProperty("user.dir");
    
    Configuration config = new Configuration(DBName);
    config.setAppName(AppName);
    config.setSetUpFolder(SetUpFolder);
    config.setAppFolder(AppFolder);
    
    //config.setObjectSerialisable(false);

    return config;
        */
    //System.out.println("debug: " + this.getClass());

    return SwTk.createDefaultInitApplicationConfiguration("DomainAppTool", DBName);

  }
  
  public void createApplicationConfiguration() throws NotPossibleException, NotFoundException {
    /*
     * Language configuration:
     *  Vietnamese (Default): requires no LabelConstantClass to be specified
     *  Other languages (e.g. English): requires a specification of the DomainLabelConstants class 
     *      and the definition of a LabelConstants class suitable for that language in a sub-package of 
     *      the domainapp.setup.data.lang package
     *      See the two examples for Vietnamese and English in the two sub-packages: 
     *        domainapp.setup.data.lang.vn and 
     *        domainapp.setup.data.lang.en 
     *     
     */
    // set language and label constant class for that language
    final Language Lang = 
        //Language.Vietnamese;
        Language.English;
    
    config = createInitApplicationConfiguration(); //new Configuration(DBName);

    /* v3.2: use standard setup config: because module generators are created for the domain classes
    // v3.2: use tool-specific setup config type
    config.setSetUpConfigurationType(SetUpConfigTool.class);
    */
    
    config.setLanguage(Lang);
    //config.setLabelConstantClass(labelConstantClass);

    // the default
    //config.setListSelectionTimeOut(25);
    //config.setMainGUISizeRatio(-1);
    //config.setChildGUISizeRatio(0.75);
    config.setUseSecurity(false);
    //config.setUseSecurity(false);
    
    /* comment these out will cause the display of Login dialog*/
    //config.setUserName("duclm");
    //config.setPassword("duclm");
    
    //config.setDefaultModule("ModuleDomainApplicationModule");
    
    // organisation
    /*
     * If organisation uses a logo picture (preferred format: gif) then 
     * GUIToolkit.initInstance(conig) must be invoked first (see below) 
     */
//    GUIToolkit.initInstance(config);
//    
//    config.setOrganisation("D.M.Le", GUIToolkit.getImageIcon("logo.jpg", null), 
//        "", "");
    validate();  
  }

  /**
   * @modifies {@link #modelClasses}, {@link #moduleDescriptors}
   * @effects 
   *  read and load the domain class(es) whose FQN names are given in <tt>args</tt>.
   *  
   *  <p>Throws IllegalArgumentException if no valid domain classes can be loaded from <tt>args</tt>; 
   *  NotPossibleException if fails to create the application modules; 
   *  DataSourceException if fails to connect to the data source
   */
  public void loadClasses(String[] args) throws IllegalArgumentException, NotPossibleException, DataSourceException {
    helper.loadClasses(args);
  }


  @Override
  protected void createDomainConfiguration(SetUpConfigBasic sucfg,
      boolean serialised  // v2.8
      )
      throws DataSourceException, NotFoundException {
    helper.createDomainConfiguration(sucfg, serialised);
  }

}
