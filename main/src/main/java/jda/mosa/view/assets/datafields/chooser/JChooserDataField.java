package jda.mosa.view.assets.datafields.chooser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JDataField;

/**
 * @overview An abstract, single-value, constrained, unbounded, non-editable, sub-type of {@link JDataField} that provides a 
 *           chooser button for users to select a value from a panel (<b>chooser panel</b>) 
 *           of allowed values. 
 *           
 *           <p>User may also enter a value directly on to the text field. 
 * @author dmle
 * 
 * @version 
 * -3.2: improved to work correctly with JObjectTable and to support custom display label
 */
public abstract class JChooserDataField<C> extends JDataField implements ActionListener {
  
  // v2.7.3
  public static enum ChooserAction {
    Choose,
    Delete
  }

  // constructor method
  /**
   * @effects initialises this to be a text field whose values are constrained by <tt>dc</tt> 
   */
  public JChooserDataField(DataValidator validator, 
      Configuration config, Object val, 
      DAttr dc, Boolean editable, Boolean autoValidation) {
    super(validator,config, val,dc,
        false,    // editable: IMPORTANT
        false     // auto-validate
        );
  }
  
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    /*
     * v2.7.3: 
     * - support a button sub-panel with 2 buttons: choose and delete
     * - use grid bag layout
    */
    /**
     * the picture frame contains two components: (1) a picture box to display
     * the image and (2) the original display component of this field
     **/
    GridBagLayout layout = new GridBagLayout();

    //JPanel panel = new JPanel();
    JPanel chooserPanel = new JPanel(layout);
    
    // v3.2: make transparent (so that background colour can be applied)
    chooserPanel.setOpaque(false);
    
    GridBagConstraints c = new GridBagConstraints();

    // the display component  
    // v5.1c: display = createTextFieldWithDimAsLength();
    JTextField display = createTextFieldWithDimAsLength();
    setGUIComponent(display);
    
    // v2.7.2: add listener etc.
    setUpTextField();
    setUpListener(tfh);
    
    // the display component
    // v2.7.2: get display size from the field config
    // RegionDataField df = getDataFieldConfiguration();

    // add components: text field first then the button panel
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0; // no extra horiz. space 
    c.weighty = 0; // no extra vert space
    c.ipadx = 5;
    c.gridx = 0;
    c.gridy = 0;
    chooserPanel.add(display, c);
    
    /*
     * the buttons
     */
    c.gridx += 1;
    createChooserButton(ChooserAction.Choose, chooserPanel, c);

    c.gridx += 1;
    createChooserButton(ChooserAction.Delete, chooserPanel, c);

