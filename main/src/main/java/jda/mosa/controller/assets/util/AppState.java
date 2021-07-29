/**
 * 
 */
package jda.mosa.controller.assets.util;

/**
 * Application state constants. These are used by application code to signify
 * a change in state of controller to handle state
 * transitions.
 */
public enum AppState {
  Init, //
  // Opened, //
  NewObject, //
  Editing, //
  Created, //
  Added, //
  // Deleted, //
  Reset, Updated
  //
  , CauseAdded, CauseDeleted, CauseUpdated, //
  Opened, Deleted, //
  DeletedFromBuffer,  // v3.0
  Previous, Next, First, Last, //
  SearchToolBarUpdated, Searched, SearchQueryEditing, SearchCleared, SearchClosed, //
  // ReportExecuted,
  OnFocus, CurrentObjectChanged, //
  Cancelled,
  //
  ViewCompact, ViewNormal,
  // , Shown, Hidden,
  Hidden, LoggedIn, LoggedOut,
  /**
   * Special state used for registering listeners <br>
   * DO NOT USE THIS IN STATE TRANSITIONS
   */
  AnyState,
  /**
   * fired to inform the state listener(s) of domain class(es) of interest
   * (e.g. indexable classes) to clear their domain-specific resources
   */
  OnClearDomainClassResources,
  /**printing has started
   * (a simple state in that it is not used to set the application state as such)
   * @version 3.2c
   * */
  Print_Started, 
  /**printing has completed
   * (a simple state in that it is not used to set the application state as such)
   * @version 3.2c
   */
  Print_Completed, 
}