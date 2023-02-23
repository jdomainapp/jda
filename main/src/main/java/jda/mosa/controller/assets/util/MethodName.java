/**
 * 
 */
package jda.mosa.controller.assets.util;

import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * Method name constants that are mapped exactly to the method names of this
 * and of some of the selected sub-classes
 * as well as to those in the {@link DataController} class.
 * 
 * <p>
 * These constants are used by {#link CompositeController} to compose the
 * running nodes.
 * 
 * <p>
 * If the method names in the concerned classes are changed then be sure to
 * update the corresponding constant entries.
 */
public enum MethodName {
  /* method names of Controller class */
  runDefaultModule, preRun, run, 
  showGUI, 
  /**similar to {@link #showGUI} but in addition wait until View has completed this process*/
  showGUIAndWait, 
  hideGUI, preRunConfigureGUI,
  /* method names of SecurityController */
  login, logout,
  /* method names of CompositeController */
  resetTree, restart, 
  /* method names of ReportController * */
  initReport, doReport,
  /* task-related method names for sub-types of Controller class */
  createObjectActively, refreshOnShown,  // v3.0
  doTask, 
  doTaskLoopBack, // v3.0 
  init, // v2.7.4
  
  /* method names of DataController class that are used in ModuleAct */
  open, openAndWait, // v2.7.2
  newObject, createObject, getCurrentObject, setUserSpecifiedState, setMutableState, updateGUI, clearChildren, first, firstAndWait,
  setDataFieldValues, // v4.0
  setDataFieldValue, // v4.0
  activateView,  // 5.6.0
  showObject, // 5.6.0
  filterInput, // v5.6.0
  updateObject, // v5.6.0
}