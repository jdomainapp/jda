package jda.mosa.view.assets.datafields;

import javax.swing.JComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.swing.JHtmlLabel;

/**
 * @overview
 *   A sub-type of {@link JLabelField} that can display long text in multiple lines, provided that 
 *   the text is marked with suitable HTML line break mark-up (<tt>&lt;br&gt;</tt>). 
 *     
 * @author dmle
 * 
 * @version 2.7.4
 */
public class JHtmlLabelField<C> extends JLabelField {

  // bounded
  public JHtmlLabelField(DataValidator validator, Configuration config,
      Object val, JDataSource dataSource, DAttr dconstraint,
      DAttr boundConstraint, Boolean editable, Boolean autoValidation)
      throws ConstraintViolationException {
    super(validator, config, val, dataSource, dconstraint, boundConstraint,
        editable, autoValidation);
  }

  // unbounded
  public JHtmlLabelField(DataValidator validator, Configuration config,
      Object val, DAttr dconstraint, Boolean editable,
      Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dconstraint, editable, autoValidation);
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // use a JLabel to display the image
    JHtmlLabel label = new JHtmlLabel();
    
    int alignX = getAlignX();
    
    label.setHorizontalAlignment(alignX);
    
    String dispVal;
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      dispVal = (String) getDisplayValue(value);
      label.setText(dispVal);
    }

    // v5.1c: display = label; return display;
    setGUIComponent(label);
    
    return label;
  }
  
  
}
