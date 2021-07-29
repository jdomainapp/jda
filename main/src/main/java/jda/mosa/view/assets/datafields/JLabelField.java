package jda.mosa.view.assets.datafields;

import javax.swing.JComponent;
import javax.swing.JLabel;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview 
 *  A sub-class of <tt>JBindableField</tt> used for displaying simple text which may 
 *  be bounded to a domain attribute.
 *  
 * @author dmle
 */
public class JLabelField<C> extends JBindableField {
//
//  /** to handle mouse events (typically mouse-click) on the image. 
//   * This attribute is set for an object only when change listeners are registered to 
//   * the object via the {@link #addChangeListener(ChangeListener) method.
//   */
//  private MouseListener mouseHandler;
//  
  /**
   * Use this for non-bounded label field
   */
  public JLabelField(DataValidator validator, Configuration config,  
      C val,
      DAttr dconstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    this(validator, config, val, null, dconstraint, null, editable, autoValidation);
  }
  
  /**
   * Use this for bounded label field.
   */
  public JLabelField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound  
      DAttr dconstraint, 
      DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    // always non-editable
    super(validator, config, val, dataSource, dconstraint, boundConstraint, false, autoValidation);
    
    //v5.1c: validated = true;
    setIsValidated(true);
    
    /*v2.7: moved to createDisplayComponent 
    String dispVal;
    if (val != null) {
      dispVal = (String) getDisplayValue(val);
      ((JLabel) display).setText(dispVal);
    }
    */
  }

  /**
   * Use this for bounded field.
   */
  public JLabelField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound  
      DAttr dconstraint, 
      DAttr boundConstraint, Boolean editable) throws ConstraintViolationException {
    // always non-editable
    super(validator, config, val, dataSource, dconstraint, boundConstraint, editable, 
        //autoValidation
        false
        );
    
    //v5.1c: validated = true;
    setIsValidated(true);
  }
  
//  /**
//   * This method is overriden to initialise <tt>mouseHandler</tt> that is 
//   * used to handle user's mouse event on the image. The event handling method 
//   * of this <tt>mouseHandler</tt> invokes the method {@link JBindableField#fireStateChanged()}
//   * when a mouse event occurs.  
//   * 
//   * @effects 
//   *  invoke <tt>super.addChangeListener(listener)</tt>
//   *  if <tt>mouseHandler</tt> is not initialised
//   *    initialise it with a <tt>mouseClicked</tt> event handling method  
//   *    register it to <tt>display</tt>
//   */
//  @Override
//  public void addChangeListener(ChangeListener listener) {
//    // register the listener first
//    super.addChangeListener(listener);
//    
//    // handle the mouse-click event on the image and consider that a state change event
//    if (mouseHandler == null) {
//      mouseHandler = new MouseAdapter() {
//        public void mouseClicked(MouseEvent e) {
//          fireStateChanged();
//        }
//      };
//      display.addMouseListener(mouseHandler);
//    }
//  }
  
  @Override
  protected void loadBoundedData() throws NotPossibleException {
    // TODO: implement this if a data source is used
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // use a JLabel to display the image
    JLabel label = new JLabel();
    
    int alignX = getAlignX();
    
//    if (alignX == SwingConstants.CENTER)
//      System.out.println(this+": align = center");
    
    label.setHorizontalAlignment(alignX);
    
    
    // create a standard empty border (so that it is aligned properly with 
    // the field's label)
    label.setBorder(GUIToolkit.LABEL_EMPTY_BORDER);
    String dispVal;
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      dispVal = (String) getDisplayValue(value);
      label.setText(dispVal);
    }

    /* v5.1c: 
    display = label;
    
    return display;
    */
    setGUIComponent(label);
    
    return label;
  }

  @Override
  protected void setDisplayValue(Object dispVal) {
    /*v2.7.2: 
    ((JLabel)display).setText(dispVal.toString());
     */
    JComponent display = getGUIComponent(); // v5.1c:

    JLabel label = (JLabel)display; 
    if (!dispVal.equals(Nil))
      label.setText(dispVal.toString());
    else
      label.setText(null);
    
    // v2.7.4: uncomment these if this field is bounded and editable
//  if (!validated) validated=true;
//  updateGUI(false);    
  }

  @Override
  public void reset() {
    /*v2.7.2: this is wrong, should use setDisplayValue above
    ((JLabel)display).setText(null);
     */
    super.reset();
    // IMPORTANT: overwrite the validated value set by super.reset
    //validated = true;
    setIsValidated(true);
  }
  
  @Override 
  public void deleteBoundedData() {
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null) {
      //TODO: implement this if data source is used
      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED,
          this.getClass().getSimpleName()+".clearBinding()");
    }
  }
}
