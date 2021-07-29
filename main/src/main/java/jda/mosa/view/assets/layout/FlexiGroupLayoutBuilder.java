package jda.mosa.view.assets.layout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import jda.modules.common.types.properties.PropertyName;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.mosa.view.View;

/**
 * @overview 
 *  A {@link LayoutBuilder} that supports <b>a more flexible, manual grouping</b> of the data components.
 *  This grouping is more flexible than that provided by {@link FlexiLayoutBuilder} in that it does not require  
 *  a separate label-only component to be specified. Instead, the grouping is set directly in the 
 *  {@link Region} configuration, using property {@link PropertyName#view_objectForm_dataField_groupId},
 *  of the data components. All data components that have the same value set for this property belong to 
 *  the same group. 
 *  
 *  <p>Each group may contain both data field and sub-container components. 
 *  All the data components in a group are displayed in one {@link JPanel}, 
 *  whose layout builder is either specified in the {@link Region} of the first data component of the group 
 *  (in definition order) or {@link #DEFAULT_GROUP_LAYOUT_BUILDER} if none is specified. 
 *  
 *  <p>To help differentiate between groups, each group's panel can be shaded and/or bordered. Which effect
 *  to use may be specified in the {@link Region} configuration of the first data component of the group.   
 *      
 * @author dmle
 */
public class FlexiGroupLayoutBuilder extends LayoutBuilder {

  private static final Class<? extends LayoutBuilder> DEFAULT_GROUP_LAYOUT_BUILDER = TwoColumnLayoutBuilder.class;

  @Override
  protected Collection<JComponent> doLayout(GroupLayout layout,
      Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {
    ParallelGroup hz = layout.createParallelGroup(Alignment.LEADING);
    SequentialGroup vert = layout.createSequentialGroup();
    
    Iterator<JComponent> labelsIt = labels.iterator();
    Iterator<JComponent> compsIt = comps.iterator();
    Iterator<Region> compCfgsIt = compCfgs.iterator();
    Region compCfg; RegionDataField dfCfg;
    JComponent label, comp;
    boolean isSubContainer;

    JPanel sub = null;
    
    /*
     * pseudocode:
     *  for each c in comps
     *    if c.config.groupId != null   // start of a new group
     *      create a panel p containing (l,c) /\ pairs (l',c') that proceed c 
     *      up to the next c2 s.t. c2.config.groupId != c.config.groupId 
     *      add p to layout
     */
    List<JComponent> nextLabels = null, nextComps = null;
    List<Region> nextCompCfgs = null;
    Class groupBuilderType = null;
    String groupId;
    String currGroupId = null;
    boolean labelOnly;
    JComponent sectionLabel = null;
    Border groupBorder = null;
    Color groupBg = null; // TODO: support this if needed
    
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      label = labelsIt.next();
      compCfg = compCfgsIt.next();
      
      isSubContainer = View.isContainer(comp);
      
      // check if grouping is specified
      groupId = compCfg.getProperty(PropertyName.view_objectForm_dataField_groupId, String.class, null);
      
      // check if this is a group label
      if (!isSubContainer) {
        dfCfg = (RegionDataField) compCfg;
        labelOnly = dfCfg.getLabelOnly();
      } else {
        labelOnly = false;
      }
      
      if (groupId != null) {
        // manual grouping
        if (currGroupId == null || !groupId.equals(currGroupId)) {
          // the first grouping (no grouping so far) OR a new grouping
          // create group panel for all pairs (l,c) processed so far (if any)        
  
          if (nextLabels != null) {
            if (groupBuilderType == null)
              groupBuilderType = DEFAULT_GROUP_LAYOUT_BUILDER;
            
            if (currGroupId != null) {
              // only use border for group panel
              groupBorder = DEFAULT_PANEL_BORDER;
            }
              
            if (sectionLabel != null) {
              sub = createTitledSubPanel(groupBuilderType, sectionLabel, nextLabels, nextComps, nextCompCfgs, groupBorder, groupBg);
            } else {
              sub = createSubPanel(groupBuilderType, nextLabels, nextComps, nextCompCfgs, groupBorder, groupBg);
            }
          
            // add subpanel to layout 
            hz.addComponent(sub, min, prefer, max);
            vert.addComponent(sub);
            
            // reset 
            nextLabels = null; nextComps = null; nextCompCfgs = null;
            groupBuilderType = null;
          }
          
          currGroupId = groupId;
          
          // the builder type of this group
          if (groupBuilderType == null)
            groupBuilderType = compCfg.getLayoutBuilderType();
          
          // reset section label of group
          if (sectionLabel != null) sectionLabel = null;
        }
      } 

      // support section label (if specified)
      if (labelOnly)
        sectionLabel = label;
      
      // add (l,c) to the current group (or the global group if no groupId is specified at all)
      if (nextLabels == null) {
        nextLabels = new ArrayList();
        nextComps = new ArrayList();
        nextCompCfgs = new ArrayList();
      }
      nextLabels.add(label);
      nextComps.add(comp);
      nextCompCfgs.add(compCfg);
    } // end while compsIt
     
    // the last group panel or the global group panel if no grouping is used (if not created already)
    if (nextLabels != null) {
      if (groupBuilderType == null)
        groupBuilderType = DEFAULT_GROUP_LAYOUT_BUILDER;
      
      if (currGroupId != null) {
        // only use border for group panel
        groupBorder = DEFAULT_PANEL_BORDER;
      }
      
      if (sectionLabel != null) {
        sub = createTitledSubPanel(groupBuilderType, sectionLabel, nextLabels, nextComps, nextCompCfgs, groupBorder, groupBg);
      } else {
        sub = createSubPanel(groupBuilderType, nextLabels, nextComps, nextCompCfgs, groupBorder, groupBg);
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
