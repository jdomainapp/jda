package jda.mosa.view.assets.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.mosa.view.View;

/**
 * @overview
 *  A title panel that consists of three components: left, centre, right. 
 *  The implementation of this class only supports the centre component, which is 
 *  a <tt>JLabel</tt> that carries the title text. Sub-classes may define left and right 
 *  components by overriding the two methods {@link #getLeftComponent()} and {@link #getRightComponent()}
 *  respectively.
 *  
 * @author dmle
 *
 */
public class TitlePanel extends JPanel {
  
  private JLabel titleLabel;
  private View parentGUI;
  
  private Map<ComponentIndex, JComponent> compMap;
  
  /***
   * the location of the component
   * @author dmle
   */
  public static enum ComponentIndex {
    Left,
    Centre,
    Right
  }
  
  public TitlePanel(View parentGUI, JLabel titleLabel) {
    super();
    
    this.parentGUI = parentGUI;
    this.titleLabel = titleLabel;
   
    compMap = new HashMap();
    
    initLayout();
    initGUIComponents();
  }
  
  protected void initLayout() {
    // v2.7.4: added bg
    this.setBackground(Color.WHITE);

    int bordersGap = 10;
 
    Border border =
        // simple empty border
//        BorderFactory.createEmptyBorder(
//            5,
//            5,
//            5,
//            5);
        // compound border
      BorderFactory.createCompoundBorder( 
        BorderFactory.createRaisedBevelBorder(),
        BorderFactory.createEmptyBorder(bordersGap,bordersGap,bordersGap,bordersGap)
       )
//        // raised border
//        BorderFactory.createRaisedBevelBorder();
       ;
    
    this.setBorder(border);
    
    //default: use grid bag layout
    setLayout(new GridBagLayout());
  }
  
  protected void initGUIComponents() {
    JComponent left = getLeftComponent();
    
    if (left != null) {
      compMap.put(ComponentIndex.Left, left);
      setComponent(left, ComponentIndex.Left);
    }

    // centre component: title text
    compMap.put(ComponentIndex.Centre, titleLabel);
    setComponent(titleLabel, ComponentIndex.Centre);
    
    // right: ...
    JComponent right = getRightComponent();
    if (right != null) {
      compMap.put(ComponentIndex.Right, right);
      setComponent(right, ComponentIndex.Right);
    }
    
  }
  
  /**
   * @requires
   *  this.layout is a GridBagLayout
   * @effect 
   *  configure <tt>label</tt> with the specified grid bag constraints and dimension
   *  and add it to this
   *  if separator = true
   *    add a separator to this 
   */
  private void addComponent(JComponent comp, 
      int weightx, int weighty, int fill, int gridx, int gridy, 
      Dimension preferredWidth, 
      boolean separator) {
    
    if (preferredWidth != null)
      comp.setPreferredSize(preferredWidth);
    
    GridBagConstraints c = new GridBagConstraints();    
    c.weightx = weightx;  
    c.weighty = weighty;  
    c.fill = fill; 
    c.gridx = gridx; 
    c.gridy = gridy;
    add(comp, c);
  
    // separator 
    if (separator) {
      c.fill=GridBagConstraints.VERTICAL;
      c.weightx=0;
      c.weighty=1;
      c.gridx=gridx+1;
      JSeparator sep = new JSeparator(JSeparator.VERTICAL);
      sep.setPreferredSize(new Dimension(5,preferredWidth.height));    
      add(sep, c);
    }
  }
  
  protected JComponent getLeftComponent() {
    /*v2.7.4: read form logo */
    //return null;
    
    // left component: logo
    View parentGUI = getParentGUI();
    RegionGui guiCfg = parentGUI.getGUIConfig();
    
    ImageIcon titleIcon = guiCfg.getTitleIconObject();
    JLabel left = null;
    if (titleIcon != null) {
      try {
        left = new JLabel(titleIcon);

        left.setHorizontalAlignment(JLabel.CENTER);
      } catch (NotFoundException e) {
        // not found
      }
    }
    
    return left;
  }
  
  protected JComponent getRightComponent() {
    return null;
  }
  
  protected void setComponent(JComponent comp, ComponentIndex index) {
    int weightX, weightY, gridX, gridY;
    int fill;
    
    if (index == ComponentIndex.Left || index == ComponentIndex.Right) {
      weightX = 0;
      weightY = 0;
      fill = GridBagConstraints.NONE;
    } else {
      weightX = 1;
      weightY = 0;
      fill = GridBagConstraints.HORIZONTAL;
    }

    gridX = index.ordinal();
    gridY = 0;
    boolean useSeparator = false;
    
    addComponent(comp,weightX, weightY, fill, gridX, gridY, null, useSeparator);
  }
  
  public JComponent getComponent(ComponentIndex index) {
    return compMap.get(index);
  }
  
  public JLabel getTitleLabel() {
    return titleLabel;
  }
  
  public View getParentGUI() {
    return parentGUI;
  }
}
