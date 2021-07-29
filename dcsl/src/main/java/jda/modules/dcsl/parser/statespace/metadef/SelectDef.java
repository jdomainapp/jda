/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.Select;

/**
 * @overview 
 *  Represents the definition of {@link Select}
 *  
 * @author dmle
 *
 * @version 3.4 
 */
public class SelectDef extends MetaAttrDef implements Select {
  
  @Override
  public String toString() {
    return String.format("@Select(clazz=%s, attributes=%s)", 
        clazz(), Arrays.toString(attributes())
        );
  }
  
  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return Select.class;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.Select#clazz()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Class clazz() {
    return (Class) propValMap.getOrDefault("clazz", Null.class);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.Select#attributes()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String[] attributes() {
    return (String[]) propValMap.getOrDefault("attributes", CommonConstants.EmptyArray);
  }

}
