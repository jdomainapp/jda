package jda.modules.mccl.conceptmodel;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ImageIcon;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpConfigBasic;
import jda.util.ObjectComparator;

/**
 * @overview Represents application configuration parameters
 */
@DClass(schema="app_config",singleton=true)
public class Configuration 
implements Serializable 
{
  /**
   * v2.8
   */
  private static final long serialVersionUID = -2941903691620977432L;

  // constants
  /**
   * the relative path to the directory containing the image files that are used
   * by this GUI. <br>
   * The image files are used as icons in the menu and command buttons.
   */
  public final static String DEFAULT_IMG_LOCATION = "/images/";

  /**
   * the relative path to the directory containing the fonts that are used
   * by this GUI. <br>
   */
  public final static String DEFAULT_FONTS_LOCATION = "/fonts/";

  /**
   * the default ratio between the size of a child GUI and that of the parent GUI
   * that is used by the application if the actual ratio (i.e. {@link #childGUISizeRatio}) 
   * is not set (i.e. set to {@link #DEFAULT_CHILD_GUI_SIZE_RATIO}).
   */
  //TODO: change this into a read-only attribute
  private final static double defaultFixedChildGUISizeRatio = 3d/4;
  

  private final static double DEFAULT_MAIN_GUI_SIZE_RATIO = -1D;
  
  private final static double DEFAULT_CHILD_GUI_SIZE_RATIO = -1D;

  public static final String AttributeName_appName = "appName";

  /**platform-specific file separator character */
  public static final String FILE_SEPARATOR = File.separator;
  
  /**
   * Attribute name constants used by other classes.
   * IMPORTANT: change these constants if the attribute names were changed:
   * <ul>
   *  <li>{@link Configuration#language} 
   * </ul>
   */
  public static enum AttributeName {
    /**
     * {@see Configuration#language} 
     **/
    language,
  };
  
  /**
   * @overview
   *  Defines the language of the application 
   *   
   * @author dmle
   */
  public static enum Language {
    Vietnamese("vi", new Locale("vi")),
    English("en", Locale.ENGLISH);
    
    private String code;
    private Locale locale;
    
    private Language(String code, Locale locale) {
      this.code=code;
      this.locale = locale;
    }
    
    @DAttr(name="name",type=Type.String,id=true,mutable=false)
    public String getName() {
      return name();
    }
    
    public String getLanguageCode() {
      return code;
    }
    
    public Locale getLocale() {
      return locale;
    }
  };  /**end {@link Language} */
  
  /**end {@link AppResource} */
  
  @DAttr(name="id",type=Type.Integer,id=true,
      //auto=true,
      mutable=false,min=1)
  private int id;
  
  private static int idCounter;
  
  @DAttr(name=AttributeName_appName,type=Type.String,length=255)
  private String appName;
  
  @DAttr(name="appLogo",type=Type.Image,
      length=2000000 // 5MB
      )  
  private ImageIcon appLogo;
  
  @DAttr(name="version",type=Type.String,length=20)  
  private String version;
  @DAttr(name="appFolder",type=Type.String,length=255)  
  private String appFolder;
  @DAttr(name="language",type=Type.Domain,optional=false)
  private Language language;
  
  @DAttr(name="dodmConfig",type=Type.Domain)
  @DAssoc(ascName="config-has-dodmConfig",role="config",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=DODMConfig.class,cardMin=1,cardMax=1 
      ))
  private DODMConfig dodmConfig;
  
  @DAttr(name="organisation",type=Type.Domain)
  @DAssoc(ascName="config-has-organisation",role="config",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=Organisation.class,cardMin=1,cardMax=1))
  private transient Organisation organisation;
  
  /**
   * The program modules
   */
  @DAttr(name="modules",type=Type.Collection,optional=false,
      serialisable=false,filter=@Select(clazz=ApplicationModule.class)//,role="config"
  )
  //@Update(add="addModule",delete="removeModule")
  @DAssoc(ascName="config-has-modules",role="config",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))
  private transient List<ApplicationModule> modules;

  @DAttr(name="defaultModule",type=Type.String,length=30)
  private String defaultModule;
  

  @DAttr(name="setUpFolder",type=Type.String,length=255)
  private String setUpFolder;

  /** v2.7.3: moved to OSMConfig
  @DomainConstraint(name="dbName",type=Type.String,mutable=false,length=50)  
  private String dbName;
  */

  @DAttr(name="userName",type=Type.String)
  private String userName;

  @DAttr(name="password",type=Type.String)
  private String password;

  @DAttr(name="mainGUISizeRatio",type=Type.Double,min=-1)
  private double mainGUISizeRatio;
  @DAttr(name="childGUISizeRatio",type=Type.Double,min=-1)
  private double childGUISizeRatio;
  @DAttr(name="useSecurity",type=Type.Boolean)    
  private boolean useSecurity;
  
  /** the number of millisecs before the current value on a list of values of 
   * a spinner control is selected 
   * Default: 25secs */
  @DAttr(name="listSelectionTimeOut",type=Type.Integer,min=0)
  private int listSelectionTimeOut;
  
  /**
   * the location of the custom fonts
   */
  @DAttr(name="fontLocation",type=Type.String,optional=false)
  private String fontLocation;
  
  /**
   * the location of the images
   */
  @DAttr(name="imageLocation",type=Type.String,optional=false)
  private String imageLocation;

