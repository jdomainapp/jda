package jda.mosa.view.assets.layout;

import java.util.Collection;

import javax.swing.GroupLayout;
import javax.swing.JComponent;

import jda.modules.mccl.conceptmodel.view.Region;

/**
 * @overview 
 *  A more constrained {@link FlexiLayoutBuilder} that follows the two-column layout principle.
 *  
 *  <p>To create a two-column effect, the labels of all the data field components (except those that are marked with label-only) have their sizes linked, i.e normalised to the max label size.
 *  Thus, all the sub-groups are standard 2-column sub-forms, whose labels are displayed on the left 
 *  and whose data components are displayed on the right.
 *  
 *  <p>Although this layout can be combined with other layouts (a behaviour inherited from {@link FlexiLayoutBuilder}), 
 *  the sub-forms may not be laid out as desired as the labels have all been fixed to the same size.  
 * 
 * @author dmle
 * 
 * @version 3.1
 */
public class FlexiTwoColumnLayoutBuilder extends FlexiLayoutBuilder {

  @Override
  protected void preLayout(GroupLayout layout, Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    super.preLayout(layout, labels, comps, compCfgs);
    
    linkSizeStandardDataFieldLabels(labels, comps, compCfgs);
  }
}
