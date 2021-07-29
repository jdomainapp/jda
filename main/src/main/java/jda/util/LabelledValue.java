package jda.util;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represents a <b>non-serialisable</b> labelled value, e.g. ("1","một"), ("1","one"), etc. 
 *  
 * @author dmle
 */
@DClass(serialisable=false)
public class LabelledValue {
  @DAttr(name="value",type=Type.String,length=15,optional=false,mutable=false)
  private String value;
  
  @DAttr(name="label",type=Type.String,optional=false,mutable=false)
  private String label;

  protected LabelledValue() {
    // only used by sub-types
  }
  
  public LabelledValue(String value, String val) {
    super();
    this.value = value;
    this.label = val;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"(" + value + "," + label + ")";
  }
}
