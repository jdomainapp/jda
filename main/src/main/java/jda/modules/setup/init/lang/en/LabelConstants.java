package jda.modules.setup.init.lang.en;


public class LabelConstants {
//  static {
//    System.out.println("Initialising English labels...");
//  }

  public static final Label Desktop = new Label("Desktop");
  public static final Label MenuBar = new Label("Menu bar");
  public static final Label ToolBar = new Label("Tool bar");
  public static final Label StatusBar = new Label("Status bar");

  // / menu bar regions
  public static final Label File = new Label("File");
  public static final Label Tools = new Label("Tools");
  public static final Label Options = new Label("Options");
  public static final Label Help = new Label("Help");

  // / File menu
  //public static final Label FileOpen = new Label("Open");
  public static final Label FileExit = new Label("Exit");
  //public static final Label LogIn = new Label("Login");

  // Tools menu
  public static final Label SearchToolBar = new Label("Find...");
  public static final Label ToolReport = new Label("Report");
  public static final Label ToolSecurity = new Label("Security");
  // other specific tool menu items are to be added by domain applications

  // Options menu
//  public static final Label OptionLanguage = new Label("Language");
//
//  public static final Label LanguageVi = new Label("Vietnamese");
//
//  public static final Label LanguageEn = new Label("English");
  
  // Help menu
//  public static final Label Configuration = new Label("About...");
  
  // v2.6.4.a: Windows menu
  public static final Label Window = new Label("Window");
  public static final Label WindowTile = new Label("Tile");
  public static final Label WindowCascade = new Label("Cascade");
  public static final Label WindowSubMenuAuto = new Label("Auto-arrange");
  public static final Label WindowAutoTile = new Label("Tile");
  public static final Label WindowAutoCascade = new Label("Cascade");
  public static final Label WindowClose = new Label("Close window");
  
  // Tool menu
  public static final Label ToolOpen = new Label("Open");
  public static final Label ToolCopyObject = new Label("Copy");
  public static final Label ToolRefresh = new Label("Refresh");
  public static final Label ToolReload = new Label("Reload");
  public static final Label ToolExport = new Label("Export");
  public static final Label ToolPrint = new Label("Print");
  public static final Label ToolChart = new Label("Chart");  
  public static final Label ToolNew = new Label("New");
  public static final Label ToolAdd = new Label("Add");
  public static final Label ToolUpdate = new Label("Update");
  public static final Label ToolDelete = new Label("Delete");
  public static final Label ToolFirst = new Label("First");
  public static final Label ToolPrevious = new Label("Previous");
  public static final Label ToolNext = new Label("Next");
  public static final Label ToolLast = new Label("Last");

  public static final Label ToolViewCompact = new Label("Compact view");

  public static final Label ToolHelpButton = new Label("Help");

  // search tool bar regions
  public static final Label ToolSearchText = new Label("Query");
  public static final Label ToolSearchOption = new Label("All");
  public static final Label ToolSearch = new Label("Search");
  public static final Label ToolClearSearch = new Label("Clear");
  public static final Label ToolCloseSearchToolBar = new Label("Close");;

  // data actions
  public static final Label ActionCreate = new Label("Create");
  public static final Label ActionReset = new Label("Reset");
  public static final Label ActionCancel = new Label("Cancel");

  // report actions
  public static final Label ActionReportCreate = new Label("Execute report");

  // login action
  public static final Label ActionLogin = new Label("Login");

  // status bar labels
  public static final Label StatusUserInfo = new Label("User");

  // export
  //public static final Label ModuleExport = new Label("Export");

