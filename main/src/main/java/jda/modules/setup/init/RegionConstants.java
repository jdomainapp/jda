package jda.modules.setup.init;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.view.assets.swing.JObjectScroll;
import jda.mosa.view.assets.swing.ViewModeCheckBox;

public class RegionConstants {
//  static {
//    System.out.println("Initialising regions...");
//  }
  
  public static final Region Root = new Region(RegionName.Root.name());
  public static final Region Main = new Region(RegionName.Main.name(), null, null, Root, null);
  public static final Region Data = new Region(RegionName.Data.name(), null, null, Root, null);
  public static final Region DataAuto = new Region(RegionName.DataAuto.name(), null, null, Root, null);
  public static final Region DataLogin = new Region(RegionName.DataLogin.name(), null, null, Root, null);
  // public static final Region Info = new Region(RegionName.Info.name(), null, null, Root, null);
  public static final Region Report = new Region(RegionName.Report.name(), null, null, Root,
      null);

  /// main regions
  public static final Region Desktop = new Region(RegionName.Desktop.name(),
      null, null);
  public static final Region MenuBar = new Region(RegionName.MenuBar.name(),
      null, null);
  public static final Region ToolBar = new Region(RegionName.ToolBar.name(),
      null, null);
  public static final Region StatusBar = new Region(
      RegionName.StatusBar.name(), null, null);

  static {
    StatusBar.setIsStateListener(true);
  }
  
  static int index = 1;

  // / menu bar regions
  public static final Region File = new Region("File", null, null, MenuBar, index++);
  public static final Region Tools = new Region("Tools", null, null,
      MenuBar, index++);
  public static final Region Options = new Region("Options", null, null,
      MenuBar, index++);
  public static final Region Help = new Region("Help", null, null,
      MenuBar, index++);

  // v2.6.4.a: add Windows region
  public static final Region Window = new Region(
      //v2.7.2: "Window",
      RegionName.Window.name(),
      null, null, MenuBar, index++);
  
  // / File menu
  static {index = 1;}
  public static final Region FileExit = new Region(
      RegionName.Exit.name(), null, "exit.png", File, index++);

  // Tools menu
  static {index = 1;}
  public static final Region SearchToolBar = new Region(
      RegionName.SearchToolBar.name(), 
      null, // label
      "find.gif",  // image icon
      Tools, // parent
      index++      // display order
      ); 
  public static final Region ToolReport = new Region(
      RegionName.ToolReport.name(),
      null,   // label
      "report.png", // image icon
      RegionType.Menu,  // type
      null,       // default value
      Tools,      // parent
      index++        // display order
      );

  
    public static final Region ToolSecurity = new Region(
      "Security", null, "security.gif",
      RegionType.Menu, null, Tools, index++);
  
  // v2.6.4.a: Windows menu
  public static final Region WindowTile = new Region(
        RegionName.WindowTile.name(), 
        null, // label 
        null, // image icon
        Window,  // parent 
        1);
  public static final Region WindowCascade = new Region(
      RegionName.WindowCascade.name(), 
      null, // label 
      null, // image icon
      Window,  // parent 
      2);
  // sub-menu
  public static final Region WindowSubMenuAuto = new Region(
      RegionName.WindowSubMenuAuto.name(),
      null, 
      null, 
      RegionType.ChoiceMenu, 
      null, // default value
      Window, 
      3);
    public static final Region WindowAutoTile = new Region(
          RegionName.WindowAutoTile.name(), 
          null, // label 
          null, // image icon
          RegionType.Check,
          null, // default value
          WindowSubMenuAuto,  // parent 
          1);
    public static final Region WindowAutoCascade = new Region(
        RegionName.WindowAutoCascade.name(), 
        null, // label 
        null, // image icon
        RegionType.Check, 
        "true", // default value
        WindowSubMenuAuto,  // parent 
        2);
    public static final Region WindowClose = new Region(
        RegionName.WindowClose.name(), 
        null, // label 
        null, // image icon
        Window,  // parent 
        4);
  /// other specific tool menu items are to be added by domain applications

