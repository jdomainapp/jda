package jda.mosa.view.assets.datafields;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.util.events.InputHandler;

/**
 * @overview
 *  A {@link JBindableField} that has a label for displaying the content of an {@link Object} and enables the user
 *  to interact with the label via the mouse. The event-handling methods can be customised through overriding.  
 *  
 *  <p>The {@link Object} can be the value of a bounded attribute.
 *  
 * @author dmle
 * @version 3.2
 */
public abstract class JObjectLabelField<C> extends JBindableField {

  private static final Border Default_Label_Border = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true);
  
  /** to handle mouse events (typically mouse-click) on the label. 
   * This attribute is set for an object only when change listeners are registered to 
   * the object via the {@link #addChangeListener(ChangeListener) method.
   */
  private MouseListener mouseHandler;
  
  /**
   * Use this for non-bounded image field
   */
  public JObjectLabelField(DataValidator validator, Configuration config,  
      C val,
      DAttr dconstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    this(validator, config, val, 
        null,   // no data source 
        dconstraint, 
        null,   // no data source
        editable, autoValidation);
  }
  
  /**
   * Use this for bounded image field.
   */
  public JObjectLabelField(DataValidator validator, Configuration config, 
      C val,
      JDataSource dataSource, // the data source to which this field is bound  
      DAttr dconstraint, 
      DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    // always non-editable
    super(validator, config, val, dataSource, dconstraint, boundConstraint, false, autoValidation);
    
    setIsValidated(true);
  }

  @Override
  protected void loadBoundedData() throws NotPossibleException {
    if (getValueDirectly() != null) {
      String labelText = getObjectLabel();
      ((JLabel) getGUIComponent()).setText(labelText);
    }
  }

  
  @Override 
  public void deleteBoundedData() {
    nullifyLabel();//((JLabel) display).setText(null);
  }

  /**
   * @effects 
   *  nullify the label's text and other related settings (icon) if needed
   */
  protected void nullifyLabel() {
    JLabel label = (JLabel) getGUIComponent(); 
    label.setText(null);
    label.setIcon(null);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // use a JLabel to display the image
    JLabel display = new JLabel();

    C value = (C) getValueDirectly();
    if (value != null) {
      // unbounded
      if (!isBounded()) {
        
        validateObjectType();
        
        String labelText = getObjectLabel();
        display.setText(labelText);
      }
      //TODO: what about bounded case?
    }

    setUpDisplayComponent(display);

    // create mouse handler for handling interaction on label
    if (mouseHandler == null) {
      mouseHandler = new InputHandler() {//new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          handleMouseClick(null);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          handleMouseEntered(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
          handleMouseExited(e);
        }
        
        // TODO: add other event handling methods here if needed
      };
      display.addMouseListener(mouseHandler);
    }

    setGUIComponent(display);
    
    return display;
  }

  /**
   * @effects 
   *  set up the label's GUI depending on the value that it displays
   */
  protected void setUpDisplayComponent(JComponent display) {
    ImageIcon icon = getImageIcon();
    
    if (icon != null) {
      JLabel label = (JLabel) display;
      label.setHorizontalTextPosition(SwingConstants.LEADING);
      label.setIcon(icon);
    }
    
    //boolean hasValue = (getValueDirectly() != null);
    display.setBorder(Default_Label_Border);
  }

  /**
   * @effects 
   *  if exists {@link ImageIcon} specified for the label
   *    return it
   *  else
   *    return null
   */
  protected ImageIcon getImageIcon() {
    return null;
  }

  /**
   * @requires 
   *  value != null
   * @effects 
   *  return <tt>String</tt> representation of <tt>this.value</tt> suitable for display on the label 
   */
  protected abstract String getObjectLabel();

  /**
   * @effects 
   *  if <tt>value</tt> is not of correct type
   *    throws ConstraintViolationException
   *  else
   *    do nothing
   */
  protected abstract void validateObjectType() throws ConstraintViolationException;

  @Override
  protected void setDisplayValue(Object dispVal) {
    JLabel label = (JLabel)getGUIComponent();
    if (!dispVal.equals(Nil)) {
      String labelText = getObjectLabel();
      label.setText(labelText);
    } else {
      nullifyLabel(); //label.setIcon(null);
    }
  }

  @Override
  public void reset() {
    super.reset();
    
    // IMPORTANT: overwrite the validated value set by super.reset
    //validated = true;
    setIsValidated(true);
  }
    
  /**
   * @effects 
   *  Handle the mouse click event on the label of this
   */
  protected abstract void handleMouseClick(MouseEvent e);
  
  /**
   * @effects 
   *  Handle the mouse moved event on the label of this
   * @param e TODO
   */
  protected abstract void handleMouseEntered(MouseEvent e);

  /**
   * @effects 
   *  Handle the mouse exited event on the label of this
   */
  protected abstract void handleMouseExited(MouseEvent e);

}
