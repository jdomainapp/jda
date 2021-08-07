package jda.software.javadbserver.setup;

import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.javadbserver.ModuleJavaDbServer;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.setup.model.SetUp;
import jda.modules.setup.model.SetUpConfig;
import jda.mosa.view.assets.GUIToolkit;
import jda.software.javadbserver.modules.ModuleJavaDbServerMain;
import jda.util.SwTk;

public class JavaDbServerSetUp extends SetUp {

  private static final boolean debug = Toolkit.getDebug(JavaDbServerSetUp.class);

  private static final Class[][] systemModuleDescriptors = 
    {
      {
        //ModuleSplashScreen.class,
        ModuleJavaDbServer.class,
      }, 
   };
  
  /** NO domain-specific configuration settings */
  private static final Class[][] moduleDescriptors = {
    {
      ModuleJavaDbServerMain.class
    }
  };

  @Override
  public Configuration createInitApplicationConfiguration() {
    return SwTk.createMemoryBasedConfiguration("JavaDbServer");
//    if (isEmbedded) {
//      return ApplicationToolKit.createDefaultInitApplicationConfiguration("JavaDbServer", DBName);
//    } else {
//      return createClientServerApplicationConfiguration();
//    }
  }
  
//  private Configuration createClientServerApplicationConfiguration() {
//    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientServerConfig("derby", clientUrl, serverUrl);
//    
//    Configuration config = ApplicationToolKit.createInitApplicationConfiguration("JavaDbServer", osmConfig);
//    return config;
//  }
  
  @Override
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
//        Language.English; 
        Language.Vietnamese;
  //v3.0: final String labelConstantClass = 
        //vn.com.courseman.setup.config.lang.vi.DomainLabelConstants.class.getName();
  //       null; 
    
    config = createInitApplicationConfiguration(); 
    
    // customise setup config 
    config.setSetUpConfigurationType(SetUpConfig.class);

    // style constants used
    // default language used
    config.setLanguage(Lang);
    
    // domain specific constant class
  //v3.0: config.setLabelConstantClass(labelConstantClass);
    
    // the default
    //config.setListSelectionTimeOut(25);
    //config.setMainGUISizeRatio(-1);
    //config.setChildGUISizeRatio(0.75);
    config.setUseSecurity(false);
    //config.setUseSecurity(false);
    
    /* comment these out will cause the display of Login dialog*/
    //config.setUserName("duclm");
    //config.setPassword("duclm");
    
    config.setDefaultModule(ModuleJavaDbServer.class.getSimpleName());
    
    // organisation
    /*
     * If organisation uses a logo picture (preferred format: gif) then 
     * GUIToolkit.initInstance(conig) must be invoked first (see below) 
     */
    GUIToolkit.initInstance(config);
    
//    config.setOrganisation("Trường mầm non song ngữ EduPlay Garden", 
//        GUIToolkit.getImageIcon("logo.png", null), 
//        "Tòa nhà B, Vinaconex 1 - 289A Khuất Duy Tiến - Cầu giấy - HN", 
//        "http://http://eduplaygarden.edu.vn");
//
//    // v2.7.4: create splash screen info
//    createSplashInfo(config, "applogo.png");
    
    validate();  
  }

  @Override
  public List<List<Class>> getSystemModuleDescriptors() {
    return 
        getModuleDescriptors(systemModuleDescriptors);
  }
  
  @Override
  public Class[] getSystemModelClasses() {
    return getModelClasses(systemModuleDescriptors);
  }
  
  @Override
  public Class[] getModelClasses() {
    return getModelClasses(moduleDescriptors);
  }

  public List<List<Class>> getModuleDescriptors() {
    //return appModel;
    return getModuleDescriptors(moduleDescriptors);
  }
}
