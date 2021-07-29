package jda.mosa.view.assets.datafields;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;

/**
 * @overview
 *  A sub-class of <tt>JTextField</tt> used for entering <tt>Integer</tt>-type data using 
 *  two convenient buttons +/- that are displayed next to the text field.  
 *  
 * @author dmle
 */
public class JCounterField<C> extends JTextField implements ActionListener {

  private static final String PLUS = "+";
  private static final String MINUS = "\u2212";
  
  private JButton butPlus;
  private JButton butMinus;
  
  public JCounterField(DataValidator validator, Configuration config, C val, 
      DAttr dconstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    // always editable
    super(validator, config, val, dconstraint, editable, autoValidation);
    
    // only supports Integer-type
    if (dconstraint != null &&    // v2.7.4: added this check 
        !dconstraint.type().isInteger()) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_DATA_TYPE, 
          "Kiểu dữ liệu không đúng {0} (cần kiểu Integer)", dconstraint.type());
    }
    
    /*v2.7.4: support null value
    if (value == null) {
      double v = dconstraint.min(); 
      if (v == Double.NaN)
        v = 0;
      
      value = (int) v;
    }
    */
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // the display component consists of a panel containing the parent's text field
    // and two counter buttons +/-
    
    // create the text field first
    createTextFieldComponent(tfh); //v3.0: super.createDisplayComponent(tfh);
    
    // v2.7.4: use gaps = 0 to squeeze components together 

    // create a panel containing the text field and the buttons
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
    //v2.7.4: create a border so that this is aligned properly with the field's label
    panel.setBorder(
        null
        //BorderFactory.createLineBorder(Color.BLACK)
        );
    // debug
    //panel.setBackground(Color.LIGHT_GRAY);
    
    // add text field to panel
    JComponent display = getGUIComponent(); // v5.1c:

    panel.add(display);
    
    // create the counter buttons and add them to panel
    JPanel buttons = new JPanel(new GridLayout(2,0));
    buttons.setBorder(null);
    // debug
    buttons.setBackground(Color.LIGHT_GRAY);
    
    butPlus = new JButton(PLUS); // +
    butPlus.addActionListener(this);
    butPlus.setPreferredSize(new Dimension(15,12));
    butPlus.setBorder(BorderFactory.createEtchedBorder());
    Font f = new Font("Arial",Font.PLAIN,12);
    butPlus.setFont(f);
    butPlus.setVerticalAlignment(SwingConstants.CENTER);
    
    butMinus = new JButton(MINUS);  // minus
    butMinus.addActionListener(this);
    butMinus.setPreferredSize(new Dimension(15,12));
    butMinus.setFont(f);
    butMinus.setBorder(BorderFactory.createEtchedBorder());
    butMinus.setVerticalAlignment(SwingConstants.CENTER);

    buttons.add(butPlus);
    buttons.add(butMinus);
    
    panel.add(buttons);
    
    // set up this field
    setUpTextField();
    setUpListener(tfh);

    return panel;
  }
  
  @Override
  public void setEditable(boolean state) {
    // must do this to set up the text field first
    super.setEditable(state);
    
    // then do this to set up the button
    if (!state) {
      // not-editable: disable buttons
      butPlus.setEnabled(false);
      butMinus.setEnabled(false);
    } else {  // v3.1: added this case
      // editable: enable buttons
      butPlus.setEnabled(true);
      butMinus.setEnabled(true);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // handle counter buttons
    String cmd = e.getActionCommand();
    if (cmd.equals(PLUS)) {
      plusOne();
    } else if (cmd.equals(MINUS)) {
      minusOne();
    }
  }

  /**
   * @modifies this
   * @effects 
   *  add one to <tt>value</tt>
   */
  private void plusOne() {
    int newVal; 
    Object value = getValueDirectly(); // v5.1c
    if (value == null) {
      newVal = 1;
    } else {
      newVal = ((Integer) value) +1;
    }

    if (//v5.1c: autoValidation
        isAutoValidation()
        ) {
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      setValueDirectly(validateValue(newVal, dconstraint)); // v5.1c: value = validateValue(newVal, dconstraint);
      
      setText(newVal);

      fireValueChanged();
      
    } else {
      // just display the value and set validated to false
      setText(newVal);
      if (//v5.1c: validated
          isValidated()
          )
        //v5.1c: validated = false;
        setIsValidated(false);
    }
  }

  /**
   * @modifies this
   * @effects 
   *  subtract one from <tt>value</tt>
   */
  private void minusOne() {
    /*v2.7.4:*/ 
//    int newVal = ((Integer) value) - 1;
//    
//    setValue(newVal);
//    
//    /*v2.7.4
//    fireStateChanged();
//    */
//    fireValueChanged();
    int newVal; 
    Object value = getValueDirectly(); // v5.1c
    
    if (value == null) {
      newVal = 0;
    } else {
      newVal = ((Integer) value) - 1;
    }

    if (//v5.1c: autoValidation
        isAutoValidation()
        ) {
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      setValueDirectly(validateValue(newVal, dconstraint)); // v5.1c: value = validateValue(newVal, dconstraint);

      setText(newVal);
      
      fireValueChanged();
      
    } else {
      // just display the value and set validated to false
      setText(newVal);
      if (//v5.1c: validated
          isValidated()
          )
        //v5.1c: validated = false;
        setIsValidated(false);
    }
  }

  /**
   * @effects 
   *  write <tt>val</tt> to the text field
   */
  private void setText(int val) {
    JTextComponent tf = getTextComponent();
    tf.setText(val+"");
  }
  
  
  @Override // v3.2c
  public boolean isMouseClickConsumableByValueChanged() {
    // refer to the mouse click on counter buttons are handled by this field 
    return true;
  }
}