// v3.0
//  /** Name of the class that provides label constant objects */
//  @DomainConstraint(name="labelConstantClass",type=Type.String,length=255)
//  private String labelConstantClass;
  
  // v2.7.4
  @DAttr(name="styleDefClass",type=Type.String,length=255)
  private String styleDefClass;
  // derived
  private Class styleDefClassObj;

  // v2.7.4: support splashinfo
  @DAttr(name="splashInfo",type=Type.Domain)
  @DAssoc(ascName="config-has-splashinfo",role="config",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=SplashInfo.class,cardMin=1,cardMax=1 
      ))
  private transient SplashInfo splashInfo;

  @DAttr(name="company",type=Type.Domain)
  @DAssoc(ascName="config-has-company",role="config",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=Company.class,cardMin=1,cardMax=1 
      ))
  private transient Company company;

  //TODO: serialise these
  private Class<? extends SetUpConfigBasic> suCfgType;
  
//  private boolean isInternationalisationSupport;

  // v2.8: derived attribute
  private transient SetUpBasic setUpInstance;

  // link attribute
  @DAttr(name="setup",type=DAttr.Type.Domain,serialisable=false)
  private transient 
    //v3.2: MasterSetUp
    SetUpBasic
        setup;

  /**
   * @effects   
   *  initialise the default settings
   */
  public Configuration() {
    this(
        // id
        null, 
        // appName
        null, 
        // appLogo
        null, // v2.7.4
        // version
        null, 
        // appFolder
        null, 
        // language
        null, 
        // dodmConfig 
        null,
        // org
        null, 
        null, null, 
        //null, 
        null, null, null, 
        null, null, null, 
        DEFAULT_FONTS_LOCATION, DEFAULT_IMG_LOCATION, 
        // labelConstantclass
        // v3.0: null,
        // styleDefClass,   // v2.7.4
        null,
        // splashinfo
        null,
        // company
        null
        );
  }
  
