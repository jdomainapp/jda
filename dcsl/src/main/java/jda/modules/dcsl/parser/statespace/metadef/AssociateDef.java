/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;

import jda.modules.dcsl.syntax.DAssoc.Associate;

/**
 * @overview 
 *  An implementation of {@link Associate} to encapsulate property definitions extracted directly from 
 *  the source code.
 *   
 * @author dmle
 *
 * @version 3.4 
 */
public class AssociateDef extends MetaAttrDef implements Associate {
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "@Associate (type()=" + type() + ", cardMin()=" + cardMin()
        + ", cardMax()=" + cardMax() + ", determinant()=" + determinant()
        + ", updateLink()=" + updateLink() + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return Associate.class;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc.Associate#type()
   */
  @Override
  public Class type() {
    return (Class) propValMap.get("type");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc.Associate#cardMin()
   */
  @Override
  public int cardMin() {
    return (Integer) propValMap.get("cardMin");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc.Associate#cardMax()
   */
  @Override
  public int cardMax() {
    return (Integer) propValMap.get("cardMax");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc.Associate#determinant()
   */
  @Override
  public boolean determinant() {
    return (Boolean) propValMap.getOrDefault("determinant", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAssoc.Associate#updateLink()
   */
  @Override
  public boolean updateLink() {
    return (Boolean) propValMap.getOrDefault("updateLink", Boolean.TRUE);
  }
}
