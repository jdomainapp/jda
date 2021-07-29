package jda.mosa.view.assets.swing;

import static jda.mosa.controller.assets.util.AppState.Created;
import static jda.mosa.controller.assets.util.AppState.Deleted;
import static jda.mosa.controller.assets.util.AppState.First;
import static jda.mosa.controller.assets.util.AppState.Hidden;
import static jda.mosa.controller.assets.util.AppState.Last;
import static jda.mosa.controller.assets.util.AppState.NewObject;
import static jda.mosa.controller.assets.util.AppState.Next;
import static jda.mosa.controller.assets.util.AppState.OnFocus;
import static jda.mosa.controller.assets.util.AppState.Opened;
import static jda.mosa.controller.assets.util.AppState.Previous;
import static jda.mosa.controller.assets.util.AppState.SearchCleared;
import static jda.mosa.controller.assets.util.AppState.SearchClosed;

import java.awt.Color;

import javax.swing.BorderFactory;

import jda.modules.common.types.properties.PropertyName;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.AppState;
import jda.util.events.StateChangeListener;

public class JObjectScroll extends javax.swing.JLabel 
//v2.7.4
implements StateChangeListener 
{
 
  private DataController current;
  
  public JObjectScroll() {
    super();
    setUp();
  }

  public JObjectScroll(String text, int maxWidth) {
    super(text, maxWidth);
    setUp();
  }

  public JObjectScroll(String text) {
    super(text);
    setUp();
  }

  private void setUp() {
    setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
  }

  @Override // v2.7.4
  public void stateChanged(Object src, AppState state, String messages,
      Object... data) {
    // display the browser state of the active data controller
    DataController dctl = (DataController) src;
    // only consider: (1) active and (2) that wish to update the scroll
    if (dctl.isActive()) {
      Boolean toUpdate = (Boolean) dctl.getProperty(PropertyName.controller_ObjectScrollUpdate, Boolean.TRUE);
      if (toUpdate) {
        if (dctl != current) current = dctl;
        
        if (state == NewObject || state == SearchClosed || state == SearchCleared) {
          if (getText() != null)
            setText(null);
        } else {
          String objectScroll = //v3.1: dctl.getBrowser().getBrowserStateAsString();
              dctl.getBrowserStateAsString();
          setText(objectScroll);
        }
      } else {
        if (getText() != null)
          setText(null);
      }
    } 
    else if (dctl == current) {
      // becomes in active -> reset 
      if (getText() != null)
        setText(null);
    }
  }
  
  @Override // StateChangeListener
  public AppState[] getStates() {
    // listen to all states
    return new AppState[] { 
        // focus 
        OnFocus,
        // browsing
        Opened, First, Previous, Next, Last, 
        // object manipulation
        NewObject, Created, Deleted, 
        // search
        SearchClosed, SearchCleared, 
        // object form
        Hidden
    };
  }
}
