package jda.modules.setup.model;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dodm.DODM;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.ds.SimpleDataFileLoader;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.mosa.controller.ControllerBasic;
import jda.util.SwTk;

/**
 * @overview
 *  CourseMan application that runs a <tt>PostgreSQL</tt> OSM in the <b>client</b> mode.  
 *  
 * @author dmle
 *
 */
public abstract class DefaultRealisticSetUp extends SetUp {

  private static final Class<? extends SetUpConfigBasic> defSetUpConfig = SetUpConfig.class;
  
  private static final String defDataSourceUserName = "admin";
  private static final String defDataSourcePassword = "password";
  
  private static final String clientUrlPrefix = "//localhost:5432" ;

  private static final Class<? extends DSMBasic> defDSM_Type = DSM.class;
  private static final Class<? extends DOMBasic> defDOM_Type = DOM.class;
  private static final Class<? extends DODMBasic> defDODM_Type = DODM.class;
  
  /**must match {@link #defOSM_Type} below*/
  private static final String defDataSourceType = "postgresql"; 
  private static final Class<? extends OSM> defOSM_Type = PostgreSQLOSM.class;
  
  private static final boolean debug = Toolkit.getDebug(DefaultRealisticSetUp.class);

  private String appName;
  private Language language;
  
  private Class<? extends SetUpConfigBasic> setUpCfgCls;
  
  private String dataSourceType;
  private String userName;
  private String pwd;
  private String clientUrl;
  private Class<? extends DODMBasic> dodmType;
  private Class<? extends DSMBasic> dsmType;
  private Class<? extends DOMBasic> domType;
  private Class<? extends OSM> osmType;
  
  public DefaultRealisticSetUp() throws NotPossibleException, NotFoundException {
    super();
    
    appName = System.getProperty(PropertyName.setup_config_appName.getSysPropName());
    String langName = System.getProperty(PropertyName.setup_config_language.getSysPropName());
    String setUpCfgName = System.getProperty(PropertyName.setup_config_configType.getSysPropName());

    // use System properties if available, otherwise use default
    dataSourceType = System.getProperty(PropertyName.setup_dataSource_type.getSysPropName());
    userName = System.getProperty(PropertyName.setup_dataSource_userName.getSysPropName());
    pwd = System.getProperty(PropertyName.setup_dataSource_password.getSysPropName());
    clientUrl = System.getProperty(PropertyName.setup_dodm_clientUrl.getSysPropName());
    String dodmTypeName = System.getProperty(PropertyName.setup_dodm_type.getSysPropName());
    String dsmTypeName = System.getProperty(PropertyName.setup_dodm_dsmType.getSysPropName());
    String domTypeName = System.getProperty(PropertyName.setup_dodm_domType.getSysPropName());
    String osmTypeName = System.getProperty(PropertyName.setup_dodm_osmType.getSysPropName());
    
    if (appName == null)
      appName = getInitAppName();
    
    if (langName == null) {
      language = getInitLanguage();
    } else {
      language = Language.valueOf(langName);
    }
  
    
    if (dataSourceType == null)
      dataSourceType = defDataSourceType;
      
    if (userName==null)
      userName = defDataSourceUserName;
    if (pwd == null)
      pwd = defDataSourcePassword;
    
    if (clientUrl == null) {
      clientUrl = clientUrlPrefix + URL_SEP + getInitDataSourceName();
    }
    
    try {
      if (setUpCfgName == null) {
        setUpCfgCls = defSetUpConfig;
      } else {
        setUpCfgCls = (Class<SetUpConfigBasic>) Class.forName(setUpCfgName);
      }

      if (dodmTypeName == null) {
        dodmType = defDODM_Type;
      } else {
        dodmType = (Class<DODMBasic>) Class.forName(dodmTypeName);
      }

      if (dsmTypeName == null) {
        dsmType = defDSM_Type;
      } else {
        dsmType = (Class<DSMBasic>) Class.forName(dsmTypeName);
      }
    
      if (domTypeName == null) {
        domType = defDOM_Type;
      } else {
        domType = (Class<DOMBasic>) Class.forName(domTypeName);
      }

      if (osmTypeName == null) {
        osmType = defOSM_Type;
      } else {
        osmType = (Class<OSM>) Class.forName(osmTypeName);
      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e);
    }
  }

  @Override
  public Configuration createInitApplicationConfiguration() {
    // use client/server config
    Configuration config = SwTk.createClientApplicationConfiguration(appName, 
        dataSourceType, 
        clientUrl, userName, pwd);
    
    // customise dodm types
    DODMConfig dodmCfg = config.getDodmConfig();
    
    dodmCfg.setDodmType(dodmType);  // v3.0
    dodmCfg.setDsmType(dsmType);
    dodmCfg.setDomType(domType);
    dodmCfg.setOsmType(osmType);
    
    // set language
    config.setLanguage(language);
    
    config.setSetUpConfigurationType(setUpCfgCls);
    
    return config;
  }

  protected abstract String getInitAppName();
  
  protected abstract Language getInitLanguage();
  
  protected abstract String getInitDataSourceName();
}
