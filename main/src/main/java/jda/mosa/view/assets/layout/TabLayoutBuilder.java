package jda.mosa.view.assets.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.controller.assets.eventhandler.TabGroupMouseHandler;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * @overview
 *  A <b>compact <tt>layout</tt></b> that group all sub-containers together in a tab group using {@link JTabPane} 
 *  The remaining data fields are grouped together in a sub-panel.
 *  
 *  <p><b>Note</b>: The original ordering of the sub data containers relative to the data fields 
 *  are changed by this layout.  
 *  
 *  <p>Use this layout for views that have many sub-containers to display.
 *    
 * @author dmle
 * @version 3.0
 */
public class TabLayoutBuilder extends LayoutBuilder {

  @Override
  protected void preLayout(GroupLayout layout, Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    super.preLayout(layout, labels, comps, compCfgs);
    
    linkSizeStandardDataFieldLabels(labels, comps, compCfgs);
  }

  /**
   * @modifies layout 
   * @effects 
   *  create a <b>compact <tt>layout</tt></b> that group all sub-containers together in a tab group. 
 *  The remaining data fields are grouped together in a sub-panel.
   */
  @Override
  public Collection<JComponent> doLayout(GroupLayout layout, Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {

    //identify the sub-containers so that they can be added to separate panels
    ParallelGroup hz = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
    SequentialGroup vert = layout.createSequentialGroup();

    Iterator<JComponent> labelsIt = labels.iterator();
    Iterator<JComponent> compsIt = comps.iterator();
    Iterator<Region> compCfgsIt = compCfgs.iterator();
    JComponent label, comp;
    String labelTxt;
    
    List<JComponent> dfLabels = null, dfComps = null;
    List<Region> dfCfgs = null;
    
    JPanel sub;
    JDataContainer cont;
    //GroupLayout.Group compGroup;
    Region compCfg; //RegionDataField dfCfg;
    AlignmentX alignX;
    Alignment compAlign;
    
    JTabbedPane tabGroup = null;
    int tabIndex;
    
    TabGroupMouseHandler tabMouseHandler = null;
        
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      label = labelsIt.next();
      compCfg = compCfgsIt.next();

      if (View.isContainer(comp)) {
        // a sub-container: add this container to the tab group
        //  if tab group has not been created, then create it 
        // (but do not yet add it to the layout - this is performed last!)
        cont = View.toDataContainer(comp);
        alignX = compCfg.getAlignX();
        if (alignX != null)
          compAlign = toGroupAlignment(alignX);
        else  // use default alignment
          compAlign = Alignment.LEADING;

        if (tabGroup == null) {
          tabGroup = new JTabbedPane(
              JTabbedPane.TOP,  // tab placement 
              JTabbedPane.WRAP_TAB_LAYOUT // tab layout policy (wrap or scroll)
              );
          tabMouseHandler = new TabGroupMouseHandler(tabGroup);
        }
        
        //TODO: use compAlign (by wrapping cont in a panel)
        labelTxt = View.getDataContainerLabelAsString(compCfg);
        
        tabGroup.addTab(labelTxt, cont.getGUIComponent());
        tabIndex = tabGroup.getTabCount()-1;
            
        // use label as tab component but needs to listen to its mouse event
        // so that we can turn on the tab when use clicks on the label
        label.addMouseListener(tabMouseHandler);
        tabGroup.setTabComponentAt(tabIndex, label);
      } else {
        // a data field
        // add to the list of data field components to be added together 
        if (dfLabels == null) {
          dfLabels = new ArrayList();
          dfComps = new ArrayList();
          dfCfgs = new ArrayList();
        }
        dfLabels.add(label);
        dfComps.add(comp);
        dfCfgs.add(compCfg);
      }
    }
     
    
    // if there are data fields then create a sub-panel for them and add this panel to 
    // the layout
    if (dfLabels != null) {
      sub = createSubPanel(FlexiTwoColumnLayoutBuilder.class, dfLabels, dfComps, dfCfgs);
      
      // add panel to layout 
      hz.addComponent(sub, min, prefer, max);
      vert.addComponent(sub);
      
      // finalise 
      dfLabels = null; dfComps = null; dfCfgs = null;
    }
    
    // if tab group is created then add it to the layout
    if (tabGroup != null) {
      int selectedIndex = tabGroup.getSelectedIndex();
      tabMouseHandler.updateTabComponentAt(selectedIndex);
      
      hz.addComponent(tabGroup, min, prefer, max);
      vert.addComponent(tabGroup);
    }
    
    layout.setHorizontalGroup(hz);
    layout.setVerticalGroup(vert);
    
    // return the tab group
    List<JComponent> containers = new ArrayList<>();
    containers.add(tabGroup);
    return containers;
  }
}