  // Tool bar regions  
  static {
    index = 1;
  }
  public static final Region ToolOpen = new Region(
  RegionName.Open.name(), null, "open.gif", ToolBar, index++);

  // v3.0
  public static final Region ToolCopyObject = new Region(
  RegionName.CopyObject.name(), null, "copy.png", ToolBar, index++);

  public static final Region ToolRefresh = new Region(
      RegionName.Refresh.name(), null, "refresh.gif", ToolBar, index++);
  
  // v3.0
  public static final Region ToolReload = new Region(
      RegionName.Reload.name(), null, "reload.png", ToolBar, index++);
  
  public static final Region ToolExport = new Region(
      RegionName.Export.name(), null, "export.gif", ToolBar, index++);
  
  // v2.7.2: print
  public static final Region ToolPrint = new Region(
      RegionName.Print.name(), null, "print.gif", ToolBar, index++);
  
  public static final Region ToolChart = new Region(
      RegionName.Chart.name(), null, "chart.gif", ToolBar, index++);
  public static final Region ToolNew = new Region(RegionName.New.name(),
      null, "new.gif", ToolBar, index++);

  // v2.7.2: add (experimental)
//  public static final Region ToolAdd = new Region(
//      RegionName.Add.name(), null, "add.gif", ToolBar, index++);
  
  public static final Region ToolUpdate = new Region(
      RegionName.Update.name(), null, "edit.gif", ToolBar, index++);
  public static final Region ToolDelete = new Region(
      RegionName.Delete.name(), null, "delete.gif", ToolBar, index++);
  public static final Region ToolFirst = new Region(
      RegionName.First.name(), null, "doubleleft.gif", ToolBar, index++);
  public static final Region ToolPrevious = new Region(
      RegionName.Previous.name(), null, "left.gif", ToolBar, index++);
  // v2.7.4
  public static final Region ToolObjectScroll = new Region(
      RegionName.ObjectScroll.name(), null, null, RegionType.Label, 
      null, JObjectScroll.class.getName(),  
      ToolBar, index++);
  static {
    ToolObjectScroll.setIsStateListener(true);
    ToolObjectScroll.setStyle(StyleConstants.DefaultBold);
    ToolObjectScroll.setWidth(50); ToolObjectScroll.setHeight(20);
    ToolObjectScroll.setAlignX(AlignmentX.Center);
  }
  
  public static final Region ToolNext = new Region(
      RegionName.Next.name(), null, "right.gif", ToolBar, index++);
  public static final Region ToolLast = new Region(
      RegionName.Last.name(), null, "doubleright.gif", ToolBar, index++);

  public static final Region ToolViewCompact = new Region(
      RegionName.ViewCompact.name(), 
      null, 
      "viewcompact.gif", RegionType.Check, 
      "false", ViewModeCheckBox.class.getName(),
      ToolBar, index++);
  static {
    ToolViewCompact.setIsStateListener(true);
  }
  
  // v3.2c: help button
  public static final Region ToolHelpButton = new Region(
      RegionName.HelpButton.name(), null, "help.gif", ToolBar, index++);

  // search tool bar regions
  static {
    index = 1;
  }
  
  public static final Region ToolSearchText = new Region("SearchQueryField", 
      null, null, 35, null, 
      RegionType.Text, null, SearchToolBar, index++);
  // v2.7.2:
  public static final Region ToolSearchOption = new Region( 
      RegionName.SearchOption.name(), null, null, RegionType.Check,  
      "false", SearchToolBar, index++); 
  public static final Region ToolSearch = new Region(
      RegionName.Search.name(), null, 
      "find.gif", SearchToolBar, index++);
  public static final Region ToolClearSearch = new Region(
      RegionName.ClearSearch.name(), null, 
      "cancel.gif", SearchToolBar,index++);
  public static final Region ToolCloseSearchToolBar = new Region(
      RegionName.CloseSearch.name(), 
      null, "close.gif", SearchToolBar, index++);

  // data regions
  public static final Region Actions = new Region(
      RegionName.Actions.name(), null, null, Data, 1);

  
  static {
    SearchToolBar.addParent(Data, 2);
    SearchToolBar.addParent(DataAuto, 2);
  }

  
  // components 
  public static final Region Components = new Region(
      RegionName.Components.name(), null, null, Data, 3);
  static {
    Components.addParent(DataAuto, 3);
  }

