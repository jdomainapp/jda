/**
 * @overview
 *
 * @author dmle
 */
package jda.util.properties.adapter;

import java.lang.annotation.Annotation;

import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;

/**
 * @overview
 *  An adapter for {@link PropertyDesc} that makes it easier for creating anonymous sub-types. 
 *  
 * @author dmle
 * @version 3.2
 */
public abstract class PropertyDescAdapter implements PropertyDesc {

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return PropertyDesc.class;
  }

  /**
   * @effects
   */
  /* (non-Javadoc)
   * @see domainapp.basics.model.util.properties.PropertyDesc#valueIsClass()
   */
  @Override
  public Class valueIsClass() {
    return Null.class;
  }
}
