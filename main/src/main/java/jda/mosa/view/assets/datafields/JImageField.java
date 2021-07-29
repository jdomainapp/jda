package jda.mosa.view.assets.datafields;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeListener;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;

/**
 * @overview 
 *  A sub-class of <tt>JBindableField</tt> used for displaying image-type data. The image value can 
 *  be bound to the attribute of a domain object. 
 * @author dmle
 */
public class JImageField<C> extends JBindableField {

  /** to handle mouse events (typically mouse-click) on the image. 
   * This attribute is set for an object only when change listeners are registered to 
   * the object via the {@link #addChangeListener(ChangeListener)} method.
   */
  private MouseListener mouseHandler;
  
  /**
   * Use this for non-bounded image field
   */
  public JImageField(DataValidator validator, Configuration config,  
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
  public JImageField(DataValidator validator, Configuration config, 
      C val,
      JDataSource dataSource, // the data source to which this field is bound  
      DAttr dconstraint, 
      DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    // always non-editable
    super(validator, config, val, dataSource, dconstraint, boundConstraint, false, autoValidation);
    
    //v5.1c: validated = true;
    setIsValidated(true);
    
    /*v2.7: moved to createDisplayComponent 
//    ImageIcon img;
//    if (val != null) {
//      if (boundConstraint != null) {
//        img = (ImageIcon) getDisplayValue(val);
//      } else {
//        if (!(val instanceof ImageIcon))
//          throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
//              "Giá trị không đúng {0}, cần kiểu ImageIcon", val);
//        
//        img = (ImageIcon) val;
//      }
//      ((JLabel) display).setIcon(img);
//    }
    ImageIcon img;
    if (value != null) {
      // unbounded
      if (boundConstraint == null) {
        if (!(value instanceof ImageIcon))
          throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
              "Giá trị không đúng {0}, cần kiểu ImageIcon", value);
        
        img = (ImageIcon) value;
        ((JLabel) display).setIcon(img);
      }
    }
    */
  }

  @Override
  protected void loadBoundedData() throws NotPossibleException {
  //v2.7.4: moved up 
    //if (dataSource != null)
    //  dataSource.connect();
    
    ImageIcon img;
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      JComponent display = getGUIComponent(); // v5.1c:

      img = (ImageIcon) getDisplayValue(value);
      ((JLabel) display).setIcon(img);
    }
  }

  /**
   * This method is overriden to initialise <tt>mouseHandler</tt> that is 
   * used to handle user's mouse event on the image. The event handling method 
   * of this <tt>mouseHandler</tt> invokes the method {@link JBindableField#fireStateChanged()}
   * when a mouse event occurs.  
   * 
   * @effects 
   *  invoke <tt>super.addChangeListener(listener)</tt>
   *  if <tt>mouseHandler</tt> is not initialised
   *    initialise it with a <tt>mouseClicked</tt> event handling method  
   *    register it to <tt>display</tt>
   */
  @Override
  public void addChangeListener(ChangeListener listener) {
    // register the listener first
    super.addChangeListener(listener);
    
    // handle the mouse-click event on the image and consider that a state change event
    if (mouseHandler == null) {
      JComponent display = getGUIComponent(); // v5.1c:

      mouseHandler = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          // v3.2: fireStateChanged();
          fireValueChanged();
        }
      };
      display.addMouseListener(mouseHandler);
    }
  }
  
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // use a JLabel to display the image
    // v5.1c: display = new JLabel();
    JLabel display = new JLabel();
    
    ImageIcon img;
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      // unbounded
      DAttr boundConstraint = getBoundConstraint(); // v5.1c:
      
      if (boundConstraint == null) {
        if (!(value instanceof ImageIcon))
          throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
              "Giá trị không đúng {0}, cần kiểu ImageIcon", value);
        
        img = (ImageIcon) value;
        display.setIcon(img);
      }
    }
    
    setGUIComponent(display); // v5.1c: 
    
    return display;
  }

  @Override
  protected void setDisplayValue(Object dispVal) {
    /*v2.7.2: consider the case dispVal = Nil
    ((JLabel)display).setIcon((ImageIcon) dispVal);
    */
    JComponent display = getGUIComponent(); // v5.1c:

    JLabel label = (JLabel)display;
    if (!dispVal.equals(Nil)) {
      label.setIcon((ImageIcon) dispVal);
    } else {
      label.setIcon(null);
    }
    
    // v2.7.4: uncomment these if this field is bounded and editable
//  if (!validated) validated=true;
//  updateGUI(false);
  }

  @Override
  public void reset() {
    /*v2.7.2: this is wrong, should use setDisplayValue above
    // do nothing
     */
    super.reset();
    
    // IMPORTANT: overwrite the validated value set by super.reset
    //validated = true;
    setIsValidated(true);
  }
  
  @Override 
  public void deleteBoundedData() {
    JComponent display = getGUIComponent(); // v5.1c:

    ((JLabel) display).setIcon(null);    
  }
}