  /**
   * side panel (child of Region(Data)) which appears after {@link #Components} region in 
   * the generation procedure.
   * 
   * @version 5.2
   */
  public static final Region SidePane = new Region(
      RegionName.SidePane.name(), null, null, Data, 4);

  // status bar regions
  static {
    index = 1;
  }
  public static final Region StatusUserInfo = new Region(
      RegionName.StatusUserInfo.name(), null, 
      100, 20, RegionType.Label, null, true, StyleConstants.Default, StatusBar, index++); 
  public static final Region StatusDateTimeInfo = new Region(
      RegionName.StatusDateTimeInfo.name(), null, 
      150, 20, RegionType.Label, null, true, StyleConstants.Default, StatusBar, index++); 
  public static final Region StatusTaskInfo = new Region(
      RegionName.StatusTaskInfo.name(), null, 
      300, 20, RegionType.Label, null, true, StyleConstants.Default, StatusBar, index++); 
  public static final Region StatusStateInfo = new Region(
      RegionName.StatusStateInfo.name(), null, 
      100, 20, RegionType.Label, null, true, StyleConstants.Default, StatusBar, index++); 

  // report regions
  public static final Region ReportActions = new Region(
      RegionName.Actions.name(), null, null, Report, 1);
  static {
    Components.addParent(Report, 2);
  }

  // login actions region
  public static final Region LoginActions = new Region(RegionName.LoginActions.name(), 
      null, null, DataLogin, 1);
  static {
    Components.addParent(DataLogin, 3);
  }

  // data actions
  static {
    index = 1;
  }
  public static final Region ActionCreate = new Region(
      RegionName.Create.name(), null, "create.gif", Actions, index++);
  public static final Region ActionReset = new Region(
      RegionName.Reset.name(), null, "reset.gif", Actions, index++);
  public static final Region ActionCancel = new Region(
      RegionName.Cancel.name(), null, "cancel.gif", Actions, index++);

  // report actions
  static {
    index = 1;
  }
  public static final Region ActionReportCreate = new Region(
      RegionName.Create.name(), null, "create.gif",
      ReportActions, index++);
  static {
    ActionReset.addParent(ReportActions, index++);
    ActionCancel.addParent(ReportActions, index++);
  }
  
  // login actions
  public static final Region ActionLogin = new Region(
      RegionName.Create.name(), null, "login.gif", LoginActions, 1);
  static {
    ActionReset.addParent(LoginActions, 2);
  }

  static {
    // set enabled
    ToolOpen.setEnabled(false); 
    ToolRefresh.setEnabled(false);
    ToolReload.setEnabled(false);
    ToolCopyObject.setEnabled(false);
    ToolDelete.setEnabled(false);
    ToolExport.setEnabled(false);
    ToolPrint.setEnabled(false);
    ToolChart.setEnabled(false);
    ToolFirst.setEnabled(false);
    ToolLast.setEnabled(false);
    ToolNew.setEnabled(false);
    // v2.7.2: experimental: ToolAdd.setEnabled(false);
    ToolNext.setEnabled(false);
    ToolPrevious.setEnabled(false);
    ToolUpdate.setEnabled(false);
    
    ToolSearchText.setEditable(false); // v2.6.4b
    ToolSearchOption.setEnabled(true);
    ToolReport.setEnabled(true);
    ToolSecurity.setEnabled(true);
    SearchToolBar.setEnabled(true);
    
    ActionCreate.setEnabled(false);
    ActionCancel.setEnabled(false);
    ActionReset.setEnabled(false);
    ActionReportCreate.setEnabled(false);
  }

  /**
   * @effects 
   *  generate a region name for the side panel of the module whose name is <tt>moduleName</tt>.
   *  This name must be unique among the side panel regions of other modules. 
   *  
   * @version 5.2
   */
  public static String genSidePaneRegionNameForModule(final String moduleName) {
    return moduleName+":SidePane";
  }
}