  //////////////////////////////// Labels for built-in modules ////////////////////

// v2.7.4: removed to use labels defined in the module descriptors  
//  public static final Label Logout = new Label("Logout");
//
//  /**LoginUser*/
//  public static final Label Login = new Label("Login");
//  public static final Label ModuleLogin_title = new Label("Login");
//  public static final Label ModuleLogin_login = new Label("User name");
//  public static final Label ModuleLogin_password = new Label("Password");
//  public static final Label ModuleLogin_role = new Label("Role");
//
//  public static final Label LoginUser_title = new Label("Login user");
//  public static final Label LoginUser_login = new Label("User name");
//  public static final Label LoginUser_password = new Label("Password");
//  public static final Label LoginUser_role = new Label("Role");
//
//  /**Configuration*/
//  public static final Label ModuleConfiguration = new Label("Application configuration");  
//  public static final Label ModuleConfiguration_title = new Label("Application configuration");
//  public static final Label ModuleConfiguration_appName = new Label("Name");
//  public static final Label ModuleConfiguration_version = new Label("Version");  
//  public static final Label ModuleConfiguration_appFolder = new Label("Application folder");  
//  public static final Label ModuleConfiguration_language = new Label("Language");
//  public static final Label ModuleConfiguration_organisation = new Label("Organisation");
//  public static final Label ModuleConfiguration_modules = new Label("Modules");
//  public static final Label ModuleConfiguration_defaultModule = new Label("Default run module");
//  public static final Label ModuleConfiguration_setUpFolder = new Label("Set up folder");
//  public static final Label ModuleConfiguration_dbName = new Label("Database name");  
//  public static final Label ModuleConfiguration_userName = new Label("Default user");
//  public static final Label ModuleConfiguration_password = new Label("Password");  
//  public static final Label ModuleConfiguration_mainGUISizeRatio = new Label("Main window <br> size ratio");
//  public static final Label ModuleConfiguration_childGUISizeRatio = new Label("Child window <br>size ratio");
//  public static final Label ModuleConfiguration_useSecurity = new Label("Security?");    
//  public static final Label ModuleConfiguration_listSelectionTimeOut = new Label("Spinner delay");
//  public static final Label ModuleConfiguration_fontLocation = new Label("Font location");
//  public static final Label ModuleConfiguration_imageLocation = new Label("Image location");
//  public static final Label ModuleConfiguration_labelConstantClass = new Label("Label constant class");
//  
//  /**ControllerConfig*/
//  public static final Label ModuleControllerConfig = new Label("Controller configuration");
//  public static final Label ModuleControllerConfig_id = new Label("Id");
//  public static final Label ModuleControllerConfig_controller = new Label("Controller type");
//  public static final Label ModuleControllerConfig_dataController = new Label("Data controller <br>type");
//  public static final Label ModuleControllerConfig_openPolicy = new Label("Object open <br>policy");
//  public static final Label ModuleControllerConfig_defaultCommand = new Label("Default run <br>command");
//  public static final Label ModuleControllerConfig_isStateListener = new Label("State listener?");
//  public static final Label ModuleControllerConfig_applicationModule = new Label("Owner module");
//  
//  /**Module*/
//  public static final Label ModuleApplicationModule = new Label("Program module");
//  public static final Label ModuleApplicationModule_name = new Label("Name");
//  public static final Label ModuleApplicationModule_config = new Label("Configuration");
//  public static final Label ModuleApplicationModule_controllerCfg = new Label("Controller <br>configuration");
//  public static final Label ModuleApplicationModule_domainClass = new Label("Domain class");
//  public static final Label ModuleApplicationModule_isViewer = new Label("Object viewer?");
//  public static final Label ModuleApplicationModule_isPrimary = new Label("Primary?");
//  
//  /**UserApplicationModule*/
//  public static final Label ModuleDomainApplicationModule = new Label("Application modules");
//  public static final Label ModuleDomainApplicationModule_domainModule = new Label("");
//  
//  /**Organisation*/
//  public static final Label ModuleOrganisation = new Label("Organisation");  
//  public static final Label ModuleOrganisation_title = new Label("Organisation");
//  public static final Label ModuleOrganisation_name = new Label("Name:");
//  public static final Label ModuleOrganisation_logo = new Label("Logo:");
//  public static final Label ModuleOrganisation_contactDetails = new Label("Contacts:");
//  public static final Label ModuleOrganisation_url = new Label("URL:");
//  
//  /**Chart*/
//  public static final Label ModuleChart = new Label("Chart");
//  public static final Label ModuleChart_chartTitle = new Label("Chart title"); 
//  public static final Label ModuleChart_chartType = new Label("Chart type");
//  public static final Label ModuleChart_categoryByColumn = new Label("Category by <br>column");
//  public static final Label ModuleChart_chart = new Label("");
//  
//  /**Export Document*/
//  public static final Label ModuleExportDocument = new Label("Export and Print");
//  public static final Label ModuleExportDocument_pages = new Label(MetaConstants.SYMBOL_ContainerHandle);
//
//  public static final Label ModulePage = new Label("-");
//  public static final Label ModulePage_outputFile = new Label(MetaConstants.SYMBOL_ContainerHandle);
//
//  /**ImportData*/
//  public static final Label ModuleImportData = new Label("Import data");
//  public static final Label ModuleImportData_osmType = new Label("Choose storage <br>type");
//  public static final Label ModuleImportData_domainClass = new Label("Choose data <br>type");
}
