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
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.panels.card.CardPanel;
import jda.mosa.view.assets.panels.card.SequentialCardPanel;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @overview
 *  A layout builder that uses {@link CardPanel} to organise components in a layout that preserves the structure 
 *  of an activity pattern.
 *  It displays the views of the actions to be performed using the same content area. 
 *  These views are controlled by a set of "buttons", which are displayed separately in another panel.
 *  
 * @author Duc Minh Le (ducmle)
 * 
 * @version 5.2
 */
public abstract class CardLayoutBuilder extends LayoutBuilder {

  @Override
  protected void preLayout(GroupLayout layout, Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    super.preLayout(layout, labels, comps, compCfgs);
    
    linkSizeStandardDataFieldLabels(labels, comps, compCfgs);
  }
  
  
  /**
   * @modifies layout 
   * @effects 
   *  create a <b>sequential <tt>layout</tt></b> that displays the tasks to be performed one after another
   */
  @Override
  public Collection<JComponent> doLayout(GroupLayout layout, Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {

    // create card panel

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
    
    CardPanel cardPanel = null;
        
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      label = labelsIt.next();
      compCfg = compCfgsIt.next();

      if (View.isContainer(comp)) {
        // a sub-container: create this as a card panel 
        //  if card panel has not been created, then create it 
        // (but do not yet add it to the layout - this is performed last!)
        cont = View.toDataContainer(comp);
        alignX = compCfg.getAlignX();
        if (alignX != null)
          compAlign = toGroupAlignment(alignX);
        else  // use default alignment
          compAlign = Alignment.LEADING;

        if (cardPanel == null) {
          // the card panel
          cardPanel = createCardPanel();
        }
        
        //TODO: use compAlign (by wrapping cont in a panel)
        labelTxt = View.getDataContainerLabelAsString(compCfg);
        // create a card button but do not yet add it to cardButtonsPanel. We will add them 
        // later together with the arrowed labels
        // v5.6: added container's config
        //cardPanel.createCard(cont.getGUIComponent(), labelTxt);
        cardPanel.createCard(cont.getGUIComponent(), cont.getContainerConfig() , labelTxt);
      } else {
        // a data field
        // add to the list of data field components to be added together 
        if (dfLabels == null) {
          dfLabels = new ArrayList<>();
          dfComps = new ArrayList<>();
          dfCfgs = new ArrayList<>();
        }
        dfLabels.add(label);
        dfComps.add(comp);
        dfCfgs.add(compCfg);
      }
    }

    // create card button panel first
    if (cardPanel != null) {
      // set up the cardButtonsPanel
      cardPanel.createCardButtonsPanel();

      JPanel cardButtonsPanel = cardPanel.getCardButtonsPanel();
      
      hz.addComponent(cardButtonsPanel, min, prefer, max);
      vert.addComponent(cardButtonsPanel);
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
    
    // finally, if cardPanel is created then add it to the layout
    if (cardPanel != null) {
      hz.addComponent(cardPanel, min, prefer, max);
      vert.addComponent(cardPanel);
    }
    
    layout.setHorizontalGroup(hz);
    layout.setVerticalGroup(vert);
    
    // return the card panel
    List<JComponent> containers = new ArrayList<>();
    containers.add(cardPanel);
    return containers;
  }


  /**
   * @effects 
   *  Create a sub-type of {@link CardPanel} suitable for this builder.
   */
  protected abstract CardPanel createCardPanel();
}