//  public Configuration(String dbName) {
//    this(null, null, null, null, 
//        null, null, null, null, 
//        dbName, 
//        null, null, null, 
//        null, null, null, 
//        DEFAULT_FONTS_LOCATION, DEFAULT_IMG_LOCATION, null
//        );
//  }
  
  public Configuration(Integer id, 
      String appName, 
      ImageIcon appLogo,  // v2.7.4
      String version, String appFolder, 
      Language language,
      DODMConfig dodmConfig,  // v2.8
      Organisation organisation,
      String defaultDomainClass,
      String setUpFolder,
      //v2.7.3: String dbName,
      String userName, String password, 
      Double mainGUISizeRatio, Double childGUISizeRatio, Boolean useSecurity,
      Integer listSelectionTimeOut, 
      String fontLocation, String imageLocation, 
      // v3.0: String labelConstantClass,
      String styleDefClass,   // v2.7.4
      SplashInfo splashInfo,   // v2.7.4
      Company company
      ) {
    this.id = nextVal(id);
    this.organisation = organisation;
    this.setUpFolder = setUpFolder;
    this.appName = appName;
    this.appLogo = appLogo;
    this.version = version;
    this.appFolder = appFolder;
    //this.dbName = dbName;
    this.language = language;
    
    this.dodmConfig = dodmConfig;
    
    this.userName = userName;
    this.password = password;
    this.defaultModule = defaultDomainClass;
    
    if (mainGUISizeRatio==null)
      this.mainGUISizeRatio = DEFAULT_MAIN_GUI_SIZE_RATIO; //0.8;
    else 
      this.mainGUISizeRatio = mainGUISizeRatio;

    if (childGUISizeRatio==null)
      this.childGUISizeRatio = DEFAULT_CHILD_GUI_SIZE_RATIO; // 0.5;
    else
      this.childGUISizeRatio = childGUISizeRatio;
    
    if (useSecurity == null)
      this.useSecurity = false;
    else
      this.useSecurity = useSecurity;
    
    if (listSelectionTimeOut==null)
      this.listSelectionTimeOut = 25;
    else
      this.listSelectionTimeOut = listSelectionTimeOut;
    
    this.fontLocation = fontLocation;
    this.imageLocation = imageLocation;
    //this.labelConstantClass=labelConstantClass;
    
    this.styleDefClass = styleDefClass;
    
    this.modules=new ArrayList();
    
    suCfgType = SetUpConfigBasic.class;
    //v3.0: use property 
    // isInternationalisationSupport = true;
    
    this.splashInfo = splashInfo;
    this.company = company;
  }

  private static int nextVal(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }


  public int getId() {
    return id;
  }

  public String getDefaultModule() {
    return defaultModule;
  }
  
  public void setDefaultModule(String cls) {
    this.defaultModule = cls;
  }
  
  public String getUserName() {
    return userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }

  public String getSetUpFolder() {
    return setUpFolder;
  }
  public void setSetUpFolder(String setUpFolder) {
    this.setUpFolder = setUpFolder;
  }
  public String getAppName() {
    return appName;
  }
  public void setAppName(String appName) {
    this.appName = appName;
  }
  public ImageIcon getAppLogo() {
    return appLogo;
  }

  public void setAppLogo(ImageIcon appLogo) {
    this.appLogo = appLogo;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getAppFolder() {
    return appFolder;
  }
  
  public void setAppFolder(String appFolder) {
    this.appFolder = appFolder;
  }
  
  //TODO
  public String getImportFolder() {
    return AppResource.importDir.getName(); //"import";
  }

  // derived
  public String getImportFolderPath() {
    return getAppFolder() + File.separator + getImportFolder();
  }
  
  //TODO
  public String getExportFolder() {
    return AppResource.export.getName(); //"export";
  }

  // derived
  public String getExportFolderPath() {
    return getAppFolder() + File.separator + getExportFolder();
  }

  //TODO
  public String getConfigFolder() {
    return AppResource.config.getName(); //"config";
  }

  // derived
  public String getConfigFolderPath() {
    return getAppFolder() + File.separator + getConfigFolder();
  }

//  public String getDbName() {
//    return dbName;
//  }

  public Language getLanguage() {
    return language;
  }
  
  public void setLanguage(Language language) {
    this.language = language;
  }

  public Locale getLanguageLocale(Locale defaultLocale) {
    if (language != null)
      return language.getLocale();
    else 
      return defaultLocale;
  }
  
//  public boolean getIsInternationalisationSupport() {
//    return isInternationalisationSupport;
//  }
//
//  public void setInternationalisationSupport(boolean isInternationalisationSupport) {
//    this.isInternationalisationSupport = isInternationalisationSupport;
//  }

  public double getMainGUISizeRatio() {
    return mainGUISizeRatio;
  }

  public void setMainGUISizeRatio(double mainGUISizeRatio) {
    this.mainGUISizeRatio = mainGUISizeRatio;
  }

  public double getChildGUISizeRatio() {
    return childGUISizeRatio;
  }

  public void setChildGUISizeRatio(double childGUISizeRatio) {
    this.childGUISizeRatio = childGUISizeRatio;
  }

  public boolean isChildGUISizeAuto() {
    return this.childGUISizeRatio==DEFAULT_CHILD_GUI_SIZE_RATIO;
  }
  
  public double getDefaultFixedChildGUISizeRatio() {
    return defaultFixedChildGUISizeRatio;
  }

  /**
   * @return the useSecurity
   */
  public boolean getUseSecurity() {
    return useSecurity;
  }

  /**
   * @param useSecurity the useSecurity to set
   */
  public void setUseSecurity(boolean useSecurity) {
    this.useSecurity = useSecurity;
  }

  public void setListSelectionTimeOut(int i) {
    this.listSelectionTimeOut = i;
  }

  public int getListSelectionTimeOut() {
    return this.listSelectionTimeOut;
  }

  /**
   * @return the organisation
   */
  public Organisation getOrganisation() {
    return organisation;
  }

  /**
   * @effects 
   *  initialise this.organisation
   */
  public void setOrganisation(String name, ImageIcon logo, 
      String contacts, String url) {
    organisation = new Organisation(name, logo, contacts, url);
  }
  
  public void setOrganisation(Organisation org) {
    this.organisation=org;
  }
  
  public String getFontLocation() {
    return fontLocation;
  }

  public void setFontLocation(String fontLocation) {
    this.fontLocation = fontLocation;
  }

  public String getImageLocation() {
    return imageLocation;
  }

  public void setImageLocation(String imageLocation) {
    this.imageLocation = imageLocation;
  }

  public List<ApplicationModule> getModules() {
    return modules;
  }

  /**
   * @version 3.0
   *  return {@link #modules} sorted as specified by <tt>comparator</tt>
   */
  public Collection<ApplicationModule> getModules(ObjectComparator comparator) {
    Set sorted = new TreeSet(comparator);
    if (modules != null && !modules.isEmpty()) {
      for (ApplicationModule m : modules) {
        sorted.add(m);
      }
      
      return sorted;
    } else {
      return null;
    }
  }
  
  public Integer getModulesCount() {
    return modules.size();
  }
  
  public void setModules(List<ApplicationModule> modules) {
    this.modules = modules;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  public void addApplicationModule(List<ApplicationModule> modules) {
    this.modules.addAll(modules);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  public void addApplicationModule(ApplicationModule module) {
    modules.add(module);
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  public void removeApplicationModule(ApplicationModule module) {
    modules.remove(module);
  }

// v3.0  
//  public String getLabelConstantClass() {
//    return labelConstantClass;
//  }
//
//  public Class getLabelConstantClassObject() throws NotFoundException {
//    //TODO: load class once 
//    Class constClass = null;
//    try {
//      if (labelConstantClass != null)
//        constClass = Class.forName(labelConstantClass);
//      return constClass;
//    } catch (ClassNotFoundException e) {
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,
//          "Không tìm thấy lớp {0}", labelConstantClass);
//    }
//  }
//  
//  public void setLabelConstantClass(String labelConstantClass) {
//    this.labelConstantClass = labelConstantClass;
//  }

  public Class<? extends SetUpConfigBasic> getSetUpConfigurationType() {
    return suCfgType;
  }

  public void setSetUpConfigurationType(Class<? extends SetUpConfigBasic> c) {
    this.suCfgType = c;
  }

  /**
   * @requires 
   *  styleConstantClass is the FQN of a class
   */
  public void setStyleDefClass(String styleDefClass) {
    this.styleDefClass = styleDefClass;
  }

  public String getStyleDefClass() {
    return styleDefClass;
  }

  public Class getStyleDefClassObject() {
    if (styleDefClassObj == null) {
      try {
        if (styleDefClass != null)
          styleDefClassObj = Class.forName(styleDefClass);
      } catch (ClassNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {styleDefClass});
      }
    }
    
    return styleDefClassObj;
  }

  public DODMConfig getDodmConfig() {
    return dodmConfig;
  }

  public void setDodmConfig(DODMConfig dodmConfig) {
    this.dodmConfig = dodmConfig;
  }

  @Override
  public String toString() {
    return "Configuration("+id+","+appName+")";
  }
  
  @Override
  public boolean equals(Object o) {
    if (o == null || o.getClass() != this.getClass()) {
      return false;
    }
    
    return ((Configuration)o).id == this.id;
  }
  
  /**
   * @overview 
   *  A helper class that represents an the software organisation. 
   */
  @DClass(schema="app_config",singleton=true
      //dependsOn={Configuration.class}
      )
  public static class Organisation {
    @DAttr(name="name",type=Type.String,id=true,length=100,optional=false)
    private String name;
    @DAttr(name="logo",type=Type.Image,
        length=5000000 // 5MB
        )
    private ImageIcon logo;
    @DAttr(name="contactDetails",type=Type.String,length=255,optional=false)
    private String contactDetails;
    @DAttr(name="url",type=Type.String,length=255,optional=false)
    private String url;    

    /** a non-serialisable domain attribute to implement the dependency association with Configuration */
    @DAttr(name="config",type=Type.Domain,serialisable=false)
    @DAssoc(ascName="config-has-organisation",role="organisation",
        ascType=AssocType.One2One,endType=AssocEndType.One,
        associate=@Associate(type=Configuration.class,cardMin=1,cardMax=1,
        determinant=true  // v2.6.4.b
        ))
    private Configuration config;

    public Organisation(String name2, ImageIcon logo2, String contacts,
        String url2) {
      this.name=name2;
      this.logo=logo2;
      this.contactDetails=contacts;
      this.url=url2;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public ImageIcon getLogo() {
      return logo;
    }

    public void setLogo(ImageIcon logo) {
      this.logo = logo;
    }

    public String getContactDetails() {
      return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
      this.contactDetails = contactDetails;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
    
    public Configuration getConfig() {
      return config;
    }

    public void setConfig(Configuration config) {
      this.config = config;
    }

    @Override
    public String toString() {
      return "Organisation<"+name+">";
    }
  } // end organisation

  public SplashInfo getSplashInfo() {
    return splashInfo;
  }

  public void setSplashInfo(SplashInfo splashInfo) {
    this.splashInfo = splashInfo;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public SetUpBasic getSetUpInstance() {
    return setUpInstance;
  }
  
  public void setSetUpInstance(SetUpBasic su) {
    setUpInstance = su;
  }

  /**
   * @requires 
   *  parent != null
   *  
   * @effects 
   *  return the sub directory path {@link #getAppFolder()}<tt>/parent/pathElements[0]/...</tt>
   */
  public String getAppSubDirPath(String parent, 
      String...pathElements) {
    String SEP = File.separator;
    StringBuffer sb = new StringBuffer(getAppFolder());
    sb.append(SEP).append(parent);
    
    for (String el : pathElements) {
      sb.append(SEP).append(el);
    }
    
    return sb.toString();
  }
  
  /**
   * Works similar to {@link #getAppSubDirPath(String, String...)} but without requiring the spec of a parent dir.
   * 
   * @requires 
   *  pathElements != null
   * 
   * @effects 
   *  return the sub directory path {@link #getAppFolder()}<tt>/pathElements[0]/...</tt>; or 
   *  return {@link #getAppFolder()} if <tt>pathElements.length = 0</tt>
   * @version 3.2c
   */
  public String getAppDirPath(String...pathElements) {
    String SEP = File.separator;
    StringBuffer sb = new StringBuffer(getAppFolder());
    
    for (String el : pathElements) {
      sb.append(SEP).append(el);
    }
    
    return sb.toString();
  }
} // end Configuration