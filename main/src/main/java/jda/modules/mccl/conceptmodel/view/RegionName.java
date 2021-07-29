package jda.modules.mccl.conceptmodel.view;

/** Some pre-configured region names */
public enum RegionName {
  Root, 
  Main, // main  
  Data, // data
  /** data GUI that automatically handles user input actions 
   * (i.e. without needing to use action buttons) */
  DataAuto, 
  /** login gui*/
  DataLogin, 
  Info,
  Report,  // report
  Desktop, 
  MenuBar, // 
  ToolBar, //
  ////// Tool bar child regions: 
  /// IMPORTANT: these names must match those in LogicalAction.LAName
  Exit, 
  Export,
  Print,  // v2.7.2
  Chart,
  Open,
  CopyObject, // v3.0
  New,
  Add,  // v2.7.2
  Refresh,
  Reload, // v3.0
  Create,
  Cancel,
  Update,
  Delete,
  Reset,
  First,
  Previous,
  Last,
  Next,
  ObjectScroll, // v2.7.4
  ////// Search Tool Bar
  SearchOption, // v2.7.2 
  Search, ClearSearch, CloseSearch,
  ////END Search ToolBar
  ViewCompact,
  /**Help button on tool bar
   * @version 3.2c*/
  HelpButton,
  ////// END Tool bar
  StatusBar, StatusUserInfo, StatusDateTimeInfo, StatusTaskInfo, StatusStateInfo, 
  File, Options, 
  /**Help menu*/
  Help, 
  Tools, 
  SearchToolBar, 
  //OptionLanguage, 
  Components,
  ToolReport, 
  Actions, 
  LoginActions,
  Null, 
  // Window menu items
  Window,
  WindowTile, 
  WindowCascade, 
  WindowSubMenuAuto, WindowAutoTile, WindowAutoCascade, 
  WindowClose,  
  // Other view-related regions
  /**The region that contains the side pane of a module's view. It is used for displaying 
   * meta-information (e.g. a navigator tree) about the view
   * 
   * @version 5.2 
   */
  SidePane,
}