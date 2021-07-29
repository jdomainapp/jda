package jda.mosa.view.assets.layout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import jda.modules.mccl.conceptmodel.view.Region;

import javax.swing.JComponent;

/**
 * @overview 
 *    Builds a <b>horizontal</b> group layout that groups labels and data components into pairs
 *    and arrange the pairs in rows (from left-to-right, top-to-bottom), with each row having the same fixed size (number of components).   
 *      
 * @author dmle
 */
public class HorizontalLayoutBuilder extends LayoutBuilder {

  // vertical gap b/w label and comp (need not be changed!!)
  private static final int vgap = 20; 
  // number of pairs (label, comp) per group, each group is arranged in a sub-panel
  private static final int groupSize = 3; 

  @Override
  protected Collection<JComponent> doLayout(GroupLayout layout, Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    // use Lists to ease processing
    List<JComponent> lstLabels = new ArrayList();
    lstLabels.addAll(labels);
    List<JComponent> lstComps = new ArrayList();
    lstComps.addAll(comps);
    List<Region> lstCompCfgs = new ArrayList();
    lstCompCfgs.addAll(compCfgs);
    
    final int startIndex = 0; 
    final int endIndex = lstLabels.size()-1;
    
    return doLayout(layout, lstLabels, lstComps, lstCompCfgs, groupSize, startIndex, endIndex);
  }

  /**
   * @effects 
   *  create a group layout for pairs (label, data comp) that are in the range <tt>[startIndex, endIndex]></tt>
   *  in <tt>labels</tt> and <tt>comps</tt>, with <tt>groupSize</tt> number of pairs per group 
   */
  private Collection<JComponent> doLayout(GroupLayout layout, List<JComponent> labels,
      List<JComponent> comps, List<Region> compCfgs, int groupSize,
      int startIndex, int endIndex) {
    // support multiple vertical groupings, each having a pre-defined number of components
    final int numComps = endIndex-startIndex+1; //labelComps.size();
    //final int groupSize = 3;
    int numGroups;
    double frac =  numComps / ((double)groupSize);
    
    // override component indent: make it 0 so that label and comp are left aligned
    // and easier to see in a horizontal layout
    final int compIndent = 0;
    
    
    if (frac > ((int)frac))  {
      numGroups = ((int)frac) + 1;
    } else {
      numGroups = (int)frac;
    }
    
    //System.out.printf("Creating group layout: %n  %d (comps) %n  %d (groups) %n  %d (per group)...%n", numComps, numGroups, groupSize);

    
    /*
    // horizontal grouping: 
     *                                 
     * labels[1]  labels[2]  labels[3]
     * comps[1]   comps[2]   comps[3]
     *  
     * labels[4]  labels[5]  labels[6]
     * comps[4]   comps[5]   comps[6] 
     * ...
     *  
     *   sequential group (           // hz
     *        parallel group 1 (        // group
     *          parallel group(           // pair
     *            label1, 
     *            sequential group( gap, comp1 )   // comp group
     *          )
     *          parallel group(
     *            label4, 
     *            sequential group( gap, comp4 )   
     *          ) 
     *          ...
     *        )
     *        parallel group 2 (        // group
     *          parallel group(           // pair
     *            label2, 
     *            sequential group( gap, comp2 )   // comp group
     *          )
     *          parallel group(
     *            label5, 
     *            sequential group( gap, comp5 )   
     *          ) 
     *          ...
     *        )
     *        ....
     *   )
     * 
     */
    // components may grow horizontally
    SequentialGroup hz = layout.createSequentialGroup();
    ParallelGroup hgroup;
    ParallelGroup pair; // GroupLayout.Alignment.LEADING
    SequentialGroup compGroup; // GroupLayout.Alignment.LEADING
    JComponent label;
    JComponent comp;

    int index;
    //System.out.printf("Creating horizontal grouping...%n");
    
    boolean emptyGroup;
    for (int i = 0; i < groupSize; i++) {
      hgroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING); //groupLayout.createSequentialGroup();
      
      emptyGroup = true;
      for (int g = 0; g < numGroups; g++) {
        index = startIndex + g * groupSize + i;
        if (index > endIndex)
          break;
        
        if (emptyGroup) {
          //System.out.printf("  group %d: ", (i+1));

          emptyGroup = false;
        }
        
        pair = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        label = labels.get(index);
        comp = comps.get(index);
        
        //System.out.printf("  [%d]: %s", index, label.getText());
        
        if (comp != null) {
          // add label first
          pair.addComponent(label, min, prefer, max);
          
          // add comp into a sequential group that has a container gap used for indentation: 
          //  sequential group ( gap, comp)
          compGroup = layout.createSequentialGroup();
          compGroup.addContainerGap(compIndent, compIndent);
          compGroup.addComponent(comp, min, prefer, max);
          
          // add comp's group 
          pair.addGroup(compGroup);
        } else {
          pair.addComponent(label); // min, prefer, max
        }
        
        hgroup.addGroup(pair);
      }
      
      //System.out.println();
      
      if (!emptyGroup) {
        hz.addGroup(hgroup);
      }
    }
    
    layout.setHorizontalGroup(hz);

    /*
     * vertical grouping:
     * 
     * labels[1]  labels[2]  labels[3]
     * comps[1]   comps[2]   comps[3]
     *  
     * labels[4]  labels[5]  labels[6]
     * comps[4]   comps[5]   comps[6] 
     * ...
     *  
     *   sequential group (           // hz
     *        parallel group 1 (        // group
     *          parallel group(           // pair
     *            label1, 
     *            sequential group( gap, comp1 )   // comp group
     *          )
     *          parallel group(
     *            label2, 
     *            sequential group( gap, comp2 )   
     *          ) 
     *          ...
     *        )
     *        parallel group 2 (        // group
     *          parallel group(           // pair
     *            label4, 
     *            sequential group( gap, comp4 )   // comp group
     *          )
     *          parallel group(
     *            label5, 
     *            sequential group( gap, comp5 )   
     *          ) 
     *          ...
     *        )
     *        ....
     *   )
     * 
     */
    // components donot grow vertically
    //System.out.printf("Creating vertical grouping...%n");
    ParallelGroup vgroup;
    SequentialGroup vert = layout.createSequentialGroup();
    for (int g = 0; g < numGroups; g++) {
      index = startIndex + g * groupSize;
      vgroup = layout.createParallelGroup();
      //vgroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);

      emptyGroup = true;
      
      for (int i = index; i < index+groupSize && i <= endIndex; i++) {
        pair = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        label = labels.get(i);
        comp = comps.get(i);
        
        if (emptyGroup) {
          //System.out.printf("  group %d: ", (g+1));

          emptyGroup = false;
        }

        //System.out.printf("  [%d]: %s", i, label.getText());
        
        if (comp != null) {
          // add label first
          pair.addComponent(label, min, prefer, max);

          // add comp into a sequential group that has a container gap used for indentation: 
          //  sequential group ( gap, comp)
          compGroup = layout.createSequentialGroup();
          compGroup.addContainerGap(vgap, vgap);
          compGroup.addComponent(comp, min, prefer, max);
          
          // add comp's group 
          pair.addGroup(compGroup);
        } else {
          pair.addComponent(label); // min, prefer, max
        }
        
        vgroup.addGroup(pair);
      }
      
      //System.out.println();
      
      if (!emptyGroup) {
        vert.addGroup(vgroup);
      }
    }
    
    layout.setVerticalGroup(vert);   
    
    return null;
  }

}
