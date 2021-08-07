package jda.modules.setup.init;

import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.security.def.LogicalAction;

/**
 * @author dmle
 *
 */
public class LogicalActionConstants {  
  static {
    System.out.println("Initialising logical actions...");
  }

  //// pre-defined actions
  public static final LogicalAction Any = new LogicalAction(LAName.LogicalAny);
  public static final LogicalAction None = new LogicalAction(LAName.None);

  public static final LogicalAction Exit = new LogicalAction(LAName.Exit);
  public static final LogicalAction Login = new LogicalAction(LAName.Login);
  public static final LogicalAction Logout = new LogicalAction(LAName.Logout);

  public static final LogicalAction View = new LogicalAction(LAName.View); // v3.1
  public static final LogicalAction Open = new LogicalAction(LAName.Open);
  public static final LogicalAction New = new LogicalAction(LAName.New);
  public static final LogicalAction Refresh = new LogicalAction(LAName.Refresh);

  // v3.0
  public static final LogicalAction Reload = new LogicalAction(LAName.Reload);
  public static final LogicalAction CopyObject = new LogicalAction(LAName.CopyObject);
  
  public static final LogicalAction Create = new LogicalAction(LAName.Create);
  public static final LogicalAction Cancel = new LogicalAction(LAName.Cancel);
  public static final LogicalAction Update = new LogicalAction(LAName.Update);
  public static final LogicalAction Delete = new LogicalAction(LAName.Delete);
  public static final LogicalAction Reset = new LogicalAction(LAName.Reset);
  public static final LogicalAction First = new LogicalAction(LAName.First);
  public static final LogicalAction Previous = new LogicalAction(LAName.Previous);
  public static final LogicalAction Last = new LogicalAction(LAName.Last);
  public static final LogicalAction Next = new LogicalAction(LAName.Next);
  public static final LogicalAction Search = new LogicalAction(LAName.Search);
  public static final LogicalAction ClearSearch = new LogicalAction(LAName.ClearSearch);
  public static final LogicalAction CloseSearch = new LogicalAction(LAName.CloseSearch);
  public static final LogicalAction Export = new LogicalAction(LAName.Export);
  public static final LogicalAction Print = new LogicalAction(LAName.Print);  // v2.7.2
  public static final LogicalAction ViewCompact = new LogicalAction(LAName.ViewCompact);
  public static final LogicalAction ObjectScroll = new LogicalAction(LAName.ObjectScroll);  // v2.8
  public static final LogicalAction HelpButton = new LogicalAction(LAName.HelpButton);  // v3.2c
  
  /// end pre-defined actions
//  public static List<LogicalAction> DataAction;
//  public static List<LogicalAction> AppAction;
//  
//  private static List<LogicalAction> actions;
//  
//  static {
//    DataAction = new ArrayList();
//    Collections.addAll(DataAction, Any, None, 
//            Open, New, Refresh, //
//      Create, Update, Cancel, Reset, //
//      Delete, //
//      Next, Last, Previous, First, ViewCompact, //
//      Search, ClearSearch, CloseSearch, //
//      Export );
//    
//    AppAction = new ArrayList();
//    Collections.addAll(AppAction, Any, None, Login, Logout, Exit);
//    
//    actions = new ArrayList();
//    actions.addAll(DataAction);
//    for (LogicalAction a: AppAction) {
//      if (!actions.contains(a)) {
//        actions.add(a);
//      }
//    }
//  }
//
//  public static Iterator<LogicalAction> actionIterator() {
//    return actions.iterator();
//  }
}