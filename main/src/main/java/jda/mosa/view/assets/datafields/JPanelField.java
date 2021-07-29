package jda.mosa.view.assets.datafields;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;

/**
 * @overview
 *  A {@link JDataField} whose display component is a {@link JPanel} that
 *  displays its value directly as a GUI component in this panel.  
 *  
 *  <p>Specifically, method {@link #setValue(Object)} takes a {@link Component} object as input and adds 
 *  this component to the panel.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class JPanelField<C> extends JDataField<C> {

  public JPanelField(DataValidator validator, Configuration config,
      C val, 
      DAttr dconstraint,
      Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dconstraint, editable, autoValidation);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // display component is a panel to contain value object
    /* v5.1c: 
    display = new JPanel();
    
    display.setBorder(BorderFactory.createEtchedBorder());
    
    return display;
    */
    JPanel p = new JPanel();
    
    p.setBorder(BorderFactory.createEtchedBorder());
    
    setGUIComponent(p);
    
    return p;
    
  }

  @Override
  public Object getValue() throws ConstraintViolationException {
    Object value = getValueDirectly(); // v5.1c
    return value;
  }

  @Override
  public void setValue(Object val) throws ConstraintViolationException {
    
    if (!(val instanceof Component)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Giá trị dữ liệu không đúng: {0}, cần kiểu {1}", val, Component.class);
    }
    
    JComponent display = getGUIComponent(); // v5.1c:

    display.removeAll();
    
    display.add((Component) val);
    
    display.repaint();
  }
  
  @Override
  public void reset() {
    // TODO: reset the display to its initial state
    // means to draw the initial value state
  }
  
  public void clear() {
    //TODO
  }
}
