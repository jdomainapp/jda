/**
 * @overview
 *
 * @author dmle
 */
package jda.util.properties.adapter;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyName;

/**
 * @overview
 *  A {@link PropertyDescAdapter} that is specifically for creating anonymous sub-types whose 
 *  values are {@link Class} objects.
 *  
 * @author dmle
 * @version 3.2
 */
public class ClassValuedPropertyDescAdapter extends PropertyDescAdapter {
  
  private Class valueIsClass;
  private PropertyName name;

  /**
   * @effects 
   *  create a new object
   */
  public ClassValuedPropertyDescAdapter(PropertyName name, Class valueIsClass) {
    this.name = name;
    this.valueIsClass = valueIsClass;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.util.properties.PropertyDesc#name()
   */
  @Override
  public PropertyName name() {
    return name;
  }
  
  @Override
  public Class valueType() {
    return Class.class;
  }
  
  @Override
  public String valueAsString() {
    return CommonConstants.NullValue;
  }
  
  @Override
  public Class valueIsClass() {
    if (valueIsClass != null)
      return valueIsClass;
    else 
      return Null.class;
  }
}
