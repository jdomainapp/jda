package jda.mosa.view.assets.swing;

import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.view.View;
import jda.util.events.StateChangeListener;

public class ViewModeCheckBox extends javax.swing.JCheckBox 
  implements StateChangeListener {
  
  private static final long serialVersionUID = -6772481294159716647L;

  private ControllerBasic controller;
  
  private AppState[] states;

  // the AppGUI to which the value of this box is being applied
  private View currGUI; // v2.7.2
  
  public ViewModeCheckBox(ControllerBasic controller) {
    super();
    this.controller = controller;
    this.states = new AppState[] {AppState.OnFocus};
  }

  @Override
  public void stateChanged(Object src, AppState state, String messages,Object...data) {
    //System.out.println("ViewModeCheckBox: new state " + state);
    /** 
     * update the state of this check box based on the
     * view of the active GUI. 
     *  if this GUI is compact
     *    set this.selected = true
     *  else
     *    set this.selected = false  
     */
    //debug
    //System.out.printf("ViewModeCheck.stateChanged: current state %b%n", this.isSelected());
    
    View gui = controller.getMainController().getActiveGUI();
    if (gui != null && gui != currGUI) {
      // update this checkbox's status 
      boolean selected = gui.isCompact();
      
      if (this.isSelected() != selected) {
        setSelected(selected);
      }
      
      this.currGUI = gui;
    } else if (gui == null) {
      // reset
      this.currGUI = null;
      setSelected(false);
    }
  }

  @Override
  public AppState[] getStates() {
    return states;
  }
}
