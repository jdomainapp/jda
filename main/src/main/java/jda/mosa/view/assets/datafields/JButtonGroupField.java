package jda.mosa.view.assets.datafields;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.mosa.controller.assets.helper.DataValidator;

/**
 * @overview
 *  Represents a button group, the values of whose items can be bound to the data field of 
 *  a set of domain objects.
 *  
 *  <p>A special feature of this class is that if the values are images then they are displayed
 *  directly as icons of the buttons in the group.
 * 
 * @author dmle
 */
public class JButtonGroupField<C> extends JBindableField implements ItemListener {

  //private List<C> values;

  private ButtonGroup group;
  
  private AbstractButton selected;
  
  public JButtonGroupField(DataValidator validator, 
      Configuration config,
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, 
      DAttr boundConstraint, 
      Boolean editable) 
          throws ConstraintViolationException {
    // always editable, auto-validation
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, true, true);
        
    // always validated
    //v5.1c: validated = true;
    setIsValidated(true);
  }
  
  @Override
  protected void loadBoundedData() throws NotPossibleException {
    
    //v2.7.4: moved up 
    //dataSource.connect();
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource.isEmpty())
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Nhóm nút bấm (JButtonGroupField) phải được khởi tạo bởi một danh sách giá trị");

    /**
     * - create a button group
     * - create a button for each bounded value in values
     * - add each button to the group and to the display component
     */
    if (group == null)
      group = new ButtonGroup();
    
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    
    List<C> displayValues = getDisplayValues(dconstraint); //values, dconstraint, boundConstraint);
    Object dispVal;
    Object value = getValueDirectly(); // v5.1c
    
    if (value != null && boundConstraint != null) {
      dispVal = getDisplayValue(value);
    } else {
      dispVal = value;
    }
    
    // create buttons
    // - if the values are images then use them as the button icons 
    JRadioButton button;
    boolean isImage = isImage();
    ImageIcon icon;
    String desc;
    for (Object v : displayValues) {
      
      if (v == null)  // ignore null values
        continue;
      
      button = new JRadioButton();
      if (isImage) {
        icon = (ImageIcon)v;
        button.setIcon(icon);
        desc = icon.getDescription();
        if (desc != null)
          button.setText(desc);  // use icon description as text
      } 
      // v2.7 
      else {
        button.setText(v.toString());
      }
      
      // v2.7: use style (if specified)
      Style style = getStyle();
      if (style != null)
        setStyle(button, style);
      
      // TODO: this is normally not necessary when this field is used
      // in a separate window. However, it is needed when this field
      // is used in the DefaultPanel of the DomainApp application generator
      // (there the border is not shown when button is clicked).
      button.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
      
      button.addItemListener(this);
      if (dispVal != null && v == dispVal) {
        button.setSelected(true);
        selected = button;
        button.setBorderPainted(true);
      }
      // add button to group
      group.add(button);
      // add button to display panel
      JComponent display = getGUIComponent(); // v5.1c:

      display.add(button);
      
      display.revalidate();      
    }
  }

  /**
   * @effects
   *  if <tt>dconstraint.type.isImage() = true \/ dconstraint.type.isImage()</tt>
   *    return true
   *  else 
   *    return false
   */
  private boolean isImage() {
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    return 
        (dconstraint != null && dconstraint.type().isImage()) || 
        (boundConstraint != null && boundConstraint.type().isImage());
  }
  
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    /*v2.7: support width and height 
    // create a panel to hold the button group 
    display = new JPanel(new GridLayout());
    */
    RegionDataField fieldCfg = getDataFieldConfiguration();
    JPanel buttonPanel;
    GridLayout layout;
    int cols = 0, rows = 0;
    if (fieldCfg != null) {
      Integer width = fieldCfg.getWidth();
      Integer height = fieldCfg.getHeight();
      if (height != null) rows = height;
      
      if (width != null) cols = width;
    } 

    if (rows == 0 && cols == 0) cols = 2;
    
    layout = new GridLayout(rows,cols);
    
    buttonPanel = new JPanel(layout);
    
    //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    
    /* v5.1c: 
    display = buttonPanel;
    return display;
    */
    setGUIComponent(buttonPanel);
    return buttonPanel;
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    AbstractButton button = (AbstractButton) e.getSource();
    
    int state = e.getStateChange();
    if (state == ItemEvent.SELECTED) {
      // show border
      selected = button;
      button.setBorderPainted(true);

      DAttr boundConstraint = getBoundConstraint(); // v5.1c:
      
      if (isImage()) {
        if (boundConstraint!= null) {
          JDataSource dataSource = getDataSource(); // v5.1c
        
          // v5.1c: value = dataSource.reverseLookUp(boundConstraint, selected.getIcon())); //lookUp(selected.getIcon());
          setValueDirectly(dataSource.reverseLookUp(boundConstraint, selected.getIcon()));
        } else
          //v5.1c: value = selected.getIcon();
          setValueDirectly(selected.getIcon());
      } else {
        if (boundConstraint!= null) {
          JDataSource dataSource = getDataSource(); // v5.1c
          
          // v5.1c: value = dataSource.reverseLookUp(boundConstraint, selected.getText());//lookUp(selected.getText());
          setValueDirectly(dataSource.reverseLookUp(boundConstraint, selected.getText()));
        } else
          //v5.1c: value = selected.getText();
          setValueDirectly(selected.getText());
      }
      
      //debug
      if (debug) {
        Object value = getValueDirectly(); // v5.1c

        System.out.printf("JButtonGroupField: selected %s%n", value);
      }
      
      // inform listeners of the state change
      //v3.2: fireStateChanged();
      fireValueChanged();
    } else if (state == ItemEvent.DESELECTED) {
      // border off
      button.setBorderPainted(false);
    }
  }
  
  @Override
  public void setDisplayValue(Object dispVal) {
    //TODO
    // implement this to turn on the button corresponding to the 
    // display value
    // v2.7.4
    if (!//v5.1c: validated
        isValidated()
        ) setIsValidated(true); //validated = true;
    updateGUI(false);
  }

