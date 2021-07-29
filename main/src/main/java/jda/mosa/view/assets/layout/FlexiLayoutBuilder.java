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
import jda.mosa.view.View;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @overview 
 *  A <b>flexible layout builder</b> which supports the logical groupings of the labels and data components 
 *  into individual sub-regions. Each group is displayed in a sub-panel that <b>can have its own layout</b>.
 *  
 *  <p>A group is marked by a special (label, data field) pair in which 
 *  the data field is configured not to appear. This label is called a <b>section label</b>. 
 *  All (label, data field) pairs that follow a sectional label in the pair list, up to the next section label (or end of 
 *  pair list) belong to one group.   
 *  
 *  <p>The layout builder of the group is one that is specified in the {@link Region} configuration  
 *  of the section label of the group. If not then {@link #DEFAULT_SECTION_BUILDER} is used instead. 
 *  
 *  <p>This layout is flexible in that it can be combined with other layouts (e.g. {@link HorizontalLayoutBuilder} to 
 *  create a truly complex form. 
 *  
 * @author dmle
 */
public class FlexiLayoutBuilder extends LayoutBuilder {

  private static final Class DEFAULT_SECTION_BUILDER = BasicTwoColumnLayoutBuilder.class;

  /**
   * @effects 
   *  generate a flexible layout
   */
  @Override
  public Collection<JComponent> doLayout(GroupLayout layout, Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {
    
    ParallelGroup hz = layout.createParallelGroup(Alignment.LEADING);
    SequentialGroup vert = layout.createSequentialGroup();
    
    // horizontal grouping:
    // label comp
    // ... --> ...
    // label comp
    
    Iterator<JComponent> labelsIt = labels.iterator();
    Iterator<JComponent> compsIt = comps.iterator();
    Iterator<Region> compCfgsIt = compCfgs.iterator();
    Region compCfg; RegionDataField dfCfg;
    JComponent label, comp;
//    JDataContainer cont;
//    AlignmentX alignX;
    boolean labelOnly = false;
    boolean isSubContainer;

    JPanel sub = null;
    
    /*
     * pseudocode:
     *  for each label lbl in labels
     *    if l.labelOnly = true   // start of a new section
     *      create a panel p containing l /\ pairs (l',c') that proceed l 
     *      up to the next l2 s.t. l2.labelOnly=true 
     *      add p to layout
     */
    List<JComponent> nextLabels = null, nextComps = null;
    List<Region> nextCompCfgs = null;
    JComponent sectionLabel = null;
    Class sectionBuilderType = null;
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      label = labelsIt.next();
      compCfg = compCfgsIt.next();
      
      isSubContainer = View.isContainer(comp);
      
      if (!isSubContainer) {
        dfCfg = (RegionDataField) compCfg;
        labelOnly = dfCfg.getLabelOnly();
      } else {
        labelOnly = false;
      }
      
      if (labelOnly) {
        // a new section
        if (nextLabels != null) {
          // create a subpanel from the components added so far
          if (sectionBuilderType == null)
            sectionBuilderType = DEFAULT_SECTION_BUILDER;
          
          if (sectionLabel != null) {
            sub = createTitledSubPanel(sectionBuilderType, sectionLabel, nextLabels, nextComps, nextCompCfgs);
          } else {
            sub = createSubPanel(sectionBuilderType, nextLabels, nextComps, nextCompCfgs);
          }
          
          // add subpanel to layout 
          hz.addComponent(sub, min, prefer, max);
          vert.addComponent(sub);

          // reset 
          nextLabels = null; nextComps = null; nextCompCfgs = null;
        }
        
        sectionBuilderType = compCfg.getLayoutBuilderType();
        sectionLabel = label;
      } else {
        // part of the current section
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
      if (sectionBuilderType == null)
        sectionBuilderType =DEFAULT_SECTION_BUILDER;
      
      if (sectionLabel != null) {
        sub = createTitledSubPanel(sectionBuilderType, sectionLabel, nextLabels, nextComps, nextCompCfgs);
      } else {
        sub = createSubPanel(sectionBuilderType, nextLabels, nextComps, nextCompCfgs);
      }
      
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
