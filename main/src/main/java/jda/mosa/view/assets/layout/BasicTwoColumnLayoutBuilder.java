package jda.mosa.view.assets.layout;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;

import javax.swing.JComponent;

/**
 * @overview 
 *  A basic two-column layout which arranges the labels and their associated data components (which can be data field 
 *  or data container) in two  
 *  columns: the labels are displayed right-aligned on the left column, while the data components are displayed 
 *  left-aligned on the right column.   
 *  
 *  <p>The width of each column is the largest width of the components that are displayed on that column.
 *   
 * @author dmle
 */
public class BasicTwoColumnLayoutBuilder extends LayoutBuilder {

  /**
   * @effects 
   *  generate a 2-column layout with column 1 containing the labels, column 2 containing the components
   */
  @Override
  public Collection<JComponent> doLayout(GroupLayout groupLayout, Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {
    
    SequentialGroup hz = groupLayout.createSequentialGroup();
    SequentialGroup vert = groupLayout.createSequentialGroup();

    // horizontal grouping:
    // label comp
    // ... --> ...
    // label comp
    
    // label sub-groups
    ParallelGroup glabels = groupLayout.createParallelGroup(Alignment.TRAILING);
    for (JComponent jl : labels) {
      if (jl != null)
        glabels.addComponent(jl,min, prefer, max);
    }

    // component sub-groups
    Alignment compAlign = Alignment.LEADING;
    ParallelGroup gcomps = groupLayout.createParallelGroup(compAlign); 
    JDataContainer cont;
    Region compCfg;
    AlignmentX alignX;
    for (JComponent jc : comps) {
      if (jc != null) {
        // containers donot grow horizontally
        if (View.isContainer(jc)) {
          // v2.7.2: use alignment setting of the linked region
          cont = View.toDataContainer(jc);
          compCfg = cont.getContainerConfig(); //getComponentConfig(cont.getGUIComponent());
          alignX = compCfg.getAlignX();
          if (alignX != null)
            compAlign = toGroupAlignment(alignX);
          else  // use default alignment
            compAlign = Alignment.LEADING;
          
          gcomps.addComponent(jc, compAlign, min, prefer, max);
        } else { // others may 
          gcomps.addComponent(jc);
        }
      }
    }

    // v3.1: groupLayout.setHorizontalGroup(groupLayout.createSequentialGroup()
    //    .addGroup(glabels).addGroup(gcomps));
    hz.addGroup(glabels).addGroup(gcomps);

    // vertical grouping:
    // label | comp
    // ...
    // components donot grow vertically
    ParallelGroup labelCompPair;
    JComponent jc;
    Iterator<JComponent> compsIt = comps.iterator();
    for (JComponent jl : labels) {
      jc = compsIt.next();
      labelCompPair = groupLayout.createParallelGroup(Alignment.LEADING);
      if (jl != null) {
        labelCompPair.addComponent(jl, min, prefer, max);
        if (jc != null)
          labelCompPair.addComponent(jc, min, prefer, max);
      } else {
        // pair.addGap(0,0,0);
        if (jc != null)
          labelCompPair.addComponent(jc); // min, prefer, max
      }
      
      vert.addGroup(labelCompPair);
    }
    
    groupLayout.setHorizontalGroup(hz);
    groupLayout.setVerticalGroup(vert);
    
    return null;
  }
}
