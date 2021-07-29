package jda.mosa.controller.assets.eventhandler;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview
 *  A mouse handler to listen to mouse click event on the tab component and 
 *  to select the tab accordingly. 
 *  
 * @author dmle
 */
public class TabGroupMouseHandler extends MouseAdapter {
  private JTabbedPane tabGroup;
  
  public TabGroupMouseHandler(JTabbedPane tabGroup) {
    this.tabGroup = tabGroup;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    // change tab icon and tab title style
    JComponent selectedTabComp = (JComponent) e.getSource();
    // select the tab whose component is tabComp
    
    // error -> tabGroup.setSelectedComponent(tabComp);
    //  because of using equals() to search
    
    int tabCount = tabGroup.getTabCount();
    
    Component tabComp;  
    
    for (int i = 0; i < tabCount; i++) {
      tabComp = tabGroup.getTabComponentAt(i);
      // need to compare using == (not equals())
      if (tabComp == selectedTabComp) {
        // found the selected tab
        tabGroup.setSelectedIndex(i);
        
        // update icon to 'opened' if not already
        if (selectedTabComp instanceof JLabel) {
          GUIToolkit.updateContainerLabelOnVisibilityUpdate((JLabel)selectedTabComp, true);
        }
      } else {
        // not a selected tab
        // change icon to 'closed' if not already
        if (tabComp instanceof JLabel) {
          GUIToolkit.updateContainerLabelOnVisibilityUpdate((JLabel)tabComp, false);
        }          
      }
    }
  }

  /**
   * @effects 
   *  update the tab component of the tab at the specified <tt>index</tt>
   */
  public void updateTabComponentAt(int index) {
    Component tabComp = tabGroup.getTabComponentAt(index);
    // update icon to 'opened' if not already
    if (tabComp instanceof JLabel) {
      GUIToolkit.updateContainerLabelOnVisibilityUpdate((JLabel)tabComp, true);
    }
  }
}