//  /**
//   * @effects returns an object in <code>values</code> whose bounded attribute value
//   *          is equal to <code>v</code>
//   * @requires <code>values != null && </code> v is a valid object v in
//   *           <code>values</code>
//   * 
//   */
//  private C lookUp(Object v) {
//    //String attributeName = boundConstraint.name();
//    for (C o : values) {
//      Object bv = dataSource.getAttributeValue(o, boundConstraint);
//      if (bv == v) {
//        return o;
//      }
//    }
//
//    return null;
//  }

//  /**
//   * @effects returns a <code>List</code> of values of the bound attribute of
//   *          the domain objects in <code>values</code>.
//   *          <p>
//   *          If <code>values.size=0 || domainConstraint.optional=true</code>
//   *          then adds the empty string object <code>Nil</code> to be the first
//   *          element of the result list.
//   * 
//   */
//  private List getDisplayValues(List<C> values,
//      DomainConstraint domainConstraint, DomainConstraint boundConstraint) {
//    // make a new list
//    List boundValues = new ArrayList();
//
//    for (C o : values) {
//      if (boundConstraint != null) {
//        boundValues.add(dataSource.getAttributeValue(o, boundConstraint));
//      } else {
//        boundValues.add(o);
//      }
//    }
//
//    return boundValues;
//  }


  @Override
  public void reset() {
    setValueDirectly((C) getInitValue());  // v5.1c: value = getInitValue(); 
    
    //validated=true;
    setIsValidated(true);
    
    // clear selection
    if (group != null) {
      //TODO: set the initial selection based on value
      group.clearSelection();
      selected = null;
    } else {
      selected = null;
    }
  }
  
  @Override
  public void deleteBoundedData() {
    /* 
     * clear the data objects that have been loaded via the binding (without removing the binding)
     */
    if (group != null) {
      Enumeration<AbstractButton> buttons = group.getElements();
      AbstractButton b;
      while (buttons.hasMoreElements()) {
        b = buttons.nextElement();
        
        // remove the button from the group and the display
        group.remove(b);
      }

      JComponent display = getGUIComponent(); // v5.1c:

      display.removeAll();
      /*
      display.invalidate();      
      display.validate();
      */
      //display.revalidate();
      display.repaint();

      selected = null;
    }
  }

}
