package jda.mosa.view.assets.layout;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import jda.modules.mccl.conceptmodel.view.Region;

import javax.swing.JComponent;

/**
 * @overview
 *  A <b>very compact <tt>layout</tt></b> displays only a single data component on the data container
 *  of this layout. This data component is the component that is configured with 
 *  {@link Region#isDisplayVisible()}=<tt>true</tt>. 
 *  
 *  <p>Use this layout for object forms that have only one visible field and that the value of this field 
 *  (e.g. an HTML editor pane) should be displayed to span the entire form. 
 *    
 * @author dmle
 * @version 3.0
 */
public class SingleDataComponentLayoutBuilder extends LayoutBuilder {

  /**
   * @modifies layout 
   * @effects 
   *  create a <b>compact <tt>layout</tt></b> that group all sub-containers together in a tab group. 
 *  The remaining data fields are grouped together in a sub-panel.
   */
  @Override
  public Collection<JComponent> doLayout(GroupLayout layout, Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {

//    //identify the sub-containers so that they can be added to separate panels
    SequentialGroup hz = //layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        layout.createSequentialGroup();
    SequentialGroup vert = layout.createSequentialGroup();

    //Iterator<JComponent> labelsIt = labels.iterator();
    Iterator<JComponent> compsIt = comps.iterator();
    Iterator<Region> compCfgsIt = compCfgs.iterator();
    JComponent comp;
//    JComponent label, comp;
//    String labelTxt;
    
    Region compCfg; //RegionDataField dfCfg;
//    AlignmentX alignX;
//    Alignment compAlign;
    ParallelGroup compGrp = layout.createParallelGroup(GroupLayout.Alignment.LEADING,true);
    hz.addGroup(compGrp);
    
    while (compsIt.hasNext()) {
      comp = compsIt.next();
      //label = labelsIt.next();
      compCfg = compCfgsIt.next();

      if (compCfg.isDisplayVisible()) {
        compGrp.addComponent(comp);//, min, prefer, max);
        vert.addComponent(comp);
        break;  // stop when found first visible component
      }
    }
    
    layout.setHorizontalGroup(hz);
    layout.setVerticalGroup(vert);
    
    return null;
  }
}
