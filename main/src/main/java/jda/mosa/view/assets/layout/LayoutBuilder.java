package jda.mosa.view.assets.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.view.View;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview
 *    Responsible for creating the {@link GroupLayout} of a data container or for a group of data fields in a data container. 
 *     
 * @author dmle
 */
public abstract class LayoutBuilder {
  protected static final int min = GroupLayout.PREFERRED_SIZE;
  protected static final int prefer = GroupLayout.DEFAULT_SIZE;
  protected static final int max = GroupLayout.PREFERRED_SIZE;
  
  protected static final int compIndent = 15;
  
  protected static final Border DEFAULT_PANEL_BORDER =
      BorderFactory.createCompoundBorder(
        //outerBorder: empty border to implement gaps from the outside to the border  
        BorderFactory.createEmptyBorder(5, 0, 5, 0), 
        //insideBorder:
        BorderFactory.createCompoundBorder(
            // actual border
            BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.YELLOW, Color.BLUE),
            // empty border: to implement gaps from border to the inside
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        )
  );
  
  protected static final Color DEFAULT_PANEL_BACKGROUND = GUIToolkit.COLOUR_LIGHT_YELLOW_2;

  // cache builder instances for re-use
  private static Map<Class,LayoutBuilder> builderMap = new HashMap();
  
  /**
   * @effects 
   *  create and return a <tt>LayoutManager</tt> whose type is <tt>cls</tt> and that 
   *  uses <tt>layout</tt>.
   */
  public static LayoutBuilder getInstance(Class<? extends LayoutBuilder> cls) throws NotPossibleException {
    try {
      
      // check in cache first 
      LayoutBuilder instance = builderMap.get(cls);
      
      if (instance == null) {
        // create new 
        instance = cls.newInstance();
        builderMap.put(cls, instance);
      }
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {cls.getSimpleName(), ""});
    }
  }
  
  /**
   * @modifies layout, labels, comps
   * @requires 
   *  labels.size = comps.size /\ labels.size = compCfgs.size /\ 
   *  
   *  there is a one-to-one, order-preserving mapping from labels to comps and from labels to compCfgs
   * @effects 
   *  {@link #preLayout(GroupLayout, Collection, Collection, Collection)}
   *  result = {@link #doLayout(GroupLayout, Collection, Collection, Collection)}
   *  {@link #postLayout(GroupLayout, Collection, Collection, Collection)}
   *  
   *  <p>If that are containers used for organising the sub-containers among <tt>comps</tt>
   *    result = <tt>Collection({@link JComponent})</tt> of those containers
   *  else 
   *    result = <tt>null</tt>  
   */
  public Collection<JComponent> createLayout(GroupLayout layout, 
      Collection<JComponent> labels, Collection<JComponent> comps, Collection<Region> compCfgs) throws NotPossibleException {
    preLayout(layout, labels, comps, compCfgs);
    Collection<JComponent> containers = doLayout(layout, labels, comps, compCfgs);
    postLayout(layout, labels, comps, compCfgs);
    
    return containers;
  }
  
  /**
   * Sub-types need first invoke <tt>super</tt> method first.
   * 
   * @modifies <tt>layout, comps, labels</tt>
   * @effects 
   *  prepare <tt>layout, labels, and comps</tt> <b>before</b> {@link #doLayout(GroupLayout, Collection, Collection, Collection)} 
   */
  protected void preLayout(GroupLayout layout,
      Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {
    // for sub-types to override
    layout.setAutoCreateGaps(true);
    //layout.setAutoCreateContainerGaps(true);  
  }
  
  /**
   * @requires 
   *  labels.size = comps.size /\ labels.size = compCfgs.size /\ 
   *  
   *  there is a one-to-one, order-preserving mapping from labels to comps and from labels to compCfgs
   * @effects 
   *  create layout with the specified components. 
   *  If that are containers used for organising the sub-containers among <tt>comps</tt>
   *    return <tt>Collection({@link JComponent})</tt> of those containers
   *  else 
   *    return <tt>null</tt>  
   */
  protected abstract Collection<JComponent> doLayout(GroupLayout layout, 
      Collection<JComponent> labels, Collection<JComponent> comps, Collection<Region> compCfgs);

  /**
   * @modifies <tt>layout, comps, labels</tt>
   * @effects 
   *  finalise <tt>layout, labels, and comps</tt> <b>after</b> {@link #doLayout(GroupLayout, Collection, Collection, Collection)}
   */
  protected void postLayout(GroupLayout layout,
      Collection<JComponent> labels, Collection<JComponent> comps,
      Collection<Region> compCfgs) {
    // for sub-types to override
  }
  
  /**
   * @effects 
   *  convert <tt>alignX</tt> to the corresponding {@link GroupLayout.Alignment}
   */
  protected Alignment toGroupAlignment(AlignmentX alignX) {
    if (alignX == AlignmentX.Right) {
      return Alignment.TRAILING; 
    } else if (alignX == AlignmentX.Center) {
      return Alignment.CENTER;
    } else {
      // rest
      return Alignment.LEADING;
    }
  }
  
  /**
   * Factory method.
   * 
   * @effects 
   *  create and return an <b>invisible</b> <tt>JPanel</tt> containing <tt>labels, comps, compCfgs</tt> whose layout is 
   *  the specified layout class <tt>builderCls</tt>
   *  
   */
  protected static JPanel createSubPanel(
      Class<? extends LayoutBuilder> builderCls,
      List<JComponent> labels,
      List<JComponent> comps, List<Region> compCfgs) throws NotPossibleException {
//    JPanel panel = new JPanel();
//    
//    GroupLayout layout = new GroupLayout(panel);
//    panel.setLayout(layout);
//    
//    LayoutBuilder lm = LayoutBuilder.getInstance(builderCls);
//    
//    lm.createLayout(layout, labels, comps, compCfgs);
//    
//    // v3.1: support panel effect
//    panel.setBorder(DEFAULT_PANEL_BORDER);
//    
//    return panel;
    // v3.1: invoke method with no border
    return createSubPanel(builderCls, labels, comps, compCfgs, null, null );
  }

  /**
   * Factory method.
   * 
   * @effects 
   *  create and return a <tt>JPanel</tt> containing <tt>labels, comps, compCfgs</tt> whose layout is 
   *  the specified layout class <tt>builderCls</tt>;
   *  that has border <tt>withBorder</tt> if specified; 
   *  and that has background color <tt>withBgColor</tt> if specified.  
   * @version 3.1
   */
  protected static JPanel createSubPanel(
      Class<? extends LayoutBuilder> builderCls,
      List<JComponent> labels,
      List<JComponent> comps, List<Region> compCfgs, Border withBorder, Color withBgColor) throws NotPossibleException {
    JPanel panel = new JPanel();
    
    GroupLayout layout = new GroupLayout(panel);
    panel.setLayout(layout);
    
    LayoutBuilder lm = LayoutBuilder.getInstance(builderCls);
    
    lm.createLayout(layout, labels, comps, compCfgs);
    
    // v3.1: support panel effect
    if (withBorder != null)
      panel.setBorder(withBorder);
    
    if (withBgColor != null) {
      panel.setBackground(withBgColor);
    } else {
      // v3.2: added this case to make transparent if background colour is not specified
      panel.setOpaque(false);
    }

    return panel;
  }
  
  /**
   * Factory method. Works similar to {@link #createSubPanel(Class, List, List, List)} except that it adds an extra title to the top.
   * 
   * @effects 
   *  create and return an <b>invisible</b> <tt>JPanel</tt> containing <tt>labels, comps, compCfgs</tt> whose layout is 
   *  the specified layout class <tt>builderCls</tt> and that <b>has a title</b> specified by <tt>titleLabel</tt>
   */
  protected static JPanel createTitledSubPanel(
      Class<? extends LayoutBuilder> builderCls,
      JComponent titleLabel, 
      List<JComponent> labels,
      List<JComponent> comps, List<Region> compCfgs) throws NotPossibleException {

    /*v3.1: invoke method with effects 
    // create the panel 
    GridBagLayout layout = new GridBagLayout(); 
    JPanel panel = new JPanel(layout);
    
    // debug
    //panel.setBackground(Color.LIGHT_GRAY);
    
    // add title to panel to first row
    GridBagConstraints c = new GridBagConstraints();
    
    c.weightx = 0; // all extra horiz.space for display component 
    c.weighty = 0; // all extra vertical space 
    c.fill = GridBagConstraints.NONE; 
    c.gridx= 0; c.gridy = 0;
    c.anchor=GridBagConstraints.LINE_START; // left
    panel.add(titleLabel, c);
    
    // add a sub-panel containing rest of components to second row
    JPanel subPanel = createSubPanel(builderCls, labels, comps, compCfgs);

    // debug
    //subPanel.setBackground(Color.WHITE);
    
    c.gridy = 1;
    c.gridheight=GridBagConstraints.REMAINDER;
    panel.add(subPanel, c);
    
    // v3.1: support panel effect
    panel.setBorder(DEFAULT_PANEL_BORDER);
    return panel;
    */
    return createTitledSubPanel(builderCls, titleLabel, labels, comps, compCfgs,  null, null);
  }
  
  /**
   * Factory method. Works similar to {@link #createSubPanel(Class, List, List, List)} except that it adds an extra title to the top.
   * 
   * @effects 
   *  create and return a <tt>JPanel</tt> containing <tt>labels, comps, compCfgs</tt> whose layout is 
   *  the specified layout class <tt>builderCls</tt>; 
   *  that <b>has a title</b> specified by <tt>titleLabel</tt>; 
   *  that has <tt>withBorder</tt> if specified; 
   *  and that has <tt>withBgColor</tt> if specified
   * @version 3.1
   */
  protected static JPanel createTitledSubPanel(
      Class<? extends LayoutBuilder> builderCls,
      JComponent titleLabel, 
      List<JComponent> labels,
      List<JComponent> comps, List<Region> compCfgs, Border withBorder, Color withBgColor) throws NotPossibleException {

    // create the panel 
    GridBagLayout layout = new GridBagLayout(); 
    JPanel panel = new JPanel(layout);
    
    // debug
    //panel.setBackground(Color.LIGHT_GRAY);
    
    // add title to panel to first row
    GridBagConstraints c = new GridBagConstraints();
    
    c.weightx = 0; // all extra horiz.space for display component 
    c.weighty = 0; // all extra vertical space 
    c.fill = GridBagConstraints.NONE; 
    c.gridx= 0; c.gridy = 0;
    c.anchor=GridBagConstraints.LINE_START; // left
    panel.add(titleLabel, c);
    
    // add an invisible sub-panel containing rest of components to second row
    JPanel subPanel = createSubPanel(builderCls, labels, comps, compCfgs, null, null);

    // debug
    //subPanel.setBackground(Color.WHITE);
    
    c.gridy = 1;
    c.gridheight=GridBagConstraints.REMAINDER;
    panel.add(subPanel, c);
    
    // v3.1: support panel effect
    if (withBorder != null)
      panel.setBorder(withBorder);
    
    if (withBgColor != null) {
      panel.setBackground(withBgColor);
    }
    
    return panel;
  }
  
  /**
   * Factory method.
   * @effects 
   *  create in <tt>layout</tt> a vertical <tt>Group</tt> containing <tt>label</tt> and <tt>comp</t>, in which 
   *  <tt>comp</tt> is displayed below <tt>label</tt> with indentation <tt>compIndent</tt> and with alignment 
   *  <tt>compAlign</tt> 
   */
  protected static javax.swing.GroupLayout.Group createHorizontalCompGroup(GroupLayout layout, JComponent label,
      JComponent comp, Alignment compAlign, int compIndent) {
    javax.swing.GroupLayout.Group pair = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
    
    // add label first
    pair.addComponent(label, min, prefer, max);
    
    // add comp into a sequential group that has a container gap used for indentation: 
    //  sequential group ( gap, comp)
    SequentialGroup compGroup = layout.createSequentialGroup();
    compGroup.addContainerGap(compIndent, compIndent);
    //compGroup.addComponent(comp, min, prefer, max);
    compGroup.addGroup(layout.createParallelGroup().
        addComponent(comp, compAlign, min, prefer, max));
    
    // add comp's group 
    pair.addGroup(compGroup);
      
    return pair;
  }
  
  /**
   * @effects 
   *  if exist sub-containers in <tt>comps</tt> or exist 'sectional' labels in <tt>labels</tt> (i.e. labels whose  <tt>config.labelOnly = true</tt>)<br> 
   *      link size (same terminology as {@link GroupLayout#linkSize(java.awt.Component...)}, which means 'make same size as max size in the group') all the labels in <tt>labels</tt> that 
   *      are (1) not those of the sub-containers and (2) not used as section labels
   *  <br>else
   *    do nothing
   */
  protected void linkSizeStandardDataFieldLabels(Collection<JComponent> labels,
      Collection<JComponent> comps, Collection<Region> compCfgs) {
    // preparation: standardise widths of data field labels
    // find the max label width and force all linked labels to have the same width
    double maxWidth = 0;
    double lblWidth, lblHeight;
    Iterator<JComponent> labelsIt = labels.iterator();
    Iterator<JComponent> compsIt = comps.iterator();
    Iterator<Region> compCfgsIt = compCfgs.iterator();
    
    List<JComponent> linkedLabels = new ArrayList();
    JComponent label, comp;
    JLabel l;
    Region compCfg; RegionDataField dfCfg;
    boolean nested = false;
    boolean hasLabelOnly = false;
    while (labelsIt.hasNext()) {
      label= labelsIt.next();
      comp = compsIt.next();
      compCfg = compCfgsIt.next();
      if (!View.isContainer(comp)) {
        // label is a data field label
        
        // ignore if label is to be displayed on its own (b/c it may be a section label whose 
        // width spans the entire form)
//        if (compCfg instanceof RegionLinking) {
//          System.out.println();
//        }
        
        dfCfg = (RegionDataField) compCfg;
        if (dfCfg.getLabelOnly()) {
          // this is a label-only component: exclude from the data components that need to link size
          if (!hasLabelOnly) hasLabelOnly = true;
          continue;
        }
        
        linkedLabels.add(label);
        lblWidth = label.getPreferredSize().getWidth();
        if (maxWidth == 0 || maxWidth < lblWidth) {
          maxWidth = lblWidth;
        }
      } else {
        // TODO: should we also process the labels of sub-container ?
        // force text to display before icon (in case label texts of containers donot have same width and 
        // thus appear unaligned)
        if (label instanceof JLabel) {
          l = (JLabel)label;
          if (l.getHorizontalTextPosition() != SwingConstants.TRAILING) {
            l.setHorizontalTextPosition(SwingConstants.TRAILING);
          }
        }
        
        nested = true;
      }
    }

    // only proceed with link size if either (1) nested: sub-containers are found or (2) there were section labels
    // do not need to link size other cases because the layout builder will take care of this  
    if (nested || hasLabelOnly) {
      Dimension lblSz;
      for (JComponent lbl : linkedLabels) {
        lblSz = lbl.getPreferredSize();
        if (lblSz.getWidth() < maxWidth) {
          lblHeight = lblSz.getHeight();
          //lblSz.setSize(maxWidth, lblHeight);
          /*v3.1: update size directly instead of creating a new Dimension object
           * this helps fix a bug in that the label is not resized to fit a new text that is set on it 
          lbl.setPreferredSize(new Dimension((int)maxWidth, (int)lblHeight));
           */
          lblSz.setSize(maxWidth, lblHeight);
          
          // force alignment to right-aligned (so that text is aligned next to comp)
          if (lbl instanceof JLabel) {
            ((JLabel) lbl).setHorizontalAlignment(SwingConstants.TRAILING);
          }
        }
      }
    }    
  }
}
