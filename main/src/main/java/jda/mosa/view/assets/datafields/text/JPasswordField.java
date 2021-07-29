package jda.mosa.view.assets.datafields.text;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.JTextField;

public class JPasswordField<C> extends JTextField {
  // bounded
  public JPasswordField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable, Boolean autoValidation) {
    super(validator, config, val , dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }

  // unbounded
  public JPasswordField(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable, Boolean autoValidation) {
    // password field is always editable
    this(validator, config, val, null, domainConstraint, null, true, true);
  }

  /**
   * @effects 
   *    initialise <tt>this.display</tt> as a <tt>JPasswordField</tt> with length <tt>length</tt> and 
   *    initial value <tt>val</tt>
   *    
   *  <br>Return <tt>this.display</tt>
   */
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    int length = getDomainFieldWidth();
    
    JComponent actualDisplay = null;
    
    // password field
    javax.swing.JPasswordField pf = new javax.swing.JPasswordField(length);
    //v3.0: pf.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

    // v2.6.4.b: left-justified
    pf.setHorizontalAlignment(
        // v3.0: SwingConstants.LEFT
        SwingConstants.CENTER
        );

    Object value = getValueDirectly(); // v5.1c
    if (value != null)
      pf.setText(value.toString());
    
    /* v5.1c: 
    display = pf;
    actualDisplay = display;     
    */
    setGUIComponent(pf);
    actualDisplay = pf;
    
  
    setUpTextField();
    
    setUpListener(tfh);

    return actualDisplay;
  }

  @Override
  public Object getValue() throws ConstraintViolationException {
    if (!//v5.1c: validated
        isValidated()
        ) {
      JComponent display = getGUIComponent(); // v5.1c:

      Object v = new String(((javax.swing.JPasswordField) display).getPassword());

      // empty strings are treated as null
      if (v.equals(Nil))
        v = null;

      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      setValueDirectly(validateValue(v, dconstraint)); // v5.1c: value = validateValue(v, dconstraint);
    } 

    return getValueDirectly(); // v5.1c: value;
  }
  
  @Override
  protected Object getRawTextValue() 
    throws ConstraintViolationException // v3.0
  {
    Object v = null;
    try {
      JComponent display = getGUIComponent(); // v5.1c:

      char[] pwd = ((javax.swing.JPasswordField) display).getPassword();
      if (pwd != null)
        v = new String(pwd);
    } catch (NullPointerException e) {
      // no pwd
      // password may be null
    }

    return v;
  }
}
