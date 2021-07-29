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
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.view.View;
import jda.mosa.view.assets.JDataContainer;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @overview
 *  A <b>compact two-column <tt>layout</tt></b> (compared to {@link BasicTwoColumnLayoutBuilder}) 
 *    in which sub-containers are treated differently from the normal data fields. 
 *  
 *  <p>To create a two-column effect, the labels of all the data field components (except those that are marked with label-only) have their sizes linked, i.e normalised to the max label size.
 *  
 *  <p>Unlike data fields, the labels of the sub-containers are kept unchanged and are displayed on top of the sub-containers (rather than on the left-side). 
 *  The container is immediately below and indented. This makes the overall layout more compact.   
 *    
 *  <p>All data fields that are in between two sub-containers are grouped into one (invisible) {@link JPanel}, 
 *  whose layout builder is {@link #DEFAULT_GROUP_LAYOUT_BUILDER}. Note that this is a natural grouping of data fields,
 *  which is <b><i>different from the manual grouping configuration of the data fields (if any)</i></b>.
 *  
 *  <p>This layout builder DOESNOT support manual grouping of the data components. To use this grouping, 
 *  use a different layout builder.   
 *  
 * @author dmle
 *
 */
public class TwoColumnLayoutBuilder extends LayoutBuilder {

  private static final Class<? extends LayoutBuilder> DEFAULT_GROUP_LAYOUT_BUILDER = FlexiTwoColumnLayoutBuilder.class;

  @Override
  protected void preLayout(GroupLayout layout, Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    super.preLayout(layout, labels, comps, compCfgs);
    
    linkSizeStandardDataFieldLabels(labels, comps, compCfgs);
  }

  /**
   * @modifies layout 
   * @effects 
   *  create a more flexible two-column <tt>layout</tt> in which sub-containers are treated differently from the normal data fields, 
   *  in that their labels are displayed on the top rather than on the side
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

    List<JComponent> nextLabels = null, nextComps = null;
    List<Region> nextCompCfgs = null;
    
    JPanel sub;
    JDataContainer cont;
    GroupLayout.Group compGroup;
    Region compCfg; RegionDataField dfCfg;
    AlignmentX alignX;
    Alignment compAlign;
    
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      label = labelsIt.next();
      compCfg = compCfgsIt.next();

      if (View.isContainer(comp)) {
        // a sub-container
        if (nextLabels != null) {
          // create a subpanel from the components added so far 
          sub = createSubPanel(DEFAULT_GROUP_LAYOUT_BUILDER, nextLabels, nextComps, nextCompCfgs);
          
          // add subpanel to layout 
          hz.addComponent(sub, min, prefer, max);
          vert.addComponent(sub);

          // reset 
          nextLabels = null; nextComps = null; nextCompCfgs = null;
        }
        
        // add sub-container to layout
        // v3.1: cont = View.toDataContainer(comp);
        //compCfg = cont.getContainerConfig();
        
        alignX = compCfg.getAlignX();
        if (alignX != null)
          compAlign = toGroupAlignment(alignX);
        else  // use default alignment
          compAlign = Alignment.LEADING;
        
        compGroup = createHorizontalCompGroup(layout, label, comp, compAlign, compIndent);
        hz.addGroup(compGroup);
        vert.addGroup(layout.createSequentialGroup().
            addComponent(label).
            addComponent(comp));
      } else {
        // a data field
        // add to the list of components to be added next
        if (nextLabels == null) {
          nextLabels = new ArrayList();
          nextComps = new ArrayList();
          nextCompCfgs = new ArrayList();
        }
        nextLabels.add(label);
        nextComps.add(comp);
        nextCompCfgs.add(compCfg);
      }
    }
     
    
    // the last panel (if not created already)
    if (nextLabels != null) {
      sub = createSubPanel(DEFAULT_GROUP_LAYOUT_BUILDER, nextLabels, nextComps, nextCompCfgs);
      
      // add panel to layout 
      hz.addComponent(sub, min, prefer, max);
      vert.addComponent(sub);
      
      // reset 
      nextLabels = null; nextComps = null; nextCompCfgs = null;
    }

    layout.setHorizontalGroup(hz);
    layout.setVerticalGroup(vert);
    
    return null;
  }
}