    return chooserPanel;
  }
  
  protected void createChooserButton(ChooserAction act, JPanel chooserPanel, Object c) {
    JButton btn = null;
    if (act == ChooserAction.Choose) {
      btn = createChooseButton();
    } else if (act == ChooserAction.Delete){
      btn = createDeleteButton();
    }
    
    chooserPanel.add(btn, c);
  }
  
  /**
   * @effects 
   *  create and return a {@link JTextField} whose length is set to {@link JDataField#DEFAULT_TEXT_WIDTH}
   *  
   * @version 3.2
   */
  protected javax.swing.JTextField createTextField() {
    /*
     * the text field (used as the display component)
     */
    /* v3.2: support configured dimension
    //TODO: get view's configuration field width here
    // should not invoke -> getDomainFieldWidth()
    int length = 25;
    */
    // default length if no length is specified
    int length = DEFAULT_TEXT_WIDTH;
    
    javax.swing.JTextField tf = new javax.swing.JTextField(length);
    
    /* v3.2: seems not needed
    if (value != null)
      tf.setText(value.toString());
     */

    // disable the text field so that it cannot be edited directly
    // user must use the choose button (below)
    tf.setEditable(// v5.1c: //editable
        getEditable());

    /* v3.2: support alignment
     tf.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
     */
    int align = getAlignX();
    tf.setHorizontalAlignment(align);
    
    return tf;
  }
  
  /**
   * @effects 
   *  create and return a {@link JTextField} whose length is determined by 
   *  both the domain field length (of {@link #getDomainConstraint()}) and the configured
   *  width (if specified)
   *  
   * @version 3.2
   */
  protected javax.swing.JTextField createTextFieldWithDimAsLength() {
    /*
     * the text field (used as the display component)
     */
    /* v3.2: support configured dimension
    //TODO: get view's configuration field width here
    // should not invoke -> getDomainFieldWidth()
    int length = 25;
    */
    // default length if no length is specified
    int length = getDomainFieldWidth();
    
    // v3.0: support configured dimension (override length above)
    // v3.2: Integer width = null, height = null;
    Dimension configDim = getConfiguredDimension();
    
    if (configDim != null) {
      
      /*v3.2:
      // configured width 
      width = (int) configDim.getWidth(); 
      // configured height
      height = (int) configDim.getHeight();*/
      
      length = (int) configDim.getWidth();
    }

    javax.swing.JTextField tf = new javax.swing.JTextField(length);
    
    /* v3.2
    // text field
    javax.swing.JTextField tf = new javax.swing.JTextField(length);

    if (width != null)
      tf.setPreferredSize(new Dimension(width, tf.getPreferredSize().height));
    */ 
    
    /* v3.2: seems not needed
    if (value != null)
      tf.setText(value.toString());
     */

    // disable the text field so that it cannot be edited directly
    // user must use the choose button (below)
    tf.setEditable(// v5.1c: //editable
        getEditable());

    /* v3.2: support alignment
     tf.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
     */
    int align = getAlignX();
    tf.setHorizontalAlignment(align);
    
    return tf;
  }
  
  /**
   * @requires 
   *  this.display is a swing text field
   */
  protected void setUpTextField() {
    setEditable(// v5.1c: //editable
        getEditable());

    // border color the id field 
    if (isId()) {
      Color BORDER_COLOR = new Color(0, 100, 255);
      // display.setBackground(new Color(120,120,120));
      JComponent display = getGUIComponent(); // v5.1c:

      display.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(BORDER_COLOR, 2),
          BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }
  }

  /**
   * @requires 
   *  this.display is a swing text field
   */
  protected void setUpListener(DataFieldInputHelper tfh) {
    JComponent display = getGUIComponent(); // v5.1c:

    display.addKeyListener(tfh);
    display.addFocusListener(tfh);      
  }

  protected JButton createChooseButton() {
    /* v2.7.3: moved to a separate method
    JButton chooseBtn = new JButton();

    // use image icon if set
    ImageIcon icon = null;
    try {
      icon = getChooserIcon();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    
    if (icon != null) {
      chooseBtn.setIcon(icon);
    } else {
      chooseBtn.setText("...");
    }
    
    // sets up border
    Dimension dim = new Dimension(20, 20);
    chooseBtn.setPreferredSize(dim);
    chooseBtn.setSize(dim);
    chooseBtn.setBorderPainted(false); // remove default border
    chooseBtn.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    chooseBtn.setBackground(Color.YELLOW);
    
    // adds this as listener
    chooseBtn.addActionListener(this);
    
    return chooseBtn;
    */
    // use image icon if set
    ImageIcon icon = null;
    try {
      icon = getChooserIcon();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    JButton chooseBtn = createButton(null, icon, ChooserAction.Choose);
    return chooseBtn;
  }
  
  protected JButton createDeleteButton() {
    // use image icon if set
    ImageIcon icon = null;
    try {
      icon = getDeleterIcon();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }

    JButton btn = createButton(null, icon, ChooserAction.Delete);
    return btn;
  }
  
  protected JButton createButton(String label, ImageIcon icon, ChooserAction act) {
    JButton btn = new JButton();

    if (icon != null) {
      btn.setIcon(icon);
    } else if (label == null) {
      btn.setText("...");
    }
    
    // sets up border
    Dimension dim = new Dimension(20, 20);
    btn.setPreferredSize(dim);
    btn.setSize(dim);
    btn.setBorderPainted(false); // remove default border
    //btn.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
    btn.setBackground(Color.YELLOW);
    
    // adds this as listener
    btn.addActionListener(this);
    
    btn.setActionCommand(act.name());
    
    return btn;
  }
  
  @Override
  public Object getValue() throws ConstraintViolationException {
    Object v;
    if (!//v5.1c: validated
        isValidated()
        ) {
      JComponent display = getGUIComponent(); // v5.1c:

      v = ((JTextComponent) display).getText();

      // empty strings are treated as null
      if (v.equals(Nil))
        v = null;

      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      setValueDirectly(validateValue(v, dconstraint)); // v5.1c: value = validateValue(v, dconstraint);
    } 

    return getValueDirectly(); //v5.1c: value;
  }
  
  @Override
  public void setValue(Object val) {
    /*v3.2: use methods
    value = val;
    validated = true;
    */
    setValueDirectly(val);
    setIsValidated(true);
    
    JComponent display = getGUIComponent();
    
    if (val != null) {
      /*v3.2: use method to get value's label
      ((JTextComponent) display).setText(value.toString());
      */
      ((JTextComponent) display).setText(getValueLabel(getValueDirectly()));
    } else {
      //reset();
      ((JTextComponent) display).setText(null);
    }
    
    //need to reset the indicator here
    updateGUI(false);
  }

  /**
   * @effects 
   *  return true so that we can use {@link #getFormattedValue(Object)} 
   *  to return the string for display
   * @version 3.2
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JDataField#isSupportValueFormatting()
   */
  @Override
  public boolean isSupportValueFormatting() {
    return true;
  }

  /**
   * @version 3.2
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JDataField#getFormattedValue(java.lang.Object)
   */
  @Override
  public String getFormattedValue(Object val) throws NotPossibleException {
    if (val == null) {
      throw new NotPossibleException(NotPossibleException.Code.CANNOT_FORMAT_VALUE, new Object[] {this, val, null});
    }
    
    return getValueLabel(val);
  }

  /**
   * @requires 
   *  value != null
   * @effects 
   *  return <tt>String</tt> representation of <tt>this.value</tt> suitable for display to user
   * @version 3.2 
   */
  protected String getValueLabel(Object value) {
    return value.toString();
  }
  
  /**
   * @effects if the chooser image icon exists then returns <tt>ImageIcon</tt> from it
   *          else throws <tt>NotFoundException</tt> 
   */
  protected ImageIcon getChooserIcon() throws NotFoundException {
    ImageIcon icon = GUIToolkit.getImageIcon("open.gif", "choose");
    return icon;
  }
  
  /**
   * @effects if the deleter image icon exists then returns <tt>ImageIcon</tt> from it
   *          else throws <tt>NotFoundException</tt> 
   */
  protected ImageIcon getDeleterIcon() throws NotFoundException {
    ImageIcon icon = GUIToolkit.getImageIcon("delete.gif", "delete");
    return icon;
  }
  
  @Override // ActionListener
  public void actionPerformed(ActionEvent e) {
    // common handler for actions performed on the chooser buttons.
    // Sub-types can override the sub-functions to provide their own handling
    String cmd = e.getActionCommand();
    if (cmd.equals(ChooserAction.Choose.name())) {
      chooseActionPerformed();
    } else if (cmd.equals(ChooserAction.Delete.name())) {
      deleteActionPerformed();
    }
    // no other actions possible
  }

  /**
   * @effects 
   *  set this.value = null
   *  update the display accordingly
   */
  protected void deleteActionPerformed() {
    if (getValueDirectly() != null) {
      setValidatedValue(null);
      
      //fireStateChanged();
      fireValueChanged();
    }
  }

  // sub-types must implement this
  protected abstract void chooseActionPerformed();

  @Override
  public void reset() {
    // resets both value and the actual value that is displayed
    // on the display component
    setValueDirectly((C) getInitValue());  // v5.1c: value = getInitValue(); 
    
    // validation is false
    //validated = false;
    setIsValidated(false);

    JComponent display = getGUIComponent(); // v5.1c:
    Object value = getValueDirectly(); // v5.1c
    
    if (value != null)
      ((JTextComponent) display).setText(value.toString());
    else
      ((JTextComponent) display).setText(null);
  }
  
  @Override
  public void clear() {
    setValueDirectly(null); // v5.1c: value = null;
    
    nullify();
    
    setIsValidated(true);
  }

  protected void nullify() {
    JComponent display = getGUIComponent(); // v5.1c:

    ((JTextComponent) display).setText(null);
  }
}
