package jda.mosa.view.assets.datafields.chooser;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview  A sub-type of {@link JChooserDataField} that opens a {@link JColorChooser} dialog to enables a user
 *            to choose a {@link Color}. The selected {@link Color} object is then used as the value for 
 *            this data field.
 * @author dmle
 */
public class JColorChooserField<C> extends JChooserDataField {
  public JColorChooserField(DataValidator validator, Configuration config, 
      Object val, DAttr dc, Boolean editable, Boolean autoValidation) 
  throws ConstraintViolationException {
    super(validator, config, val,dc, editable, autoValidation);
    
    if (!(val instanceof Color))
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, 
          "Giá trị dữ liệu không hợp lệ {0} (cần kiểu {1})", val, "Color");
    
    //v5.1c: validated=true;
    setIsValidated(true);

    /*v2.7: moved to createDisplayComponent
    if (val != null) {
      display.setBackground((Color) val);
    }
    */
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper dfh) {
    JComponent comp = super.createDisplayComponent(dfh);
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      JComponent display = getGUIComponent(); // v5.1c:

      // update bg color of the display
      display.setBackground((Color) value);
    }
    
    return comp;
  }
  
  @Override
  public void setValue(Object val) {
    super.setValue(val);
    
    JComponent display = getGUIComponent(); // v5.1c:

    // update bg color of the display
    Object value = getValueDirectly(); // v5.1c
    if (value != null) {
      // update bg color of the display
      display.setBackground((Color) value);
    } else {
      display.setBackground(null);
    }
  }
  
  @Override
  protected ImageIcon getChooserIcon() throws NotFoundException {
    return GUIToolkit.getImageIcon("colorchooser.gif", "color chooser");
  }
  
  @Override
  //v2.7.3: public void actionPerformed(ActionEvent e) {
  protected void chooseActionPerformed() {
    Color c = JColorChooser.showDialog(this, "Choose a color", null);
    if (c != null) {
      setValidatedValue(c);      
      
      // change the background color to the specified color
      //getDisplayComponent().setBackground(c);
      JComponent display = getGUIComponent(); // v5.1c:

      display.setBackground(c);
      
      // v2.7.2: fire state change event 
      fireStateChanged();
    }
  }
  
  public Object getValue() throws ConstraintViolationException {
    Object value = getValueDirectly(); // v5.1c
    if (value != null && value.equals(Nil)) {
      setValueDirectly(null); // v5.1c: value = null;
      value = null;
    }
    
    return value;
  }
  
  @Override
  public void reset() {
    super.reset();
    
    Color c = (Color) getValue();
    JComponent display = getGUIComponent(); // v5.1c:

    display.setBackground(c);
  }
  
  @Override
  protected void nullify() {
    super.nullify();
    JComponent display = getGUIComponent(); // v5.1c:

    display.setBackground(null);
  }
  
}
