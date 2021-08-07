package jda.modules.setup.model;

import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.setup.ModuleSetUp;
import jda.mosa.view.assets.GUIToolkit;
import jda.software.setup.modules.ModuleSetUpMain;
import jda.util.SwTk;

/**
 * @overview 
 *    This is a special set-up in that apart from its own configuration, it also contains a reference to the configuration 
 *    of another set-up (called the <b>target set-up</b>). This target set-up is the set-up object of the application 
 *    that this module is designed to run. 
 *     
 * @author dmle
 * @version 2.8
 */
@DClass(serialisable=false)
public class MasterSetUp extends SetUp {
  
  @DAttr(name = "id",id = true, auto = true, type = DAttr.Type.Integer, length = 6, optional = false, mutable = false)
  private int id;
  private static int idCounter;
  
  private SetUpBasic targetSetUp;
  
  /** derived from {@link #targetSetUp} */
  @DAttr(name="targetConfig",type=DAttr.Type.Domain,auto=true)
  @DAssoc(ascName="setUp-has-targetConfig",role="setUp",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=Configuration.class,cardMin=1,cardMax=1,updateLink=false)
  )
  private Configuration targetConfig;
  
  /** derived from {@link #targetSetUp} */
  @DAttr(name="targetStatus",type=DAttr.Type.String,
      length=100, // v3.0: to use text area
      auto=true,mutable=false)
  private String targetStatus;

  @DAttr(name="targetCommand",type=DAttr.Type.Domain,
      optional=false)
  private Cmd targetCommand;
  
  //public static final String DBName = "data/SetUp";

//  private static final String clientUrl = "//localhost:1527/"+ DBName;
//  private static final String serverUrl = "//:1527";
//  private static final boolean isEmbedded = false;

  private static final boolean debug = Toolkit.getDebug(MasterSetUp.class);

  private static final Class[][] systemModuleDescriptors = 
    {
      // modules needed to operate on Configuration 
      SetUpConfig.getConfigurationModules(),
      {
        ModuleSetUp.class,
      }, 
   };
  
  /** NO domain-specific configuration settings */
  private static final Class[][] moduleDescriptors = {
    {
      ModuleSetUpMain.class
    }
  };


  public MasterSetUp() {
    super();
    idCounter++;
    id = idCounter;
  }

  public int getId() {
    return id;
  }

  public SetUpBasic getTargetSetUp() {
    return targetSetUp;
  }

  /**
   * @requires 
   * targetSetUp != null
   */
  public void setTargetSetUp(SetUpBasic targetSetUp) {
    this.targetSetUp = targetSetUp;
    targetConfig = targetSetUp.getConfig();    
  }

  public Configuration getTargetConfig() {
    return targetConfig;
  }

  /**
   * stub-method used by view to add association link
   */
  public void setTargetConfig(Configuration config) {
    //donot need to do this
    // this.targetConfig = config;
  }

  public Cmd getTargetCommand() {
    return targetCommand;
  }

  public void setTargetCommand(Cmd targetCommand) {
    this.targetCommand = targetCommand;
  }

  /**
   * @effects  
   *  if this.targetSetUp != null
   *    return this.targetSetUp.status
   *  else
   *    return null
   */
  public String getTargetStatus() {
    if (targetSetUp != null)
      targetStatus = targetSetUp.getStatus();
    
    return targetStatus;
  }

  /**
   * @effects 
   *  if target-setup has new status message
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   *  
   * @version 3.0
   */
  public boolean getNewTargetStatus() {
    if (targetSetUp != null) {
      String prevStatus = targetStatus;
      String currStatus = targetSetUp.getStatus();
      if (currStatus != null) {
        if (prevStatus != null) {
          return prevStatus.length() != currStatus.length();
        } else {
          return true;
        }
      }
    }
    
    return false;
  }
  
  @Override
  public Configuration createInitApplicationConfiguration() {
    Configuration config = SwTk.createMemoryBasedConfiguration("SetUp");
    
    // customise dodm types
    DODMConfig dodmCfg = config.getDodmConfig();
    
    // use the extended JavaDb OSM
    dodmCfg.setDsmType(DSM.class);
    dodmCfg.setDomType(DOM.class);
    
    return config;
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
    //     null; 
    
    config = createInitApplicationConfiguration(); 
    
    // customise setup config 
    config.setSetUpConfigurationType(MasterSetUpConfig.class);

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
    
    config.setDefaultModule(ModuleSetUp.class.getSimpleName());
    
    config.setSetUpInstance(this);
    
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
